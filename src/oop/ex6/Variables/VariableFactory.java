package oop.ex6.Variables;

import oop.ex6.Compiler.CompilerPatterns;
import oop.ex6.main.compilerExceptions.InvalidLineException;
import oop.ex6.main.compilerExceptions.InvalidVariableUsageException;

import java.util.regex.Matcher;


public class VariableFactory {
	static final String INVALID_DOUBLE_ASSIGNMENT = "Invalid Double assignment";
	static final String INVALID_BOOLEAN_ASSIGNMENT = "Invalid Boolean assignment";
	static final String INVALID_STRING_ASSIGNMENT = "Invalid String assignment";
	static final String INVALID_CHAR_ASSIGNMENT = "Invalid Char assignment";
	/**
	 * A class representing a Variable factory, its purpose is to create instances of a given parameters.
	 */

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
	public static scopeVariable variableFactory(boolean finalFlag, String type, String varName, String varValue,
	                                            int lineNum) throws InvalidLineException {
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
		return new scopeVariable(finalFlag, varName, type, lineNum);
	}

	/*
	 * A method the helps check if the int assignment in a value is valid.
	 */
	private static void intHelper(String varValue) throws InvalidVariableUsageException {
		Matcher m = CompilerPatterns.getMatcher(CompilerPatterns.INT_VALUE_PATTERN, varValue);
		if (!m.matches()) {
			throw new InvalidVariableUsageException(INVALID_DOUBLE_ASSIGNMENT);
		}
	}

	/*
	 * A method the helps check if the double assignment in a value is valid.
	 */
	private static void doubleHelper(String varValue) throws InvalidVariableUsageException {
		Matcher m = CompilerPatterns.getMatcher(CompilerPatterns.DOUBLE_VALUE_PATTERN, varValue);
		if (!m.matches()) {
			throw new InvalidVariableUsageException(INVALID_DOUBLE_ASSIGNMENT);
		}
	}

	/*
	 * A method the helps check if the boolean assignment in a value is valid.
	 */
	private static void booleanHelper(String varValue) throws InvalidVariableUsageException {
		Matcher m = CompilerPatterns.getMatcher(CompilerPatterns.BOOLEAN_VALUE_PATTERN, varValue);
		if (!(m.matches())) {
			throw new InvalidVariableUsageException(INVALID_BOOLEAN_ASSIGNMENT);
		}
	}

	/*
	 * A method the helps check if the string assignment in a value is valid.
	 */
	private static void stringHelper(String varValue) throws InvalidVariableUsageException {
		Matcher m = CompilerPatterns.getMatcher(CompilerPatterns.STRING_VALUE_PATTERN, varValue);
		if (!(m.matches())) {
			throw new InvalidVariableUsageException(INVALID_STRING_ASSIGNMENT);
		}
	}

	/*
	 * A method the helps check if the char assignment in a value is valid.
	 */
	private static void charHelper(String varValue) throws InvalidVariableUsageException {
		Matcher m = CompilerPatterns.getMatcher(CompilerPatterns.CHAR_VALUE_PATTERN, varValue);
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
