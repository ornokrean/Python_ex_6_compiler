package oop.ex6.main.Compiler;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class CompileHelper {
	static final String EMPTY_STR = "";
	static final String CURLY_OPEN = "{";
	static final String CURLY_CLOSE = "}";
	static final String ROUND_OPEN = "(";
	static final String ROUND_CLOSE = ")";
	private FileCompiler compiler;

	CompileHelper(FileCompiler comp) {
		this.compiler = comp;
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
	 * this function changes the bracketsCount of the compiler if the currentCodeLine matches the
	 * pattern of row with parenthesis.
	 * @throws Exception if there's a problem with the counters, too many unmatched parenthesis - (below
	 * zero).
	 */
	private void changeCounter() throws Exception {
		Pattern notCommentPattern = Pattern.compile("([^/]{2}.*|}|\\{)");
		Matcher m2 = notCommentPattern.matcher(compiler.currentCodeLine);
		if (!m2.matches())
			return;
		updateCounter();
		checkCounter(compiler.bracketsCount[0] < 0, compiler.bracketsCount[1] < 0);
		// TODO: lineNum used only for tests. please remove before submit;

	}

	/*
	 * this function updates each counter according to the number of parenthesis in the currentCodeLine.
	 */
	private void updateCounter() {
		int lineLen = compiler.currentCodeLine.length();

		compiler.bracketsCount[0] += lineLen - compiler.currentCodeLine.replace(CURLY_OPEN, EMPTY_STR).length();

		compiler.bracketsCount[0] -= lineLen - compiler.currentCodeLine.replace(CURLY_CLOSE, EMPTY_STR)
				.length();

		compiler.bracketsCount[1] += lineLen - compiler.currentCodeLine.replace(ROUND_OPEN, EMPTY_STR)
				.length();

		compiler.bracketsCount[1] -= lineLen - compiler.currentCodeLine.replace(ROUND_CLOSE, EMPTY_STR)
				.length();
	}

	/*
	 * this function creates an new block when needed: if the curly bracketsCounter spot went up from 0 to
	 * 1, we will save this line as the line the block started (blockStartIndex). then, after some line if
	 * the counter went down from 1 to 0, we will create the new block with this line as the last index and
	 * add it to the compiler's mySubBlocks ArrayList.
	 * @throws Exception
	 */
	private void newBlockHelper() throws Exception {
		if (compiler.oldCurlyBracketCount == 0 && compiler.bracketsCount[0] == 1) {
			// a new block is in the block!
			compiler.blockStartIndex = compiler.lineNum;
		} else if (compiler.oldCurlyBracketCount == 1 && compiler.bracketsCount[0] == 0) {
			//it is the end of the block:
			compiler.mySubBlocks.add(new BlockCompiler(compiler.blockStartIndex, compiler.lineNum, compiler,
					(BlockCompiler) this.compiler));
		}
	}

	/**
	 * this function is like the "main" function of the class, it operates all the work that needs to be
	 * done and makes it into one function line. it will save the oldCurlyBracketCount, and run the change
	 * counter and will add a new block when needed.
	 * @throws Exception if one of the counters is below zero, an exception will be thrown.
	 */
	public void compileLine() throws Exception {
		compiler.oldCurlyBracketCount = compiler.bracketsCount[0];
		changeCounter();
		// check for the new block indexes, if needed.
		this.newBlockHelper();
	}


}
