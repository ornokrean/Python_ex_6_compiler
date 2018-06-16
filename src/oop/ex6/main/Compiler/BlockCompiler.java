package oop.ex6.main.Compiler;


import oop.ex6.main.Variables.scopeVariable;

import java.io.IOException;
import java.util.HashMap;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import static oop.ex6.main.Variables.VariableFactory.variableFactory;

public class BlockCompiler extends FileCompiler {

	final static String FUNC_DELIMITER = ",";
	final static String BOOL_DELIMITER = "\\|\\||&&";
	private static final int NOT_ASSIGNED = -1;

	static final String NAME_VAR = "([\\s]*(([a-zA-Z]|[_][\\w])[\\w]*)[\\s]*)";
	//	private static final String VAR_ASSIGNMENT = NAME_VAR + "[\\s]+[=].*";
//	private static final String VAR_DECLERATION = "((final )?[\\s]*(int|double|char|boolean|String)[\\s]+)";


	public static final String BOOLEAN_VALUE = "(true|false|[-]?[0-9]+[.]?[0-9]*|[-]?[.][0-9]+)";
	public static final String STRING_VALUE = "([\"][^\"]*[\"])";
	public static final String CHAR_VALUE = "([\'][^\'][\'])";
	public static final String SOME_PRIMITIVE = "(" + NAME_VAR + "[=][\\s]*" +
			"(" + BOOLEAN_VALUE + "|" + CHAR_VALUE + "|" + STRING_VALUE + "))[\\s]*";
	//	String new11 = "("+NAME_VAR+"[=][\\s]*"+BOOLEAN_VALUE+"|"+CHAR_VALUE+"|"+STRING_VALUE+")";
	static HashMap<String, String[]> functionsList = new HashMap<>();
	protected FileCompiler myCompiler;
	boolean isFunctionBlock = false;
	int start;
	int end;
	HashMap<String, scopeVariable> scopeVariables = new HashMap<>();
	private BlockCompiler parentBlock = null;


	public BlockCompiler(int start, int end, FileCompiler myCompiler, Boolean isFunctionBlock) throws Exception {
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

	public BlockCompiler(int start, int end, FileCompiler myCompiler, BlockCompiler parentBlock, boolean isFunctionBlock)
			throws Exception {
		this(start, end, myCompiler, isFunctionBlock);
		this.parentBlock = parentBlock;
	}

	public void setEnd(int end) {
		this.end = end;
	}

	void setParentBlock(BlockCompiler parentBlock) {
		this.parentBlock = parentBlock;
		this.isFunctionBlock = true;
	}

	void initiateBlock() throws IOException, Exception {
		for (int i = start + 1; i < end; i++) {
			currentCodeLine = code.get(i);
			lineNum = i;
			compileLine(this);
		}

	}

	private void checkSignature() throws Exception {
		if (this.isFunctionBlock) {
			String funcDeclaration = this.code.get(this.start);
			String name = getFuncName(funcDeclaration);
			String[] vars = splitSignature(funcDeclaration, "(", ")", FUNC_DELIMITER);
			functionsList.put(name, vars);
			addFuncVars(vars);
		}
	}

	void addFuncVars(String[] vars) throws Exception {
		if (vars.length == 1 && vars[0].trim().equals(EMPTY_LINE)) {
			return;
		}
		for (String var : vars) {
			var = var.trim();
			if (vars.length > 1)
				checkEmptyVar(var, "Empty func call slot");
			String newVarName = declarationCallCase(var + ";", true,start);
			if (newVarName != null) {
				scopeVariable currVar = getVarInScope(newVarName);
				currVar.setAssigned(start);
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
			return;
		}
		throw new Exception("Invalid function call");
	}

	void checkFuncCallVars(String[] callVars, String[] validVars) throws Exception {
		if (callVars.length == 1 && callVars[0].trim().equals(EMPTY_LINE)) {
			return;
		}
		if (callVars.length != validVars.length) {
			throw new Exception("Invalid func call - not same length as signature");
		}
		for (int i = 0; i < validVars.length; i++) {
			checkEmptyVar(callVars[i], "Empty func call slot");
			declarationCallCase(validVars[i] + "=" + callVars[i], false, NOT_ASSIGNED);
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
		checkSignature();
		checkReturnStatement();
		//TODO check signature

		int i = this.start;
		if (isFunctionBlock) {
			i++;
		}
		for (BlockCompiler b : mySubBlocks) {
			while (i < b.start) {
				checkLine(i);
				i++;
			}
			i = b.end + 1;
		}
		while (i <= this.end) {
			checkLine(i);
			i++;
		}
		for (BlockCompiler subBlock : mySubBlocks) {
			subBlock.compile();
		}

	}

	private void checkLine(int lineNum) throws Exception {
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
		p = Pattern.compile("[\\s]*(return)[\\s]*[;]");
		m = p.matcher(line);
		if (m.matches()) {
			return;

		}
		if (declarationCallCase(line, true,lineNum) != null)
			return;


		// existing var usage call case.
		p = Pattern.compile(NAME_VAR + "[=].*[;]");
		m = p.matcher(line);
		if (m.matches()) {
			// notice we are sending the is final true by default but in this case it makes no difference since it is
			// not in use since the line type is null.
			varDeclarationCase(line, null, true, true,lineNum);
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

	private String declarationCallCase(String line, boolean insertVal,int lineNum) throws Exception {
		// var declaration call case.
		Pattern p = Pattern.compile("[\\s]*((final )?[\\s]*(int|double|char|boolean|String)[\\s]+)" + NAME_VAR + ".*");
		Matcher m = p.matcher(line);
		if (m.matches()) {
			String lineType = m.group(3); // getting the type of the declaration.
			boolean isFinal = false;
			if (m.group(2) != null) {
				isFinal = true;
			}
			varDeclarationCase(line, lineType, isFinal, insertVal,lineNum);
			// returns the name of the variable.
			return m.group(5).trim();
		}
		return null;
	}


	private void varDeclarationCase(String line, String lineType, boolean isFinal, boolean insertVal,int lineNum) throws Exception {
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
				String varName = m.group().trim();
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
					scopeVariables.put(m.group(1).trim(), new scopeVariable(isFinal, m.group(1).trim(), lineType,
							lineNum));
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
							existingVariableInScope.getName(), m.group(5),lineNum);
					existingVariableInScope.setAssigned(lineNum);
					// assigned!!!!!

					continue;
				} else {
					// group  here is the name, and group  here is the var assignment.
					scopeVariable result = variableFactory(isFinal, lineType, m.group(3).trim(), m.group(5),lineNum);
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
				scopeVariable assignedVar = getVarInScope(m.group(5).trim());
				if (assignedVar.isAssigned()) {
					if(!(myCompiler.globalScope.scopeVariables.containsKey(assignedVar.getName())|| assignedVar
							.getVarLineNum()< lineNum)){
						// checking the the variable declaration was done in the correct scope.
						throw new Exception("trying to assign a value with a value that has not been declared yet.");
					}


					// separating the existing var assignment and the regular one.
					if (lineType == null) {
						// we are checking if the assigned value is of the same type of the variable.
						variableFactory(existingVariableInScope.isFinal(), existingVariableInScope.getMyType(),
								existingVariableInScope.getName(), assignedVar.getDefaultVal(),lineNum);
						existingVariableInScope.setAssigned(lineNum);
							// assigned!!!!!
					} else {
						scopeVariable result = variableFactory(isFinal, lineType, m.group(2).trim(),
								assignedVar.getDefaultVal(),lineNum);
						if (insertVal) {
							scopeVariables.put(m.group(2).trim(), result);
						}
						continue;
					}
				}

			}
			throw new Exception("invalid assignment in line" + code.indexOf(line) + " of new variable with old one");


		}

	}


	private scopeVariable getVarInScope(String varName) {
		BlockCompiler currentBlock = this;
		while (currentBlock != null) {
			if (currentBlock.scopeVariables.containsKey(varName)) {
				return currentBlock.scopeVariables.get(varName);
			}
			currentBlock = currentBlock.parentBlock;
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
			p = Pattern.compile("[\\s]*(return)[\\s]*[;]");
			m = p.matcher(code.get(end - 1));

			if (!m.matches()) {
				throw new Exception("bad end of block");
			}
		}
	}


	String[] splitSignature(String signature, String start, String end, String delimiter) {
		if (start == null){
			start = EMPTY_LINE;
		}
		return signature.substring(signature.indexOf(start) + start.length(), signature.indexOf(end)).split
				(delimiter, -1);
	}

	String getFuncName(String line) throws Exception {
		Pattern p = Pattern.compile("[\\s]*(void)?[\\s]*([a-zA-Z]+[\\w]*).*");
		Matcher m = p.matcher(line);
		if (m.matches()) {
			return m.group(2).trim();
		}
		throw new Exception("illegal func name");
	}

	@Override
	public void compileLine(BlockCompiler parent) throws Exception {
		this.oldCurlyBracketCount = this.bracketsCount[0];
		changeCounter();
		// check for the new block indexes, if needed.
		this.newBlockHelper(this, false);
	}

}
