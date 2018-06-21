package oop.ex6.Compiler;

import oop.ex6.main.compilerExceptions.InvalidLineException;

import java.io.BufferedReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.regex.Matcher;


public class FileCompiler {
	/**
	 * A class that its purpose is to check if a given code in a given path compiles, and throw an error if it does not.
	 */

	static final String EMPTY_LINE = "";
	static final String BAD_COMMENT_MSG = "bad comment in line ";
	static final String BAD_CODE_SYNTAX_MSG = "bad code syntax in line ";
	static final String ILLEGAL_RETURN_MSG = "return statement at illegal location (global scope)";

	/**
	 * A data member of BlockCompiler type which represents the global scope of the code.
	 */
	static BlockCompiler globalScope;
	/**
	 * An array of BlockCompilers Containing all the Function Blocks.
	 */
	ArrayList<BlockCompiler> mySubBlocks = new ArrayList<>();

	/**
	 * A Array of size 2, of usage to count the number of brackets and by that create the subBlocks.
	 */
	int[] bracketsCount = {0, 0};

	/**
	 * An array list containing the code that was read and filtered from the path.
	 */
	ArrayList<String> code = new ArrayList<>();

	/**
	 * the previous curly brackets count also held in order to create the subBlocks.
	 */
	int oldCurlyBracketCount;
	/**
	 * the current line num we are at in the code read.
	 */
	int lineNum;
	/**
	 * the current code line we are using.
	 */
	String currentCodeLine;

	/**
	 * An int indicating the index of the blockStart.
	 */
	private int blockStartIndex;

	/**
	 * A  Hash set containing the line numbers of the block.
	 */
	private HashSet<Integer> myLines = new HashSet<>();
	;
	/**
	 * default constructor
	 */
	public FileCompiler() {
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

	/**
	 * this function checks for two booleans with OR operator, and throws an exception if one of them is
	 * true. it is a helper function to check that the counters are valid.
	 *
	 * @param b  the first condition
	 * @param b2 the second condition
	 * @throws Exception if on of the conditions matches, exception will be thrown.
	 */
	static void checkCounter(boolean b, boolean b2) throws InvalidLineException {
		if (b || b2)
			throw new InvalidLineException("problem with {}()");
		// TODO: lineNum used only for tests. please remove before submit;
	}

	/*
	 * this function checks if given line is valid according to the codeLinePattern and notCommentPattern
	 * @param line the line to check
	 * @return true iff the line is valid.
	 */
	private boolean validateLine(String line) throws InvalidLineException {
		Matcher codePattern = CompilerPatterns.CODE_PATTERN.matcher(line);
		Matcher comment = CompilerPatterns.COMMENT_PATTERN.matcher(line);
		Matcher badComment = CompilerPatterns.BAD_COMMENT_PATTERN.matcher(line);
		if (comment.matches()) { // if it is comment, lets check if the comment is valid:
			if (badComment.matches()) {//bad comment line. bye bye.
				throw new InvalidLineException(BAD_COMMENT_MSG + lineNum);
			}
		} else if (!codePattern.matches() && !line.trim().equals(EMPTY_LINE)) {
			//it has something inside but it isn't a code
			throw new InvalidLineException(BAD_CODE_SYNTAX_MSG + lineNum);
		} else { // return a code
			checkInvalidGlobalCode(line);
			return codePattern.matches() && !line.trim().equals(EMPTY_LINE);
		}
		return false;
	}

	/**
	 * A function checking that the lines written in the global scope are in the right format.
	 * @param line the current line.
	 * @throws InvalidLineException throws an invalid line exception in case of identifying an invalid line.
	 */
	private void checkInvalidGlobalCode(String line) throws InvalidLineException {
		if (bracketsCount[0] == 0) {
			Matcher invalidGlobalScopeCode = CompilerPatterns.INVALID_GLOBAL_SCOPE_CODE_PATTERN.matcher(line);
			if (!invalidGlobalScopeCode.matches()){
				this.myLines.add(this.lineNum);
			}else{
				throw new InvalidLineException(ILLEGAL_RETURN_MSG);
			}

		}
	}


	/**
	 * this function initiates the Compiler. it reads the given code via the code reader, checks for syntax
	 * errors, and adds the code to an ArrayList, and creates the code blocks accordingly.
	 *
	 * @param codeReader the BufferedReader to read the code from
	 * @throws IOException if there is a problem with the BufferedReader
	 * @throws Exception   if there is a syntax error
	 */
	void initiateCompiler(BufferedReader codeReader) throws IOException, InvalidLineException {
		globalScope = new BlockCompiler(0, -1, this, false);
		while ((currentCodeLine = codeReader.readLine()) != null) {
			if (validateLine(currentCodeLine)) {
				//this is a valid line, add it to the code:
				code.add(currentCodeLine.trim());
				// compile this line and it's sub-blocks:
				compileLine();
				lineNum++;
			}
		}
		globalScope.mySubBlocks = this.mySubBlocks;
		globalScope.setEnd(lineNum - 1);
		// check counter at the end
		checkCounter(bracketsCount[0] != 0, bracketsCount[1] != 0);


		// close the BufferedReader
		codeReader.close();
	}

    /**
     * The functions the actually compiles all the code after preparations.
     * @throws InvalidLineException
     */
	public void compile() throws InvalidLineException {

		for (BlockCompiler block : mySubBlocks) {
			block.checkSignature();
		}
		globalScope.compile();
	}

	/*
	 * this function changes the bracketsCount of the compiler if the currentCodeLine matches the
	 * pattern of row with parenthesis.
	 * @throws Exception if there's a problem with the counters, too many unmatched parenthesis - (below
	 * zero).
	 */
	void changeCounter() throws InvalidLineException {
		Matcher m2 = CompilerPatterns.NOT_COMMENT_PATTERN.matcher(this.currentCodeLine);
		if (!m2.matches())
			return;
		updateCounter();
		checkCounter(this.bracketsCount[0] < 0, this.bracketsCount[1] < 0);
	}


	/*
	 * this function updates each counter according to the number of parenthesis in the currentCodeLine.
	 */
	private void updateCounter() {
		int lineLen = this.currentCodeLine.length();

		this.bracketsCount[0] += lineLen - this.currentCodeLine.replace(CompilerPatterns.CURLY_OPEN, CompilerPatterns.EMPTY_STR).length();

		this.bracketsCount[0] -= lineLen - this.currentCodeLine.replace(CompilerPatterns.CURLY_CLOSE, CompilerPatterns.EMPTY_STR)
				.length();

		this.bracketsCount[1] += lineLen - this.currentCodeLine.replace(CompilerPatterns.ROUND_OPEN, CompilerPatterns.EMPTY_STR)
				.length();

		this.bracketsCount[1] -= lineLen - this.currentCodeLine.replace(CompilerPatterns.ROUND_CLOSE, CompilerPatterns.EMPTY_STR)
				.length();
	}

	/*
	 * this function creates an new block when needed: if the curly bracketsCounter spot went up from 0 to
	 * 1, we will save this line as the line the block started (blockStartIndex). then, after some line if
	 * the counter went down from 1 to 0, we will create the new block with this line as the last index and
	 * add it to the compiler's mySubBlocks ArrayList.
	 * @throws InvalidLineException if the block compiler constructor throws exception
	 */
	void newBlockHelper(BlockCompiler parent, boolean isFunctionBlock) throws InvalidLineException {
		if (this.oldCurlyBracketCount == 0 && this.bracketsCount[0] == 1) {
			// a new block is in the block!
			this.blockStartIndex = this.lineNum;
		} else if (this.oldCurlyBracketCount == 1 && this.bracketsCount[0] == 0) {
			//it is the end of the block:
			this.mySubBlocks.add(new BlockCompiler(this.blockStartIndex, this.lineNum, this,
					parent, isFunctionBlock));
		}
	}
	boolean containsLine(int line){
		return this.myLines.contains(line);
	}

	/**
	 * this function is like the "main" function of the class, it operates all the work that needs to be
	 * done and makes it into one function line. it will save the oldCurlyBracketCount, and run the change
	 * counter and will add a new block when needed.
	 * @throws InvalidLineException if one of the counters is below zero, an exception will be thrown.
	 */
	public void compileLine() throws InvalidLineException {
		this.oldCurlyBracketCount = this.bracketsCount[0];
		changeCounter();
		// check for the new block indexes, if needed.
		this.newBlockHelper(globalScope, true);
		//                globalScope
	}
}
