package oop.ex6.main.Compiler;


import oop.ex6.main.Variables.Variable;

import java.io.BufferedReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class FileCompiler {

	public static final String EMPTY_LINE = "";
	protected ArrayList<String> code = new ArrayList<>();

	protected HashSet<String> functionsList;

	protected HashSet<Variable> vars;

	private ArrayList<BlockCompiler> mySubBlocks = new ArrayList<>();

	private int[] parenthesisCounter = {0,0};

	private static final String CODE_REGEX = "(?:(?:(?:(?:void|if|while).*\\{)|\\}|.*[;]?))";
	private static final Pattern CODE_PATTERN = Pattern.compile(CODE_REGEX);

	private static final String NO_COMMENT_REGEX = "([^\\/]{2}.*|})";
	private static final Pattern NO_COMMENT_PATTERN = Pattern.compile(NO_COMMENT_REGEX);

	private static final String BAD_COMMENT_REGEX = "([\\s].*|[/*]+)";
	private static final Pattern BAD_COMMENT_PATTERN = Pattern.compile(BAD_COMMENT_REGEX);

	private static final String COMMENT_REGEX = "[\\s]*([/]|[/*])+.*";
	private static final Pattern COMMENT_PATTERN = Pattern.compile(COMMENT_REGEX);

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
	private boolean validateLine(String line) throws Exception {
		Matcher codePattern = CODE_PATTERN.matcher(line);
		Matcher comment = COMMENT_PATTERN.matcher(line);
		Matcher badComment = BAD_COMMENT_PATTERN.matcher(line);

		if (comment.matches()) { // if it is comment, lets check if the comment is valid:
			if (badComment.matches()){
				throw new Exception("bad comment in line "+ lineNum);
			}
		}else {
			return codePattern.matches() && !line.equals(EMPTY_LINE);
		}
		return false;
	}



	private void initiateCompiler(BufferedReader codeReader) throws IOException, Exception {
		String codeLine;

		while ((codeLine = codeReader.readLine()) != null) {
			lineNum++;// TODO: this is a variable used only for tests. please remove before submit;
			compileHelper.changeCounter(parenthesisCounter,codeLine);
			if (validateLine(codeLine)) {
				// we know the code is valid, no comment and can be any code line:

				code.add(codeLine.replace("\t",""));
			}
		}
		lineNum = -1;  // TODO: lineNum used only for tests. please remove before submit;

		//FIX for tests only, delete
		for (String c : code) {
			System.out.println(c);
		}

		// check counter at the end
		compileHelper.checkCounter(parenthesisCounter[0] != 0, parenthesisCounter[1] != 0);
		codeReader.close();
	}







	public void compile() throws Exception{
		mySubBlocks.add(new BlockCompiler(0,code.size(),this));

		for (BlockCompiler block : mySubBlocks) {
			block.compile();
		}
	}

}
