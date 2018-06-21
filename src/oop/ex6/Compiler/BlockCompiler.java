package oop.ex6.Compiler;

import oop.ex6.Variables.scopeVariable;
import oop.ex6.main.compilerExceptions.InvalidLineException;
import oop.ex6.main.compilerExceptions.InvalidNameException;
import oop.ex6.main.compilerExceptions.InvalidVariableUsageException;

import java.util.HashMap;
import java.util.HashSet;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import static oop.ex6.Variables.VariableFactory.variableFactory;

public class BlockCompiler extends FileCompiler {
	static final String SEMICOLON = ";";
	static final String DEFAULT_VAR_NAME = "varName";
	static final String NO_MATCH_FOR_LINE = "No match for line";
	static final String VAR_IN_SCOPE_ERROR = "declaring a var that is already in scope.";
	static final String FINAL_VAR_ASSIGNMENT_MSG = "trying to assign a variable that is final";
	static final String VAR_IS_NOT_DECLARED_USAGE_MSG = "trying to assign a value with a value that has not been declared yet.";
	static final String INVALID_ASSIGNMENT_OF_NEW_VARIABLE_WITH_OLD_ONE = "invalid assignment of new variable with old one";
	static final String INVALID_END_OF_BLOCK = "Invalid end of block";
	static final String ILLEGAL_FUNC_NAME = "illegal func name";
	static final String INVALID_VARIABLE_ASSIGNMENT = "invalid variable assignment.";
	static final String INVALID_FUNCTION_SIGNATURE = "Invalid function signature";
	static final String EMPTY_FUNC_CALL_SLOT = "Empty func call slot";
	static final String INVALID_FUNCTION_CALL = "Invalid function call";
	static final String EMPTY_BOOLEAN_SLOT = "Empty boolean slot";
	static final String INVALID_BOOLEAN_CONDITION = "invalid boolean condition";
	private static final int NOT_ASSIGNED = -1;
	static HashMap<String, String[]> functionsList = new HashMap<>();
	/**
	 * A reference to the super FileCompiler
	 */
	FileCompiler myCompiler;
	/**
	 * A boolean declaring if the block is a function block.
	 */
	private boolean isFunctionBlock = false;
	/**
	 * a int indicating the start of a block in the code array.
	 */
	private int start;
	/**
	 * a int indicating the end of a block in the code array.
	 */
	private int end;
	/**
	 * A Hash map containing the scope's variables.
	 */
	private HashMap<String, scopeVariable> scopeVariables = new HashMap<>();
	/**
	 * A  Hash set containing the line numbers of the block.
	 */
	private HashSet<Integer> myLines;
	/**
	 * a reference to the parent block.
	 */
	private BlockCompiler parentBlock = null;


	/**
	 * A constructor for BlockCompiler.
	 *
	 * @param start           a int indicating the start of a block in the code array.
	 * @param end             a int indicating the end of a block in the code array.
	 * @param myCompiler      A reference to the super FileCompiler
	 * @param isFunctionBlock A boolean declaring if the block is a function block.
	 * @throws InvalidLineException
	 */
	public BlockCompiler(int start, int end, FileCompiler myCompiler, Boolean isFunctionBlock) throws InvalidLineException {
		this.start = start;
		this.end = end;
		this.myCompiler = myCompiler;
		this.code = myCompiler.code;
		this.isFunctionBlock = isFunctionBlock;
		initiateBlock();
		myLines = new HashSet<>();
		// compile first line
	}

	/**
	 * A constructor allowing to set the parent block of the block.
	 *
	 * @param start       a int indicating the start of a block in the code array.
	 * @param end         a int indicating the end of a block in the code array.
	 * @param myCompiler  A reference to the super FileCompiler
	 * @param parentBlock a reference to the parent block.
	 * @throws InvalidLineException
	 */
	public BlockCompiler(int start, int end, FileCompiler myCompiler, BlockCompiler parentBlock) throws InvalidLineException {
		this(start, end, myCompiler, false);
		this.parentBlock = parentBlock;
	}

	/**
	 * A constructor allowing to create a function block and set a parent.
	 *
	 * @param start           a int indicating the start of a block in the code array.
	 * @param end             a int indicating the end of a block in the code array.
	 * @param myCompiler      A reference to the super FileCompiler
	 * @param parentBlock     a reference to the parent block.
	 * @param isFunctionBlock A boolean declaring if the block is a function block.
	 * @throws InvalidLineException
	 */
	public BlockCompiler(int start, int end, FileCompiler myCompiler, BlockCompiler parentBlock, boolean isFunctionBlock)
			throws InvalidLineException {
		this(start, end, myCompiler, isFunctionBlock);
		this.parentBlock = parentBlock;
	}

	/*
	 * A method that allows setting the end of a Block.
	 */
	void setEnd(int end) {
		this.end = end;
	}

	/*
	 * An initiator function that
	 * @throws InvalidLineException
	 */
	private void initiateBlock() throws InvalidLineException {
		for (int i = start + 1; i < end; i++) {
			currentCodeLine = code.get(i);
			lineNum = i;
			compileLine(this);
		}
	}

	void checkSignature() throws InvalidLineException {
		if (this.isFunctionBlock) {
			String funcDeclaration = this.code.get(this.start);
			String name = getFuncName(funcDeclaration, CompilerPatterns.FUNC_DECLARATION_PATTERN);
			String[] vars = splitSignature(funcDeclaration, CompilerPatterns.ROUND_OPEN, CompilerPatterns.ROUND_CLOSE, CompilerPatterns.FUNC_DELIMITER);
			addFuncVars(vars);

			if (!(vars.length == 1 && vars[0].trim().equals(EMPTY_LINE))) {
				for (int i = 0; i < vars.length; i++) {
					Matcher m = CompilerPatterns.getMatcher(CompilerPatterns.VAR_DECLARATION_PATTERN,vars[i]);
					if (m.matches()) {
						vars[i] = m.group(3).trim();
						continue;
					}
					throw new InvalidNameException(INVALID_FUNCTION_SIGNATURE);
				}
			}
			functionsList.put(name, vars);
		}
	}

	void addFuncVars(String[] vars) throws InvalidLineException {
		if (vars.length == 1 && vars[0].trim().equals(EMPTY_LINE)) {
			return;
		}
		for (String var : vars) {
			var = var.trim();
			if (vars.length > 1)
				checkEmptyVar(var, EMPTY_FUNC_CALL_SLOT);
			String newVarName = declarationCallCase(var + SEMICOLON, start);
			if (newVarName == null) {
				throw new InvalidNameException(INVALID_FUNCTION_SIGNATURE);
			}
		}
	}

	private void checkValidFuncCall(String line) throws InvalidLineException {
		String name = getFuncName(line, CompilerPatterns.FUNC_CALL_PATTERN);
		String[] callVars = splitSignature(line, CompilerPatterns.ROUND_OPEN, CompilerPatterns.ROUND_CLOSE, CompilerPatterns.FUNC_DELIMITER);
		if (functionsList.containsKey(name)) {
			String[] validVars = functionsList.get(name);
			checkFuncCallVars(callVars, validVars);
			return;
		}
		throw new InvalidLineException(INVALID_FUNCTION_CALL);
	}

	private void checkFuncCallVars(String[] callVars, String[] validVars) throws InvalidLineException {
		if (callVars.length != validVars.length) {
			throw new InvalidNameException(INVALID_FUNCTION_CALL);
		}
		if (callVars.length == 1 && validVars[0].equals(EMPTY_LINE)) {
			return;
		}
		Pattern p = Pattern.compile(CompilerPatterns.STRING_VALUE_REGEX + CompilerPatterns.OR_REGEX +
				CompilerPatterns.BOOLEAN_VALUE_REGEX + CompilerPatterns.OR_REGEX + CompilerPatterns
				.CHAR_VALUE_REGEX);
		Matcher m;

		for (int i = 0; i < validVars.length; i++) {
			callVars[i] = callVars[i].trim();
			m = p.matcher(callVars[i]);
			scopeVariable currVar = getVarInScope(callVars[i]);
			checkEmptyVar(callVars[i], EMPTY_FUNC_CALL_SLOT);
			if (m.matches()) {
				variableFactory(true, validVars[i], DEFAULT_VAR_NAME, callVars[i], 0);
			} else if (currVar == null || !(currVar.isAssigned())) {
				throw new InvalidVariableUsageException(INVALID_VARIABLE_ASSIGNMENT);
			} else {
				variableFactory(true, validVars[i], DEFAULT_VAR_NAME, currVar.getDefaultVal(), 0);
			}

		}
	}

	private void checkBooleanCall(String line, int lineNum) throws InvalidLineException {
		String[] checkVars = splitSignature(line, CompilerPatterns.ROUND_OPEN, CompilerPatterns.ROUND_CLOSE, CompilerPatterns.BOOL_DELIMITER);
		for (String var : checkVars) {
			checkEmptyVar(var, EMPTY_BOOLEAN_SLOT);
			Pattern p = Pattern.compile(CompilerPatterns.BOOLEAN_VALUE_REGEX);
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
			throw new InvalidLineException(INVALID_BOOLEAN_CONDITION);

		}

	}

	/**
	 * this function checks if the var given equals to EMPTY_LINE, meaning its an error, and throws an
	 * Exception with the message s iff the var is equals EMPTY_LINE
	 *
	 * @param var the var to check
	 * @param s   the message to throw.
	 * @throws InvalidLineException
	 */
	private void checkEmptyVar(String var, String s) throws InvalidLineException {
		if (var.equals(EMPTY_LINE)) {
			throw new InvalidLineException(s);
		}
	}


	@Override
	public void compile() throws InvalidLineException {
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

	private void checkLine(int lineNum) throws InvalidLineException {
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
		if (declarationCallCase(line, lineNum) != null)
			return;


		// existing var usage call case.
		p = Pattern.compile(CompilerPatterns.NAME_VAR + CompilerPatterns.ASSIGNMENT_REGEX);
		m = p.matcher(line);
		if (m.matches()) {
			// notice we are sending the is final true by default but in this case it makes no difference since it is
			// not in use since the line type is null.
			varDeclarationCase(line, null, true, lineNum);
			return;
		}
		// function call case
		p = Pattern.compile(CompilerPatterns.FUNC_CALL);
		m = p.matcher(line);
		if (m.matches()) {
			checkValidFuncCall(line);
			return;
		}

		throw new InvalidLineException(NO_MATCH_FOR_LINE);
	}

	private String declarationCallCase(String line, int lineNum) throws InvalidLineException {
		// var declaration call case.
		Pattern p = Pattern.compile(CompilerPatterns.VAR_DECLARATION_REGEX + CompilerPatterns.NAME_VAR + CompilerPatterns.EVERYTHING_REGEX);
		Matcher m = p.matcher(line);
		if (m.matches()) {
			String lineType = m.group(3); // getting the type of the declaration.
			boolean isFinal = false;
			if (m.group(2) != null) {
				isFinal = true;
			}
			varDeclarationCase(line, lineType, isFinal, lineNum);
			// returns the name of the variable.
			return m.group(5).trim();
		}
		return null;
	}

	private void varDeclarationCase(String line, String lineType, boolean isFinal, int lineNum) throws InvalidLineException {
		String[] varsDeclared = splitSignature(line, lineType, ";", ",");
		if (line == null && varsDeclared.length != 1) {
			throw new InvalidVariableUsageException(INVALID_VARIABLE_ASSIGNMENT);
		}
		for (String var : varsDeclared) {
			scopeVariable existingVariableInScope = null;
			checkEmptyVar(var, INVALID_VARIABLE_ASSIGNMENT);

			Pattern p = Pattern.compile(CompilerPatterns.NAME_VAR);
			Matcher m = p.matcher(var);

			// just declaration of a variable with no assignment.


			// this is a check that the variable assigned here has not been assigned in previous scopes.
			if (m.find()) {
				String varName = m.group().trim();
				existingVariableInScope = getVarInScope(varName);
				if (lineType != null && scopeVariables.containsKey(varName)) {
					throw new InvalidVariableUsageException(VAR_IN_SCOPE_ERROR);
				}


				// checking that in the case of declaring a variable that it does not exist in the scope.
				// checking that in the case of using a variable that it does exist in the scope.
				if (lineType == null) {
					if (existingVariableInScope == null)
						throw new InvalidVariableUsageException(VAR_IN_SCOPE_ERROR);
				}
			}

			if (m.matches()) {
					if (IsSignatureLine(lineNum)) {
						scopeVariables.put(m.group(1).trim(), new scopeVariable(isFinal, m.group(1).trim(),
								lineType, start));
					} else {
						scopeVariables.put(m.group(1).trim(), new scopeVariable(isFinal, m.group(1).trim(), lineType,
								NOT_ASSIGNED));
					}

				continue;
			}


			// checking that the existing variable is not final.
			if (existingVariableInScope != null && existingVariableInScope.isFinal()) {
				throw new InvalidVariableUsageException(FINAL_VAR_ASSIGNMENT_MSG);
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
						scopeVariables.put(result.getName(), result);

					continue;
				}
			}


			// need to check that the variable has not been assigned
			p = Pattern.compile(CompilerPatterns.NAME_VAR + CompilerPatterns.EQUALS_REGEX + CompilerPatterns.NAME_VAR);
			m = p.matcher(var);
			if (m.matches()) {

				//group  here is the name of the assigned-to variable, and group  is the new variable name.
				scopeVariable assignedVar = getVarInScope(m.group(5).trim());

				if (assignedVar != null && assignedVar.isAssigned()) {
					if (!globalScope.scopeVariables.containsValue(assignedVar) && (assignedVar
							.getVarLineNum() < start || assignedVar.getVarLineNum() > end) &&
							(assignedVar.getVarLineNum() > lineNum)) {
						throw new InvalidVariableUsageException(VAR_IS_NOT_DECLARED_USAGE_MSG);

					} else if (globalScope.scopeVariables.containsValue(assignedVar) && !globalScope
							.myLines.contains(assignedVar.getVarLineNum())) {
						throw new InvalidVariableUsageException(VAR_IS_NOT_DECLARED_USAGE_MSG);

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
							scopeVariables.put(m.group(2).trim(), result);
						continue;
					}
				}

			}
			throw new InvalidVariableUsageException(INVALID_ASSIGNMENT_OF_NEW_VARIABLE_WITH_OLD_ONE);


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


	private void checkReturnStatement() throws InvalidLineException {
		if (isFunctionBlock) {
			// check if lat row is "}"
			Pattern p = Pattern.compile(CompilerPatterns.BRACKET_CLOSE_REGEX);
			Matcher m = p.matcher(code.get(end));
			if (!m.matches()) {
				throw new InvalidLineException(INVALID_END_OF_BLOCK);
			}
			// check if one row before last contains "return;"
			p = Pattern.compile(CompilerPatterns.RETURN_REGEX);
			m = p.matcher(code.get(end - 1));

			if (!m.matches()) {
				throw new InvalidLineException(INVALID_END_OF_BLOCK);
			}
		}
	}


	private String[] splitSignature(String signature, String start, String end, String delimiter) {
		if (start == null) {
			start = EMPTY_LINE;
		}
		return signature.substring(signature.indexOf(start) + start.length(), signature.indexOf(end)).split
				(delimiter, -1);
	}

	private String getFuncName(String line, Pattern p) throws InvalidLineException {
		Matcher m = p.matcher(line);
		if (m.matches()) {
			return m.group(2).trim();
		}
		throw new InvalidNameException(ILLEGAL_FUNC_NAME);
	}

	@Override
	public void compileLine(BlockCompiler parent) throws InvalidLineException {
		this.oldCurlyBracketCount = this.bracketsCount[0];
		changeCounter();
		// check for the new block indexes, if needed.
		this.newBlockHelper(this, false);
	}

}
