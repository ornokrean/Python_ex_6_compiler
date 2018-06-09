package oop.ex6.main.Compiler;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class compileHelper {

	static void changeCounter(int[] parenthesisCounter, String line) throws Exception {
		Pattern notCommentPattern = Pattern.compile("([^/]{2}.*|}|\\{)");
		Matcher m2 = notCommentPattern.matcher(line);
		if (!m2.matches())
			return;
		updateCounter(parenthesisCounter, line);
		checkCounter(parenthesisCounter[0] < 0, parenthesisCounter[1] < 0);
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

}
