package oop.ex6.main.Compiler;


import oop.ex6.main.Variables.scopeVariable;

import java.io.IOException;
import java.util.HashMap;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import static oop.ex6.main.Variables.VariableFactory.variableFactory;

public class BlockCompiler extends FileCompiler {

	static final String NAME_VAR = "([\\s]*(([a-zA-Z]|[_][\\w])[\\w]*)[\\s]*)";
	//	private static final String VAR_ASSIGNMENT = NAME_VAR + "[\\s]+[=].*";
//	private static final String VAR_DECLERATION = "((final )?[\\s]*(int|double|char|boolean|String)[\\s]+)";
	private static final String BOOLEAN_VALUE = "(true|false|[-]?[0-9]+[.]?[0-9]*|[-]?[.][0-9]+)";
	private static final String STRING_VALUE = "([\"][^\"]*[\"])";
	private static final String CHAR_VALUE = "([\'][^\'][\'])";
	public static final String SOME_PRIMITIVE = "(" + NAME_VAR + "[=][\\s]*" +
			"(" + BOOLEAN_VALUE + "|" + CHAR_VALUE + "|" + STRING_VALUE + "))[\\s]*";
	//	String new11 = "("+NAME_VAR+"[=][\\s]*"+BOOLEAN_VALUE+"|"+CHAR_VALUE+"|"+STRING_VALUE+")";
	static HashMap<String, String[]> functionsList = new HashMap<>();
	protected FileCompiler myCompiler;
	boolean isFunctionBlock = false;
	int start;
	int end;
	HashMap<String, scopeVariable> scopeVariables = new HashMap<>();
	;
	private BlockCompiler parentBlock = null;


	public BlockCompiler(int start, int end, FileCompiler myCompiler, Boolean isFunctionBlock) throws Exception {
		this.compileHelper = new CompileHelper(this);
		this.start = start;
		this.end = end;
		this.myCompiler = myCompiler;
		this.code = myCompiler.code;
		this.isFunctionBlock = isFunctionBlock;
		initiateBlock();
		// compile first line
	}

	public BlockCompiler(int start, int end, FileCompiler myCompiler, BlockCompiler parentBlock) throws Exception {
		this(start, end, myCompiler, false);
		this.parentBlock = parentBlock;
	}

	void setParentBlock(BlockCompiler parentBlock) {
		this.parentBlock = parentBlock;
		isFunctionBlock = true;
	}

	void initiateBlock() throws IOException, Exception {
		for (int i = start + 1; i < end; i++) {
			currentCodeLine = code.get(i);
			lineNum = i;
			compileHelper.compileLine();
		}
		if (this.isFunctionBlock) {
			String funcDeclaration = this.code.get(this.start);
			String name = getFuncName(funcDeclaration);
			String[] vars = splitSignature(funcDeclaration, "(", ")", FUNC_DELIMITER);
			functionsList.put(name, vars);
			addFuncVars(vars);
		}
	}

	void addFuncVars(String[] vars) throws Exception {
		for (String var : vars) {
			if (vars.length > 1)
				checkEmptyVar(var, "Empty func call slot");
			String newVarName = declarationCallCase(var, true);
			if (newVarName != null) {
				scopeVariable currVar = getVarInScope(newVarName);
				currVar.setAssigned(true);
				continue;
			}
			throw new Exception("invalid parameter in function call");
		}
	}

	void checkValidFuncCall(String line) throws Exception {
		String name = getFuncName(line);
		String[] callVars = splitSignature(line, "(", ")", FUNC_DELIMITER);
		if (functionsList.containsKey(name)) {
			String[] validVars = functionsList.get(name);
			checkFuncCallVars(callVars, validVars);
		}
		throw new Exception("Invalid function call");
	}

	void checkFuncCallVars(String[] callVars, String[] validVars) throws Exception {
		if (callVars.length != validVars.length) {
			throw new Exception("Invalid func call - not same length as signature");
		}
		for (int i = 0; i < validVars.length; i++) {
			checkEmptyVar(callVars[i], "Empty func call slot");
			declarationCallCase(validVars[i] + "=" + callVars[i], false);
		}
	}

	void checkBooleanCall(String line) throws Exception {
		String[] checkVars = splitSignature(line, "(", ")", BOOL_DELIMITER);
		for (String var : checkVars) {
			checkEmptyVar(var, "Empty boolean slot");
			Pattern p = Pattern.compile(BOOLEAN_VALUE);
			Matcher m = p.matcher(var.trim());
			if (m.matches()) {
				continue;
			}
			p = Pattern.compile(NAME_VAR);
			m = p.matcher(var.trim());
			if (m.matches()) {
				scopeVariable currVar = getVarInScope(var.trim());
				if (currVar != null && (currVar.isBoolean())) {
					continue;
				}
			}
			throw new Exception("invalid boolean condition");

		}
	}

	/**
	 * this function checks if the var given equals to EMPTY_LINE, meaning its an error, and throws an
	 * Exception with the message s iff the var is equals EMPTY_LINE
	 *
	 * @param var the var to check
	 * @param s   the message to throw.
	 * @throws Exception
	 */
	private void checkEmptyVar(String var, String s) throws Exception {
		if (var.equals(EMPTY_LINE)) {
			throw new Exception(s);
		}
	}


	@Override
	public void compile() throws Exception {
		// first off we check if the last 2 lines contains the return and "}"  statement.
		checkReturnStatement();
		//TODO check signature

		int i = this.start;
		for (BlockCompiler b : mySubBlocks) {
			while (i < b.start) {
//				System.out.println("checking: " + code.get(i));
				getLineCase(i);
				i++;
			}
			i = b.end + 1;
		}
		while (i <= this.end) {
//			System.out.println("checking: " + code.get(i));
			getLineCase(i);
			i++;
		}
//		System.out.println(this.scopeVariables);

	}

	private void getLineCase(int lineNum) throws Exception {
		String line = code.get(lineNum);
		// if or while case.
		Pattern p = Pattern.compile("^[\\s]*(if|while)[\\s]*[(].+[)][\\s]*[{]");
		Matcher m = p.matcher(line);
		if (m.matches()) {
			checkBooleanCall(line);
			return;
		}


		// end of block case
		p = Pattern.compile("^[\\s]*}[\\s]*$");
		m = p.matcher(line);
		if (m.matches()) {
			return;
		}


		// return line case.
		p = Pattern.compile("(return;)[\\s]*");
		m = p.matcher(line);
		if (m.matches()) {
			return;

		}
		if (declarationCallCase(line, true) != null)
			return;


		// existing var usage call case.
		p = Pattern.compile(NAME_VAR + "[=].*[;]");
		m = p.matcher(line);
		if (m.matches()) {
			// notice we are sending the is final true by default but in this case it makes no difference since it is
			// not in use since the line type is null.
			varDeclarationCase(line, null, true, true);
			return;
		}


		// function call case
		p = Pattern.compile("[a-zA-Z][\\w]*[(].*[)][\\s]*(;|[{])");
		m = p.matcher(line);
		if (m.matches()) {
			checkValidFuncCall(line);
			return;
		}

		throw new Exception("No match for line");
	}

	private String declarationCallCase(String line, boolean insertVal) throws Exception {
		// var declaration call case.
		Pattern p = Pattern.compile("[\\s]*((final )?[\\s]*(int|double|char|boolean|String)[\\s]+)"+NAME_VAR+".*");
		Matcher m = p.matcher(line);
		if (m.matches()) {
			String lineType = m.group(3); // getting the type of the declaration.
			boolean isFinal = false;
			if (m.group(2) != null) {
				isFinal = true;
			}
			varDeclarationCase(line, lineType, isFinal, insertVal);
			// returns the name of the variable.
			return m.group(5);
		}
		return null;
	}


	private void varDeclarationCase(String line, String lineType, boolean isFinal, boolean insertVal) throws Exception {
		String[] varsDeclared = splitSignature(line, lineType, ";", ",");
		if (line == null && varsDeclared.length != 1) {
			throw new Exception("An invalid usage of a variable in one line.");
		}
		for (String var : varsDeclared) {
			scopeVariable existingVariableInScope = null;

			checkEmptyVar(var, "bad var assignment");

			Pattern p = Pattern.compile(NAME_VAR);
			Matcher m = p.matcher(var);
			// this is a check that the variable assigned here has not been assigned in previous scopes.
			if (m.find()) {
				String varName = m.group();
				existingVariableInScope = getVarInScope(varName);

				// checking that in the case of declaring a variable that it does not exist in the scope.
				// checking that in the case of using a variable that it does exist in the scope.
				if ((lineType == null) == (existingVariableInScope == null)) {
					throw new Exception("declaring a variable that has  already been declared.");
				}
			}

			// just declaration of a variable with no assignment.
			if (m.matches()) {
				if (insertVal) {
					scopeVariables.put(m.group(1), new scopeVariable(isFinal, m.group(1), lineType,
							false));
				}
				continue;
			}

			// checking that the existing variable is not final.
			if (existingVariableInScope != null && existingVariableInScope.isFinal()) {
				throw new Exception("trying to assign a variable that is final");
			}

			// an assignment of a variable with a primitive.
			p = Pattern.compile(SOME_PRIMITIVE);
			m = p.matcher(var);
			if (m.matches()) {
				// separating the existing var assignment and the regular one.
				if (lineType == null) {
					// we are checking if the assigned value is of the same type of the variable.
					variableFactory(existingVariableInScope.isFinal(), existingVariableInScope.getMyType(),
							existingVariableInScope.getName(), m.group(5));
				} else {
					// group  here is the name, and group  here is the var assignment.
					scopeVariable result = variableFactory(isFinal, lineType, m.group(3), m.group(5));
					if (insertVal) {
						scopeVariables.put(result.getName(), result);
					}
					continue;
				}
			}


			// need to check that the variable has not been assigned
			p = Pattern.compile(NAME_VAR + "[=]" + NAME_VAR);
			m = p.matcher(var);
			if (m.matches()) {

				//group  here is the name of the assigned-to variable, and group  is the new variable name.
				scopeVariable assignedVar = getVarInScope(m.group(5));
				if (assignedVar.isAssigned()) {

					// separating the existing var assignment and the regular one.
					if (lineType == null) {
						// we are checking if the assigned value is of the same type of the variable.
						variableFactory(existingVariableInScope.isFinal(), existingVariableInScope.getMyType(),
								existingVariableInScope.getName(), assignedVar.getDefaultVal());
					} else {
						scopeVariable result = variableFactory(isFinal, lineType, m.group(2), assignedVar.getDefaultVal());
						if (insertVal) {
							scopeVariables.put(m.group(2), result);
						}
						continue;
					}
				}
				break;

			}
			throw new Exception("invalid assignment in line" + code.indexOf(line) + " of new variable with old one");


		}

	}

	private scopeVariable getVarInScope(String varName) {
		BlockCompiler currentBlock = this;
		while (currentBlock != null) {
			if (currentBlock.scopeVariables.containsKey(varName)) {
				return scopeVariables.get(varName);
			}
			currentBlock = parentBlock;
		}
		return null;
	}


	private void checkReturnStatement() throws Exception {
		if (isFunctionBlock) {
			// check if lat row is "}"
			Pattern p = Pattern.compile("}[\\s]*$");
			Matcher m = p.matcher(code.get(end));
			if (!m.matches()) {
				throw new Exception("bad end of block");
			}
			// check if one row before last contains "return;"
			p = Pattern.compile("(return;)[\\s]*");
			m = p.matcher(code.get(end - 1));

			if (!m.matches()) {
				throw new Exception("bad end of block");
			}
		}
	}

}
