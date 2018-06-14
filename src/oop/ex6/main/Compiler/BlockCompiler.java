package oop.ex6.main.Compiler;


import oop.ex6.main.Variables.scopeVariable;

import java.io.IOException;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class BlockCompiler extends FileCompiler {

	static final String NAME_VAR = "[\\s]*(([a-zA-Z]*|[_])[\\w]+)[\\s]*";
	private static final String VAR_ASSIGNMENT = NAME_VAR + "[\\s]+[=].*";
	private static final String VAR_DECLERATION = "(final)?[\\s]+(int|double|char|boolean|String)[\\s]+";

	protected FileCompiler myCompiler;

	boolean isFunctionBlock = false;
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
		//check signature
		int i = start;
		while (i < end - 1) {
			i++;
			i = getLineCase(i);
			for (BlockCompiler subblock : mySubBlocks) {
				subblock.compile();
			}
		}
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

// (" + NAME_VAR + "[\\s]+[,]?)+[=].*"
		// var declaration call case.
		p = Pattern.compile("[\\s]*("+ VAR_DECLERATION + ")?");
		m = p.matcher(line);
		if (m.matches()) {
			String lineType = m.group(2); // getting the type of the declaration.
			boolean isfinal = false;
			if(m.group(1) != null){
				isfinal = true;
			}

			varDeclarationCase(line,lineType,isfinal);
			return 0;
		}

		// existing var usage call case.
		p = Pattern.compile(NAME_VAR + "[=].*[;]");
		m = p.matcher(line);
		if (m.matches()) {
			isVarUsageValid(line);
			return 0;
		}


		// function call case
		p = Pattern.compile("[a-zA-Z][\\w]*[(].*[)][\\s]*(;|[{])");
		m = p.matcher(line);
		if (m.matches()) {
			return 0;
		}

		throw new Exception("No match for line");
	}

	private void varDeclarationCase(String line,String lineType,boolean isfinal) {
		line = line.substring(line.indexOf(lineType),line.indexOf(";"));
		String[] varsDeclared = line.split(",");
		for (String var:varsDeclared
			 ) {

			Pattern p  = Pattern.compile(NAME_VAR);
			Matcher m  = p.matcher(line);
			if(m.matches()){
			    // need to check that the variable does'ent exist already.
				scopeVariables.add( new scopeVariable(isfinal,m.group(1),lineType,false));
				continue;
			}
			// need to check that the variable has not been assigned

            p  = Pattern.compile(NAME_VAR+"[=]"+NAME_VAR);
			m  = p.matcher(line);
            if(m.matches()){

            }

		}
	}

	private scopeVariable getVarInScope(String varName){
	   scopeVariable result;
	   BlockCompiler currentBlock = this;
	   while (this.parentBlock != null){
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
