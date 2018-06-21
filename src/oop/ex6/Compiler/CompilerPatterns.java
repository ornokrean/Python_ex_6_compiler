package oop.ex6.Compiler;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class CompilerPatterns {
	/**
	 * A class Containing Patterns and regex's and operates as a helper class for the compiler classes.
	 */

	/* ======================================== REGEXES ======================================== */

	public static final String ROUND_CLOSE = ")";
	public static final String OR_REGEX = "|";
	public static final String SPACES_REGEX = "[\\s]*";
	public static final String INT_VALUE_REGEX = "[-]?[0-9]+";
	public static final String DOUBLE_VALUE_REGEX = INT_VALUE_REGEX + "[.]?[0-9]*";
	public static final String BOOLEAN_VALUE_REGEX = "(true|false" + OR_REGEX + DOUBLE_VALUE_REGEX
			+ ROUND_CLOSE;
	public static final String STRING_VALUE_REGEX = "([\"][^\"]*[\"])";
	public static final String CHAR_VALUE_REGEX = "([\'][^\'][\'])";
	public static final String FUNC_DELIMITER = ",";
	public static final String BOOL_DELIMITER = "\\|\\||&&";
	public static final String NAME_VAR_REGEX = "([\\s]*(([a-zA-Z]|[_][\\w])[\\w]*)[\\s]*)";
	public static final String FUNC_DECLARATION = "[\\s]*(void)[\\s]*([a-zA-Z]+[\\w]*)[\\s]*[(].*[)" +
			"][\\s]*[{]";
	public static final String VAR_DECLARATION_TYPE_REGEX = "[\\s]*((final\\s)?[\\s]*" +
			"(int|double|char|boolean|String)" +
			"[\\s]+)";
	public static final String END_BLOCK_REGEX = "^[\\s]*}[\\s]*$";
	public static final String ASSIGNMENT_REGEX = "[\\s]*[=].*[;]";
	public static final String EVERYTHING_REGEX = ".*";
	public static final String EQUALS_REGEX = "[=]";
	public static final String BRACKET_CLOSE_REGEX = "}[\\s]*$";
	public static final String EMPTY_STR = "";
	public static final String CURLY_OPEN = "{";
	public static final String CURLY_CLOSE = "}";
	public static final String RETURN_REGEX = "[\\s]*(return)[\\s]*[;]";
	public static final String NOT_COMMENT_REGEX = "([^/]{2}.*|}|\\{)";
	public static final String IF_WHILE_REGEX = "^[\\s]*(if|while)[\\s]*[(].+[)][\\s]*[{]";
	public static final String FUNC_CALL = "([\\s]*)([a-zA-Z][\\w]*)[\\s]*[(].*[)][\\s]*(;)";
	public static final String CODE_REGEX = "[\\s]*(?:(?:void|if|while)[^{]*\\{|\\}|[^;]*[;][\\s]*)[\\s]*";
	public static final String BAD_COMMENT_REGEX = "([\\s].*|[/][*].*)";
	public static final String COMMENT_REGEX = "[\\s]*([/]|[/*])+.*";
	public static final String PRIMITIVE_VAL_REGEX = STRING_VALUE_REGEX + OR_REGEX + BOOLEAN_VALUE_REGEX
			+ OR_REGEX + CHAR_VALUE_REGEX;
	public static final String VAR_DECLARATION_START_ONLY_REGEX = VAR_DECLARATION_TYPE_REGEX + NAME_VAR_REGEX
			+ SPACES_REGEX;
	public static final String VAR_DECLARATION_START_REGEX = VAR_DECLARATION_START_ONLY_REGEX +
			EVERYTHING_REGEX;
	static final String ROUND_OPEN = "(";
	public static final String PRIMITIVE_DECLARATION_REGEX = ROUND_OPEN + NAME_VAR_REGEX + EQUALS_REGEX +
			SPACES_REGEX + ROUND_OPEN +
			PRIMITIVE_VAL_REGEX + ROUND_CLOSE + ROUND_CLOSE + SPACES_REGEX;
	public static final String INVALID_GLOBAL_CODE_REGEX = ROUND_OPEN + RETURN_REGEX + ROUND_CLOSE
			+ OR_REGEX + ROUND_OPEN + IF_WHILE_REGEX + ROUND_CLOSE + OR_REGEX
			+ ROUND_OPEN + FUNC_CALL + ROUND_CLOSE;
	public static final String NAME_AND_ASSIGNMENT_REGEX = NAME_VAR_REGEX + ASSIGNMENT_REGEX;
	public static final String ASSIGN_VAR_REGEX = NAME_VAR_REGEX + EQUALS_REGEX + NAME_VAR_REGEX;




	/* ======================================== Patterns ======================================== */
	public static final Pattern VAR_DECLARATION_START_PATTERN = Pattern.compile(VAR_DECLARATION_START_REGEX);
	public static final Pattern BOOLEAN_VALUE_PATTERN = Pattern.compile(BOOLEAN_VALUE_REGEX);
	public static final Pattern NAME_VAR_PATTERN = Pattern.compile(NAME_VAR_REGEX);
	public static final Pattern IF_WHILE_PATTERN = Pattern.compile(IF_WHILE_REGEX);
	public static final Pattern END_BLOCK_PATTERN = Pattern.compile(END_BLOCK_REGEX);
	public static final Pattern RETURN_PATTERN = Pattern.compile(RETURN_REGEX);
	public static final Pattern NAME_AND_ASSIGNMENT_PATTERN = Pattern.compile(NAME_AND_ASSIGNMENT_REGEX);
	public static final Pattern ASSIGN_VAR_PATTERN = Pattern.compile(ASSIGN_VAR_REGEX);
	public static final Pattern PRIMITIVE_VAL_PATTERN = Pattern.compile(PRIMITIVE_VAL_REGEX);
	public static final Pattern VAR_DECLARATION_START_ONLY_PATTERN =
			Pattern.compile(VAR_DECLARATION_START_ONLY_REGEX);
	public static final Pattern INT_VALUE_PATTERN = Pattern.compile(INT_VALUE_REGEX);
	public static final Pattern DOUBLE_VALUE_PATTERN = Pattern.compile(DOUBLE_VALUE_REGEX);
	public static final Pattern FUNC_DECLARATION_PATTERN = Pattern.compile(FUNC_DECLARATION);
	public static final Pattern FUNC_CALL_PATTERN = Pattern.compile(FUNC_CALL);
	public static final Pattern NOT_COMMENT_PATTERN = Pattern.compile(NOT_COMMENT_REGEX);
	public static final Pattern CODE_PATTERN = Pattern.compile(CODE_REGEX);
	public static final Pattern BAD_COMMENT_PATTERN = Pattern.compile(BAD_COMMENT_REGEX);
	public static final Pattern COMMENT_PATTERN = Pattern.compile(COMMENT_REGEX);
	public static final Pattern INVALID_GLOBAL_SCOPE_CODE_PATTERN =
			Pattern.compile(INVALID_GLOBAL_CODE_REGEX);
	public static final Pattern STRING_VALUE_PATTERN = Pattern.compile(STRING_VALUE_REGEX);
	public static final Pattern CHAR_VALUE_PATTERN = Pattern.compile(CHAR_VALUE_REGEX);
	public static final Pattern BRACKET_CLOSE_PATTERN = Pattern.compile(BRACKET_CLOSE_REGEX);
	public static final Pattern PRIMITIVE_DECLARATION_PATTERN = Pattern.compile(PRIMITIVE_DECLARATION_REGEX);


	/**
	 * A function that  receives a pattern and a String and returns a matcher for them.
	 *
	 * @param pattern A given Pattern.
	 * @param string  A given String.
	 * @return A matcher object.
	 */
	public static Matcher getMatcher(Pattern pattern, String string) {
		return pattern.matcher(string);
	}


}
