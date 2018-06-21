package oop.ex6.Compiler;

import java.util.regex.Pattern;

public class CompilerPatterns {
	public static final String ROUND_CLOSE = ")";
	public static final String BOOLEAN_VALUE = "(true|false|[-]?[0-9]+[.]?[0-9]*)";
	public static final String STRING_VALUE_REGEX = "([\"][^\"]*[\"])";
	public static final String CHAR_VALUE = "([\'][^\'][\'])";
	public static final String OR_REGEX = "|";
	public static final String FUNC_DELIMITER = ",";
	public static final String BOOL_DELIMITER = "\\|\\||&&";
	public static final String NAME_VAR = "([\\s]*(([a-zA-Z]|[_][\\w])[\\w]*)[\\s]*)";
	public static final String FUNC_DECLARATION = "[\\s]*(void)[\\s]*([a-zA-Z]+[\\w]*)[\\s]*[(].*[)" +
			"][\\s]*[{]";
	public static final String VAR_DECLARATION_REGEX = "[\\s]*((final\\s)?[\\s]*" +
			"(int|double|char|boolean|String)" +
			"[\\s]+)";
	public static final String END_BLOCK_REGEX = "^[\\s]*}[\\s]*$";
	public static final String ASSIGNMENT_REGEX = "[\\s]*[=].*[;]";
	public static final String EVERYTHING_REGEX = ".*";
	public static final String EQUALS_REGEX = "[=]";
	public static final String BRACKET_CLOSE_REGEX = "}[\\s]*$";
	public static final String SPACES_REGEX = "[\\s]*";
	public static final String EMPTY_STR = "";
	public static final String CURLY_OPEN = "{";
	public static final String CURLY_CLOSE = "}";
	public static final String RETURN_REGEX = "[\\s]*(return)[\\s]*[;]";
	public static final String NOT_COMMENT_REGEX = "([^/]{2}.*|}|\\{)";
	public static final String IF_WHILE_REGEX = "^[\\s]*(if|while)[\\s]*[(].+[)][\\s]*[{]";
	public static final String FUNC_CALL = "([\\s]*)([a-zA-Z][\\w]*)[\\s]*[(].*[)][\\s]*(;)";
	public static final String CODE_REGEX = "[\\s]*(?:(?:(?:(?:void|if|while)[^{]*\\{)|\\}|[^;" +
			"]*[;][\\s]*))[\\s]*";
	public static final String BAD_COMMENT_REGEX = "([\\s].*|[/][*].*)";
	public static final String COMMENT_REGEX = "[\\s]*([/]|[/*])+.*";
	public static final Pattern CODE_PATTERN = Pattern.compile(CompilerPatterns.CODE_REGEX);
	public static final Pattern BAD_COMMENT_PATTERN = Pattern.compile(CompilerPatterns.BAD_COMMENT_REGEX);
	public static final Pattern COMMENT_PATTERN = Pattern.compile(CompilerPatterns.COMMENT_REGEX);
	static final String ROUND_OPEN = "(";
	public static final String SOME_PRIMITIVE = ROUND_OPEN + NAME_VAR + "[=][\\s]*" + ROUND_OPEN +
			BOOLEAN_VALUE + OR_REGEX + CHAR_VALUE + OR_REGEX + STRING_VALUE_REGEX + ROUND_CLOSE + ROUND_CLOSE + "[\\s]*";

}