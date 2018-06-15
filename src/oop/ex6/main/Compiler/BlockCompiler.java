package oop.ex6.main.Compiler;


import oop.ex6.main.Variables.scopeVariable;

import java.io.IOException;
import java.util.HashMap;
import java.util.HashSet;
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
	//	private static final String SOME_PRIMITIVE = "(" + NAME_VAR + "[=][\\s]*" + "" + BOOLEAN_VALUE + "|" +
//			CHAR_VALUE + "|" + STRING_VALUE + ")";
	private static final String SOME_PRIMITIVE = "(" + NAME_VAR + "[=][\\s]*" +
			"(" + BOOLEAN_VALUE + "|" + CHAR_VALUE + "|" + STRING_VALUE + "))[\\s]*";
//	String new11 = "("+NAME_VAR+"[=][\\s]*"+BOOLEAN_VALUE+"|"+CHAR_VALUE+"|"+STRING_VALUE+")";


	protected FileCompiler myCompiler;

	boolean isFunctionBlock = false;
	int start;
	int end;
	HashMap<String, scopeVariable> scopeVariables = new HashMap<>();
	HashSet<String> functionsList;
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
	}

	void initiateBlock() throws IOException, Exception {
		for (int i = start + 1; i < end; i++) {
			currentCodeLine = code.get(i);
			lineNum = i;
			compileHelper.compileLine();
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

	private int subBlockGeneretor(int lineNumber) throws Exception {
		int startOfSubblock = lineNumber;
//		CompileHelper.changeCounter(bracketsCount, code.get(lineNumber));
		while (bracketsCount[0] != 0) {
			lineNumber++;
//			CompileHelper.changeCounter(bracketsCount, code.get(lineNumber));
		}
		mySubBlocks.add(new BlockCompiler(startOfSubblock, lineNumber, myCompiler, this));
		return lineNumber;
	}

	private void isBooleanConditionValid(String line) throws Exception {
		throw new Exception("bad boolean input");
	}

	private void isVarUsageValid(String line) throws Exception {

		throw new Exception("bad variable usage input");
	}

	private int getLineCase(int lineNum) throws Exception {
		String line = code.get(lineNum);
		// if or while case.
		Pattern p = Pattern.compile("^[\\s]*(if|while)[\\s]*[(].+[)][\\s]*[{]");
		Matcher m = p.matcher(line);
		if (m.matches()) {
			return subBlockGeneretor(lineNum);
		}


		// end of block case
		p = Pattern.compile("^[\\s]*}[\\s]*$");
		m = p.matcher(line);
		if (m.matches()) {
			return lineNum;
		}


		// return line case.
		p = Pattern.compile("(return;)[\\s]*");
		m = p.matcher(line);
		if (m.matches()) {
			return lineNum;
		}

		// var declaration call case.
//		p = Pattern.compile("[\\s]*"+ VAR_DECLERATION + "");
		p = Pattern.compile("[\\s]*((final )?[\\s]*(int|double|char|boolean|String)[\\s]+).*");
		m = p.matcher(line);
		if (m.matches()) {
			String lineType = m.group(3); // getting the type of the declaration.
			boolean isFinal = false;
			if (m.group(2) != null) {
				isFinal = true;
			}
			varDeclarationCase(line, lineType, isFinal);
			return lineNum;
		}

		// existing var usage call case.
		p = Pattern.compile(NAME_VAR + "[=].*[;]");
		m = p.matcher(line);
		if (m.matches()) {
			isVarUsageValid(line);
			return lineNum;
		}


		// function call case
		p = Pattern.compile("[a-zA-Z][\\w]*[(].*[)][\\s]*(;|[{])");
		m = p.matcher(line);
		if (m.matches()) {
			return lineNum;
		}

		throw new Exception("No match for line");
	}

	private void varDeclarationCase(String line, String lineType, boolean isFinal) throws Exception {
		String[] varsDeclared = splitSignature(line, lineType, ";", ",");

		for (String var : varsDeclared) {
			if (var.equals("")) {
				throw new Exception("bad var assignment");
			}

			Pattern p = Pattern.compile(NAME_VAR);
			Matcher m = p.matcher(var);

			// this is a check that the variable assigned here hass not been assigned in previous scopes.
			if(m.find()){
				String varName = m.group();
				scopeVariable s = getVarInScope(varName);
				if(s != null){ throw new Exception("declaring a variable that has  already been declared."); }
			}


			if (m.matches()) {

				scopeVariables.put(m.group(1), new scopeVariable(isFinal, m.group(1), lineType, false));
				continue;
			}


			p = Pattern.compile(SOME_PRIMITIVE);
			m = p.matcher(var);
			if (m.matches()) {

				// group  here is the name, and group  here is the var assignment.
				scopeVariable result = variableFactory(isFinal, lineType, m.group(3), m.group(5));
				scopeVariables.put(result.getName(), result);
				continue;
			}



			// need to check that the variable has not been assigned
			p = Pattern.compile(NAME_VAR + "[=]" + NAME_VAR);
			m = p.matcher(var);
			if (m.matches()) {

				//group  here is the name of the assigned-to variable, and group  is the new variable name.
				scopeVariable assignedVar = getVarInScope(m.group(5));
				if (assignedVar.isAssigned()) {
					scopeVariable result = variableFactory(isFinal, lineType, m.group(2), assignedVar.getDefaultVal());
					scopeVariables.put(m.group(2),result);
					continue;
				}
				break;

			}
			throw new Exception("invalid assignment in line" + code.indexOf(line) + " of new variable with old one");


		}

	}

	private scopeVariable getVarInScope(String varName)  {
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
