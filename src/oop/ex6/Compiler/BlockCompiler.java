package oop.ex6.Compiler;

import oop.ex6.Variables.scopeVariable;

import java.io.IOException;
import java.util.HashMap;
import java.util.HashSet;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import static oop.ex6.Variables.VariableFactory.variableFactory;

public class BlockCompiler extends FileCompiler {
	static final String SEMICOLON = ";";
	static final String DEFAULT_VAR_NAME = "varName";
	private static final int NOT_ASSIGNED = -1;
	private static final Pattern FUNC_DECLARATION_PATTERN = Pattern.compile(CompilerPatterns.FUNC_DECLARATION);
	private static final Pattern FUNC_CALL_PATTERN = Pattern.compile(CompilerPatterns.FUNC_CALL);
	static HashMap<String, String[]> functionsList = new HashMap<>();
	protected FileCompiler myCompiler;
	boolean isFunctionBlock = false;
	int start;
	int end;
	HashMap<String, scopeVariable> scopeVariables = new HashMap<>();
	HashSet<Integer> myLines;
	private BlockCompiler parentBlock = null;

	public BlockCompiler(int start, int end, FileCompiler myCompiler, Boolean isFunctionBlock) throws Exception {
		this.start = start;
		this.end = end;
		this.myCompiler = myCompiler;
		this.code = myCompiler.code;
		this.isFunctionBlock = isFunctionBlock;
		initiateBlock();
		myLines = new HashSet<>();
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

	void checkSignature() throws Exception {
		if (this.isFunctionBlock) {
			String funcDeclaration = this.code.get(this.start);
			String name = getFuncName(funcDeclaration, FUNC_DECLARATION_PATTERN);
			String[] vars = splitSignature(funcDeclaration, CompilerPatterns.ROUND_OPEN, CompilerPatterns.ROUND_CLOSE, CompilerPatterns.FUNC_DELIMITER);
			addFuncVars(vars);

			if (!(vars.length == 1 && vars[0].trim().equals(EMPTY_LINE))) {
				for (int i = 0; i < vars.length; i++) {
					Pattern p = Pattern.compile(CompilerPatterns.VAR_DECLARATION_REGEX + CompilerPatterns.NAME_VAR + CompilerPatterns.SPACES_REGEX);
					Matcher m = p.matcher(vars[i]);
					if (m.matches()) {
						vars[i] = m.group(3).trim();
						continue;
					}
					throw new Exception("bad function signature");
				}
			}
			functionsList.put(name, vars);
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
			String newVarName = declarationCallCase(var + SEMICOLON, true, start);
			if (newVarName == null) {
				throw new Exception("invalid parameter in function call");
			}
		}
	}

	void checkValidFuncCall(String line) throws Exception {
		String name = getFuncName(line, FUNC_CALL_PATTERN);
		String[] callVars = splitSignature(line, CompilerPatterns.ROUND_OPEN, CompilerPatterns.ROUND_CLOSE, CompilerPatterns.FUNC_DELIMITER);
		if (functionsList.containsKey(name)) {
			String[] validVars = functionsList.get(name);
			checkFuncCallVars(callVars, validVars);
			return;
		}
		throw new Exception("Invalid function call");
	}

	void checkFuncCallVars(String[] callVars, String[] validVars) throws Exception {
		if (callVars.length != validVars.length) {
			throw new Exception("Invalid func call - not same length as signature");
		}
		if (callVars.length == 1 && validVars[0].equals(EMPTY_LINE)) {
			return;
		}
		Pattern p = Pattern.compile(CompilerPatterns.STRING_VALUE_REGEX + CompilerPatterns.OR_REGEX + CompilerPatterns.BOOLEAN_VALUE + CompilerPatterns.OR_REGEX + CompilerPatterns.CHAR_VALUE);
		Matcher m;

		for (int i = 0; i < validVars.length; i++) {
			callVars[i] = callVars[i].trim();
			m = p.matcher(callVars[i]);
			scopeVariable currVar = getVarInScope(callVars[i]);
			checkEmptyVar(callVars[i], "Empty func call slot");
			if (m.matches()) {
				variableFactory(true, validVars[i], DEFAULT_VAR_NAME, callVars[i], 0);
			} else if (currVar == null || !(currVar.isAssigned())) {
				throw new Exception("invalid variable assignment.");
			} else {
				variableFactory(true, validVars[i], DEFAULT_VAR_NAME, currVar.getDefaultVal(), 0);
			}

		}
	}

	void checkBooleanCall(String line, int lineNum) throws Exception {
		String[] checkVars = splitSignature(line, CompilerPatterns.ROUND_OPEN, CompilerPatterns.ROUND_CLOSE, CompilerPatterns.BOOL_DELIMITER);
		for (String var : checkVars) {
			checkEmptyVar(var, "Empty boolean slot");
			Pattern p = Pattern.compile(CompilerPatterns.BOOLEAN_VALUE);
			Matcher m = p.matcher(var.trim());
			if (m.matches()) {
				continue;
			}
			p = Pattern.compile(CompilerPatterns.NAME_VAR);
			m = p.matcher(var.trim());
			if (m.matches()) {
				scopeVariable currVar = getVarInScope(var.trim());
				if (currVar != null && (currVar.isBoolean()) && (currVar.getVarLineNum() < lineNum ||
						globalScope.scopeVariables.containsValue(currVar))) {
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
		myLines.add(lineNum);
		// if or while case.
		Pattern p = Pattern.compile(CompilerPatterns.IF_WHILE_REGEX);
		Matcher m = p.matcher(line);
		if (m.matches()) {
			checkBooleanCall(line, lineNum);
			return;
		}


		// end of block case
		p = Pattern.compile(CompilerPatterns.END_BLOCK_REGEX);
		m = p.matcher(line);
		if (m.matches()) {
			return;
		}


		// return line case.
		p = Pattern.compile(CompilerPatterns.RETURN_REGEX);
		m = p.matcher(line);
		if (m.matches()) {
			return;

		}
		if (declarationCallCase(line, true, lineNum) != null)
			return;


		// existing var usage call case.
		p = Pattern.compile(CompilerPatterns.NAME_VAR + CompilerPatterns.ASSIGNMENT_REGEX);
		m = p.matcher(line);
		if (m.matches()) {
			// notice we are sending the is final true by default but in this case it makes no difference since it is
			// not in use since the line type is null.
			varDeclarationCase(line, null, true, true, lineNum);
			return;
		}
		// function call case
		p = Pattern.compile(CompilerPatterns.FUNC_CALL);
		m = p.matcher(line);
		if (m.matches()) {
			checkValidFuncCall(line);
			return;
		}

		throw new Exception("No match for line");
	}

	private String declarationCallCase(String line, boolean insertVal, int lineNum) throws Exception {
		// var declaration call case.
		Pattern p = Pattern.compile(CompilerPatterns.VAR_DECLARATION_REGEX + CompilerPatterns.NAME_VAR + CompilerPatterns.EVERYTHING_REGEX);
		Matcher m = p.matcher(line);
		if (m.matches()) {
			String lineType = m.group(3); // getting the type of the declaration.
			boolean isFinal = false;
			if (m.group(2) != null) {
				isFinal = true;
			}
			varDeclarationCase(line, lineType, isFinal, insertVal, lineNum);
			// returns the name of the variable.
			return m.group(5).trim();
		}
		return null;
	}


	private void varDeclarationCase(String line, String lineType, boolean isFinal, boolean insertVal, int lineNum) throws Exception {
		String[] varsDeclared = splitSignature(line, lineType, ";", ",");
		if (line == null && varsDeclared.length != 1) {
			throw new Exception("An invalid usage of a variable in one line.");
		}
		for (String var : varsDeclared) {
			scopeVariable existingVariableInScope = null;
			checkEmptyVar(var, "bad var assignment");

			Pattern p = Pattern.compile(CompilerPatterns.NAME_VAR);
			Matcher m = p.matcher(var);

			// just declaration of a variable with no assignment.


			// this is a check that the variable assigned here has not been assigned in previous scopes.
			if (m.find()) {
				String varName = m.group().trim();
				existingVariableInScope = getVarInScope(varName);
				if (lineType != null && scopeVariables.containsKey(varName)) {
					throw new Exception("declaring a var that is already in scope.");
				}


				// checking that in the case of declaring a variable that it does not exist in the scope.
				// checking that in the case of using a variable that it does exist in the scope.
				if (lineType == null) {
					if (existingVariableInScope == null)
						throw new Exception("declaring a variable that has  already been declared.");
				}
			}

			if (m.matches()) {
				if (insertVal) {
					if (IsSignatureLine(lineNum)) {
						scopeVariables.put(m.group(1).trim(), new scopeVariable(isFinal, m.group(1).trim(),
								lineType, start));
					} else {
						scopeVariables.put(m.group(1).trim(), new scopeVariable(isFinal, m.group(1).trim(), lineType,
								NOT_ASSIGNED));
					}
				}
				continue;
			}


			// checking that the existing variable is not final.
			if (existingVariableInScope != null && existingVariableInScope.isFinal()) {
				throw new Exception("trying to assign a variable that is final");
			}

			// an assignment of a variable with a primitive.
			p = Pattern.compile(CompilerPatterns.SOME_PRIMITIVE);
			m = p.matcher(var);
			if (m.matches()) {
				// separating the existing var assignment and the regular one.
				if (lineType == null) {
					// we are checking if the assigned value is of the same type of the variable.
					variableFactory(existingVariableInScope.isFinal(), existingVariableInScope.getMyType(),
							existingVariableInScope.getName(), m.group(5), lineNum);
					existingVariableInScope.setAssigned(lineNum);
					// assigned!!!!!

					continue;
				} else {
					// group  here is the name, and group  here is the var assignment.
					scopeVariable result = variableFactory(isFinal, lineType, m.group(3).trim(), m.group(5), lineNum);
					if (insertVal) {
						scopeVariables.put(result.getName(), result);
					}
					continue;
				}
			}


			// need to check that the variable has not been assigned
			p = Pattern.compile(CompilerPatterns.NAME_VAR + CompilerPatterns.EQUALS_REGEX + CompilerPatterns.NAME_VAR);
			m = p.matcher(var);
			if (m.matches()) {

				//group  here is the name of the assigned-to variable, and group  is the new variable name.
				scopeVariable assignedVar = getVarInScope(m.group(5).trim());

				if (assignedVar.isAssigned()) {
					if (!globalScope.scopeVariables.containsValue(assignedVar) && (assignedVar
							.getVarLineNum() < start || assignedVar.getVarLineNum() > end) &&
							(assignedVar.getVarLineNum() > lineNum)) {
						throw new Exception("trying to assign a value with a value that has not been declared yet.");

					} else if (globalScope.scopeVariables.containsValue(assignedVar) && !globalScope
							.myLines.contains(assignedVar.getVarLineNum())) {
						throw new Exception("trying to assign a value with a value that has not been declared yet.");

					}
					// separating the existing var assignment and the regular one.
					if (lineType == null) {
						// we are checking if the assigned value is of the same type of the variable.
						variableFactory(existingVariableInScope.isFinal(), existingVariableInScope.getMyType(),
								existingVariableInScope.getName(), assignedVar.getDefaultVal(), lineNum);
						existingVariableInScope.setAssigned(lineNum);
						// assigned!!!!!
					} else {
						scopeVariable result = variableFactory(isFinal, lineType, m.group(2).trim(),
								assignedVar.getDefaultVal(), lineNum);
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

	private boolean IsSignatureLine(int lineNum) {
		return lineNum == start && isFunctionBlock;
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
			Pattern p = Pattern.compile(CompilerPatterns.BRACKET_CLOSE_REGEX);
			Matcher m = p.matcher(code.get(end));
			if (!m.matches()) {
				throw new Exception("bad end of block");
			}
			// check if one row before last contains "return;"
			p = Pattern.compile(CompilerPatterns.RETURN_REGEX);
			m = p.matcher(code.get(end - 1));

			if (!m.matches()) {
				throw new Exception("bad end of block");
			}
		}
	}


	String[] splitSignature(String signature, String start, String end, String delimiter) {
		if (start == null) {
			start = EMPTY_LINE;
		}
		return signature.substring(signature.indexOf(start) + start.length(), signature.indexOf(end)).split
				(delimiter, -1);
	}

	String getFuncName(String line, Pattern p) throws Exception {
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