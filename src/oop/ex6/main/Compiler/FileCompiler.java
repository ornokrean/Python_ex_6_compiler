package oop.ex6.main.Compiler;


import oop.ex6.main.Variables.Variable;

import java.io.BufferedReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
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
	//	private static final String NO_COMMENT_REGEX = "([^\\/]{2}.*|})";
//	private static final Pattern NO_COMMENT_PATTERN = Pattern.compile(NO_COMMENT_REGEX);
	HashSet<Variable> variables;
	ArrayList<BlockCompiler> mySubBlocks = new ArrayList<>();
	int[] bracketsCount = {0, 0};
	ArrayList<String> code = new ArrayList<>();
	HashSet<String> functionsList;
	String currentCodeLine;
	int oldCurlyBracketCount;
	int lineNum;
	int blockStartIndex;
	CompileHelper compileHelper;

	/**
	 * default constructor
	 */
	public FileCompiler() {
		this.compileHelper = new CompileHelper(this);
	}

	/**
	 * constructor for the first compiler, gets the BufferedReader
	 *
	 * @param codeReader the BufferedReader to read the code from
	 * @throws IOException if there is a problem with the BufferedReader
	 * @throws Exception   if there is a syntax error.
	 */
	public FileCompiler(BufferedReader codeReader) throws IOException, Exception {
		this();
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

	/**
	 * this function initiates the Compiler. it reads the given code via the code reader, checks for syntax
	 * errors, and adds the code to an ArrayList, and creates the code blocks accordingly.
	 *
	 * @param codeReader the BufferedReader to read the code from
	 * @throws IOException if there is a problem with the BufferedReader
	 * @throws Exception   if there is a syntax error
	 */
	void initiateCompiler(BufferedReader codeReader) throws IOException, Exception {

		// FIX TODO FIX what to do with globals? which block will handle?
		while ((currentCodeLine = codeReader.readLine()) != null) {
			if (validateLine(currentCodeLine)) {
				//this is a valid line, add it to the code:
				code.add(currentCodeLine.replace("\t", ""));
				// compile this line and it's sub-blocks:
				compileHelper.compileLine();
				lineNum++;
			}
		}
		// check counter at the end
		CompileHelper.checkCounter(bracketsCount[0] != 0, bracketsCount[1] != 0);
		// close the BufferedReader
		codeReader.close();


		// fix for test only
		System.out.println(this);
	}


	public void  compile() throws Exception {
//		mySubBlocks.add(new BlockCompiler(1,8,this));


//		for (BlockCompiler block : mySubBlocks) {
//			block.compile();
//		}
	}


	// fix for test only
	@Override
	public String toString() {
		String out = "";
		//FIX for tests only, delete
		lineNum = 0;
		for (String c : code) {
			out += lineNum + ":|    " + c + "\n";
			lineNum++;
		}
		return "\nFileCompiler: " +
				"\nvariables = " + variables +
//				",\nmySubBlocks=" + mySubBlocks +
				",\nbracketsCount = " + Arrays.toString(bracketsCount) +
//				", code=" + code +
				",\nfunctionsList = " + functionsList +
				",\noldCurlyBracketCount = " + oldCurlyBracketCount +
				",\ncompileHelper = " + compileHelper +
				"\nCode:\n" + out;
	}
}
