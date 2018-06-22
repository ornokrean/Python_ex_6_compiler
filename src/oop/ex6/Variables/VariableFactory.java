package oop.ex6.Variables;

import oop.ex6.CompilerExceptions.InvalidLineException;
import oop.ex6.CompilerExceptions.InvalidVariableUsageException;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * A class representing a Variable factory, its purpose is to create instances of a given parameters.
 */
public class VariableFactory {
	private static final String INVALID_DOUBLE_ASSIGNMENT = "Invalid Double assignment";
	private static final String INVALID_BOOLEAN_ASSIGNMENT = "Invalid Boolean assignment";
	private static final String INVALID_STRING_ASSIGNMENT = "Invalid String assignment";
	private static final String INVALID_CHAR_ASSIGNMENT = "Invalid Char assignment";
	private static final String INT_VALUE_REGEX = "[-]?[0-9]+";
	private static final String DOUBLE_VALUE_REGEX = INT_VALUE_REGEX + "[.]?[0-9]*";
	private static final String BOOLEAN_VALUE_REGEX = "(true|false|"  + DOUBLE_VALUE_REGEX+ ")";
	private static final String STRING_VALUE_REGEX = "([\"][^\"]*[\"])";
	private static final String CHAR_VALUE_REGEX = "([\'][^\'][\'])";
	private static final Pattern STRING_VALUE_PATTERN = Pattern.compile(STRING_VALUE_REGEX);
	private static final Pattern CHAR_VALUE_PATTERN = Pattern.compile(CHAR_VALUE_REGEX);
	private static final Pattern INT_VALUE_PATTERN = Pattern.compile(INT_VALUE_REGEX);
	private static final Pattern DOUBLE_VALUE_PATTERN = Pattern.compile(DOUBLE_VALUE_REGEX);
	private static final Pattern BOOLEAN_VALUE_PATTERN = Pattern.compile(BOOLEAN_VALUE_REGEX);

	private static final String BAD_VARIABLE_DECLARATION = "ERROR: Wrong type variable in line : ";

	/**
	 * A factory method that creates a relevant variable by the parameters given.
	 *
	 * @param finalFlag A boolean representing if the variable is final.
	 * @param type      A string representation of the variable type.
	 * @param varName   A string representation of the variable name.
	 * @param varValue  A string representation of the variable value.
	 * @param lineNum   an int of the line the variable was created in.
	 * @return A variable.
	 * @throws InvalidLineException an exception that is thrown in case of invalid parameters.
	 */
	public static ScopeVariable variableFactory(boolean finalFlag, String type, String varName,
	                                            String varValue, int lineNum) throws InvalidLineException {
		if (type.equals(typeCases.BOOLEAN.myType)) {
			booleanHelper(varValue);
		} else if (type.equals(typeCases.INT.myType)) {
			intHelper(varValue);
		} else if (type.equals(typeCases.DOUBLE.myType)) {
			doubleHelper(varValue);
		} else if (type.equals(typeCases.STRING.myType)) {
			stringHelper(varValue);
		} else if (type.equals(typeCases.CHAR.myType)) {
			charHelper(varValue);
		} else {
			throw new InvalidLineException(BAD_VARIABLE_DECLARATION);
		}
		return new ScopeVariable(finalFlag, varName, type, lineNum);
	}

	/*
	 * A method the helps check if the int assignment in a value is valid.
	 */
	private static void intHelper(String varValue) throws InvalidVariableUsageException {
		Matcher m = INT_VALUE_PATTERN.matcher(varValue);
		if (!m.matches()) {
			throw new InvalidVariableUsageException(INVALID_DOUBLE_ASSIGNMENT);
		}
	}

	/*
	 * A method the helps check if the double assignment in a value is valid.
	 */
	private static void doubleHelper(String varValue) throws InvalidVariableUsageException {
		Matcher m = DOUBLE_VALUE_PATTERN.matcher(varValue);
		if (!m.matches()) {
			throw new InvalidVariableUsageException(INVALID_DOUBLE_ASSIGNMENT);
		}
	}

	/*
	 * A method the helps check if the boolean assignment in a value is valid.
	 */
	private static void booleanHelper(String varValue) throws InvalidVariableUsageException {
		Matcher m = BOOLEAN_VALUE_PATTERN.matcher(varValue);
		if (!(m.matches())) {
			throw new InvalidVariableUsageException(INVALID_BOOLEAN_ASSIGNMENT);
		}
	}

	/*
	 * A method the helps check if the string assignment in a value is valid.
	 */
	private static void stringHelper(String varValue) throws InvalidVariableUsageException {
		Matcher m = STRING_VALUE_PATTERN.matcher(varValue);
		if (!(m.matches())) {
			throw new InvalidVariableUsageException(INVALID_STRING_ASSIGNMENT);
		}
	}

	/*
	 * A method the helps check if the char assignment in a value is valid.
	 */
	private static void charHelper(String varValue) throws InvalidVariableUsageException {
		Matcher m = CHAR_VALUE_PATTERN.matcher(varValue);
		if (!(m.matches())) {
			throw new InvalidVariableUsageException(INVALID_CHAR_ASSIGNMENT);
		}
	}

	/*
	 * A enum class representing the different types of variables available.
	 */
	private enum typeCases {
		BOOLEAN("boolean"), INT("int"), DOUBLE("double"), STRING("String"), CHAR("char");
		private final String myType;

		typeCases(String string) {
			myType = string;
		}
	}
}
