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
	private static final String CODE_REGEX = "[\\s]*(?:(?:(?:(?:void|if|while).*\\{)|\\}|.*[;]))";
	private static final Pattern CODE_PATTERN = Pattern.compile(CODE_REGEX);
	private static final String BAD_COMMENT_REGEX = "([\\s].*|[/*]+)";
	private static final Pattern BAD_COMMENT_PATTERN = Pattern.compile(BAD_COMMENT_REGEX);
	private static final String COMMENT_REGEX = "[\\s]*([/]|[/*])+.*";
	private static final Pattern COMMENT_PATTERN = Pattern.compile(COMMENT_REGEX);




	protected ArrayList<String> code = new ArrayList<>();
	protected HashSet<String> functionsList;

	String codeLine;
	int parBefore;
	int lineNum;
	int blockStart;

	compileHelper compileHelper = new compileHelper(this);



	//	private static final String NO_COMMENT_REGEX = "([^\\/]{2}.*|})";
//	private static final Pattern NO_COMMENT_PATTERN = Pattern.compile(NO_COMMENT_REGEX);
	protected HashSet<Variable> vars;
	protected ArrayList<BlockCompiler> mySubBlocks = new ArrayList<>();
	protected int[] parenthesisCounter = {0, 0};


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
			if (badComment.matches()) {//bad comment line. bye bye.
				throw new Exception("bad comment in line " + lineNum);
			}
		} else if (!codePattern.matches() && !line.equals(EMPTY_LINE)) {
			//it has something inside but it isn't a code
			throw new Exception("bad code syntax in line " + lineNum);
		} else { // return a code
			return codePattern.matches() && !line.equals(EMPTY_LINE);
			//return !line.equals(EMPTY_LINE);
		}//fix: check with tomer if there's need for double return (?)
		return false;
	}


	private void initiateCompiler(BufferedReader codeReader) throws IOException, Exception {

		// FIX TODO FIX what to do with globals? which block will handle?
		while ((codeLine = codeReader.readLine()) != null) {
			if (validateLine(codeLine)) {
				//this is a valid line
				compileHelper.compileLine();

				// we know the code is valid, no comment and can be any code line:
				code.add(codeLine.replace("\t", ""));
				lineNum++;
			}
		}

		//FIX for tests only, delete
		lineNum = 0;
		for (String c : code) {
			System.out.println(lineNum+":|    "+c);
			lineNum++;
		}

		// check counter at the end
		compileHelper.checkCounter(parenthesisCounter[0] != 0, parenthesisCounter[1] != 0);
		codeReader.close();
	}








	public void compile() throws Exception{
//		mySubBlocks.add(new BlockCompiler(1,8,this));


		for (BlockCompiler block : mySubBlocks) {
			block.compile();
		}
	}

}
