package oop.ex6.main.Compiler;


import java.io.BufferedReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class FileCompiler {
	public static final String EMPTY_LINE = "";
	static final String EMPTY_STR = "";
	static final String CURLY_OPEN = "{";
	static final String CURLY_CLOSE = "}";
	static final String ROUND_OPEN = "(";
	static final String ROUND_CLOSE = ")";
	static final String TAB_CHAR = "\t";
	//todo this is for the if inside, returns an array of the conditions.

	private static final String CODE_REGEX = "[\\s]*(?:(?:(?:(?:void|if|while)[^{]*\\{)|\\}|[^;]*[;][\\s]*))";
	private static final Pattern CODE_PATTERN = Pattern.compile(CODE_REGEX);
	private static final String BAD_COMMENT_REGEX = "([\\s].*|[/][*].*)";
	private static final Pattern BAD_COMMENT_PATTERN = Pattern.compile(BAD_COMMENT_REGEX);
	private static final String COMMENT_REGEX = "[\\s]*([/]|[/*])+.*";
	private static final Pattern COMMENT_PATTERN = Pattern.compile(COMMENT_REGEX);

	ArrayList<BlockCompiler> mySubBlocks = new ArrayList<>();
	int[] bracketsCount = {0, 0};
	ArrayList<String> code = new ArrayList<>();
	String currentCodeLine;
	int oldCurlyBracketCount;
	int lineNum;
	int blockStartIndex;
	BlockCompiler globalScope;


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
	static void checkCounter(boolean b, boolean b2) throws Exception {
		if (b || b2)
			throw new Exception("problem with {}()");
		// TODO: lineNum used only for tests. please remove before submit;
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
		} else if (!codePattern.matches() && !line.trim().equals(EMPTY_LINE)) {
			//it has something inside but it isn't a code
			throw new Exception("bad code syntax in line " + lineNum);
		} else { // return a code
			return codePattern.matches() && !line.trim().equals(EMPTY_LINE);
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
		this.globalScope = new BlockCompiler(0, -1, this, false);
		// FIX TODO FIX what to do with globals? which block will handle?
		while ((currentCodeLine = codeReader.readLine()) != null) {
			if (validateLine(currentCodeLine)) {
				//this is a valid line, add it to the code:
				code.add(currentCodeLine.replace(TAB_CHAR, ""));
				// compile this line and it's sub-blocks:
				compileLine(this.globalScope);
				lineNum++;
			}
		}
		this.globalScope.mySubBlocks = this.mySubBlocks;
		this.globalScope.setEnd(lineNum - 1);
		// check counter at the end
		checkCounter(bracketsCount[0] != 0, bracketsCount[1] != 0);

		// close the BufferedReader
		codeReader.close();
	}

	public void compile() throws Exception {
		this.globalScope.compile();
	}

	/*
	 * this function changes the bracketsCount of the compiler if the currentCodeLine matches the
	 * pattern of row with parenthesis.
	 * @throws Exception if there's a problem with the counters, too many unmatched parenthesis - (below
	 * zero).
	 */
	 void changeCounter() throws Exception {
		Pattern notCommentPattern = Pattern.compile("([^/]{2}.*|}|\\{)");
		Matcher m2 = notCommentPattern.matcher(this.currentCodeLine);
		if (!m2.matches())
			return;
		updateCounter();
		checkCounter(this.bracketsCount[0] < 0, this.bracketsCount[1] < 0);
		// TODO: lineNum used only for tests. please remove before submit;

	}


	/*
	 * this function updates each counter according to the number of parenthesis in the currentCodeLine.
	 */
	private void updateCounter() {
		int lineLen = this.currentCodeLine.length();

		this.bracketsCount[0] += lineLen - this.currentCodeLine.replace(CURLY_OPEN, EMPTY_STR).length();

		this.bracketsCount[0] -= lineLen - this.currentCodeLine.replace(CURLY_CLOSE, EMPTY_STR)
				.length();

		this.bracketsCount[1] += lineLen - this.currentCodeLine.replace(ROUND_OPEN, EMPTY_STR)
				.length();

		this.bracketsCount[1] -= lineLen - this.currentCodeLine.replace(ROUND_CLOSE, EMPTY_STR)
				.length();
	}

	/*
	 * this function creates an new block when needed: if the curly bracketsCounter spot went up from 0 to
	 * 1, we will save this line as the line the block started (blockStartIndex). then, after some line if
	 * the counter went down from 1 to 0, we will create the new block with this line as the last index and
	 * add it to the compiler's mySubBlocks ArrayList.
	 * @throws Exception if the block compiler constructor throws exception
	 */
	 void newBlockHelper(BlockCompiler parent, boolean isFunctionBlock) throws Exception {
		if (this.oldCurlyBracketCount == 0 && this.bracketsCount[0] == 1) {
			// a new block is in the block!
			this.blockStartIndex = this.lineNum;
		} else if (this.oldCurlyBracketCount == 1 && this.bracketsCount[0] == 0) {
			//it is the end of the block:
			this.mySubBlocks.add(new BlockCompiler(this.blockStartIndex, this.lineNum, this,
					parent,isFunctionBlock));
		}
	}

	/**
	 * this function is like the "main" function of the class, it operates all the work that needs to be
	 * done and makes it into one function line. it will save the oldCurlyBracketCount, and run the change
	 * counter and will add a new block when needed.
	 *
	 * @throws Exception if one of the counters is below zero, an exception will be thrown.
	 */
	public void compileLine(BlockCompiler parent) throws Exception {
		this.oldCurlyBracketCount = this.bracketsCount[0];
		changeCounter();
		// check for the new block indexes, if needed.
		this.newBlockHelper(parent,true);
	}


}
