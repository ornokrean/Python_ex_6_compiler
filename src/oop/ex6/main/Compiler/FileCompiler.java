package oop.ex6.main.Compiler;


import oop.ex6.main.Variables.Variable;

import java.io.BufferedReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class FileCompiler {

	protected ArrayList<String> code = new ArrayList<>();

	protected HashSet<String> functionsList;

	protected HashSet<Variable> vars;

	private BlockCompiler[] mySubBlocks;

	private static int[] parenthesisCounter = {0,0};

	private static final String CODE_REGEX = "(?:(?:(?:(?:void|if|while).*\\{)|\\}|.*[;]?))";
	private static final Pattern CODE_PATTERN = Pattern.compile(CODE_REGEX);

	private static final String NO_COMMENT_REGEX = "([^\\/]{2}.*|})";
	private static final Pattern NO_COMMENT_PATTERN = Pattern.compile(NO_COMMENT_REGEX);

	private static int lineNum;// TODO: this is a variable used only for tests. please remove before submit;


	public FileCompiler() {

	}

	public FileCompiler(BufferedReader codeReader) throws IOException, Exception {
		initiateCompiler(codeReader);

	}

	/*
	 * this function checks if given line is valid according to the codeLinePattern and notCommentPattern
	 * @param line the line to check
	 * @return true iff the line is valid.
	 */
	private boolean validateLine(String line) {
		Matcher codePattern = CODE_PATTERN.matcher(line);
		Matcher noComment = NO_COMMENT_PATTERN.matcher(line);
		if (codePattern.matches()) { // if it is comment, lets check if the comment is valid:
			return noComment.matches();
		}

//		Matcher m2 = notCommentPattern.matcher(line);

		return true;
	}



	private void initiateCompiler(BufferedReader codeReader) throws IOException, Exception {
		String codeLine;

		while ((codeLine = codeReader.readLine()) != null) {
			lineNum++;// TODO: this is a variable used only for tests. please remove before submit;
			changeCounter(codeLine);
			if (validateLine(codeLine)) {
				code.add(codeLine);
			}
		}
		lineNum = -1;  // TODO: lineNum used only for tests. please remove before submit;

		//FIX for tests only
		for (String c : code) {
			System.out.println(c);
		}

		// check counter at the end
		checkCounter(parenthesisCounter[0] != 0, parenthesisCounter[1] != 0);
		codeReader.close();
	}





	private void changeCounter(String line) throws Exception {
		Pattern notCommentPattern = Pattern.compile("([^/]{2}.*|}|\\{)");
		Matcher m2 = notCommentPattern.matcher(line);
		if (!m2.matches())
			return;
		updateCounter(line);
		checkCounter(parenthesisCounter[0] < 0, parenthesisCounter[1] < 0);
		// TODO: lineNum used only for tests. please remove before submit;

	}

	private void checkCounter(boolean b, boolean b2) throws Exception {
		if (b || b2)
			throw new Exception("problem with {}() in line number: " + lineNum);
							// TODO: lineNum used only for tests. please remove before submit;
	}

	private void updateCounter(String line) {
		parenthesisCounter[0] += line.length() - line.replace("{", "").length();
		parenthesisCounter[0] -= line.length() - line.replace("}", "").length();
		parenthesisCounter[1] += line.length() - line.replace("(", "").length();
		parenthesisCounter[1] -= line.length() - line.replace(")", "").length();
	}


	public void compile() throws Exception{


//		for (BlockCompiler block : mySubBlocks) {
//			block.compile();
//		}
	}

}
