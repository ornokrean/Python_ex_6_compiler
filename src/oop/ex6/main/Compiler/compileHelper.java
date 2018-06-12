package oop.ex6.main.Compiler;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class compileHelper {
	private FileCompiler compiler;
	compileHelper(FileCompiler comp) {
		this.compiler = comp;
	}

	private void changeCounter() throws Exception {
		Pattern notCommentPattern = Pattern.compile("([^/]{2}.*|}|\\{)");
		Matcher m2 = notCommentPattern.matcher(compiler.currentCodeLine);
		if (!m2.matches())
			return;
		updateCounter(compiler.parenthesisCounter, compiler.currentCodeLine);
		checkCounter(compiler.parenthesisCounter[0] < 0, compiler.parenthesisCounter[1] < 0);
		// TODO: lineNum used only for tests. please remove before submit;

	}

	static void checkCounter(boolean b, boolean b2) throws Exception {
		if (b || b2)
			throw new Exception("problem with {}()");
		// TODO: lineNum used only for tests. please remove before submit;
	}
	private static void updateCounter(int[] parenthesisCounter, String line) {
		parenthesisCounter[0] += line.length() - line.replace("{", "").length();
		parenthesisCounter[0] -= line.length() - line.replace("}", "").length();
		parenthesisCounter[1] += line.length() - line.replace("(", "").length();
		parenthesisCounter[1] -= line.length() - line.replace(")", "").length();
	}
	private void newBlockHelper() throws Exception {
		if (compiler.parentesisCountBefore == 0 && compiler.parenthesisCounter[0] == 1) {
			// a new block is in the block!
			compiler.blockStartIndex = compiler.lineNum;
		} else if (compiler.parentesisCountBefore == 1 && compiler.parenthesisCounter[0] == 0) {
			//it is the end of the block:
			compiler.mySubBlocks.add(new BlockCompiler(compiler.blockStartIndex,compiler.lineNum,compiler,true));

		}
	}
	
	public void compileLine() throws Exception{
		compiler.parentesisCountBefore = compiler.parenthesisCounter[0];
		changeCounter();
		// check for the new block indexes, if needed.
		this.newBlockHelper();
	}

	
	
	
	
	// (([a-zA-Z]*|[_])[\w]+)[\s]+[=].*) regex for var name.



	//	static Variable checkVariableAssignment(String line){
//		// is it a new variable declaration?
//		Pattern p  = Pattern.compile("(final)?[\\s]*(int|double|char|boolean|String)[\\s].*|");
//		Matcher m = p.matcher(line);
//		if(m.matches()){
//
//		}
//		return null;
//	}

}
