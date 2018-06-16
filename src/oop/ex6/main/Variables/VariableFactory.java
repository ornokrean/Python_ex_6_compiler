package oop.ex6.main.Variables;

import oop.ex6.main.Compiler.BlockCompiler;

import java.util.regex.Matcher;
import java.util.regex.Pattern;


public class VariableFactory {
	private static final String BAD_VARIABLE_DECLERATION = "ERROR: Wrong type variable in line : ";

	public static scopeVariable variableFactory(boolean finalFlag, String type, String varName, String varValue, int lineNum) throws Exception {

		if (type.equals(typeCases.BOOLEAN.myType)) {
			booleanHelper(varValue);
			return new scopeVariable(finalFlag, varName, type, lineNum);


		} else if (type.equals(typeCases.INT.myType)) {
			intHelper(varValue);
			return new scopeVariable(finalFlag, varName, type, lineNum);


		} else if (type.equals(typeCases.DOUBLE.myType)) {
			doubleHelper(varValue);
			return new scopeVariable(finalFlag, varName, type, lineNum);


		} else if (type.equals(typeCases.STRING.myType)) {
			stringHelper(varValue);
			return new scopeVariable(finalFlag, varName, type, lineNum);

		} else if (type.equals(typeCases.CHAR.myType)) {
			charHelper(varValue);
			return new scopeVariable(finalFlag, varName, type, lineNum);
		} else {
			throw new Exception(BAD_VARIABLE_DECLERATION);
		}
	}

	private static int intHelper(String varValue) throws Exception {
		try {
			return Integer.parseInt(varValue);
		} catch (NumberFormatException e) {
			throw new Exception("bad int");
		}
	}

	private static double doubleHelper(String varValue) throws Exception {
		try {
			return Double.parseDouble(varValue);
		} catch (NumberFormatException e) {
			throw new Exception("bad double");
		}
	}

	private static Boolean booleanHelper(String varValue) throws Exception {
		Pattern p = Pattern.compile(BlockCompiler.BOOLEAN_VALUE);
		Matcher m = p.matcher(varValue);
		if (!(m.matches())) {
			throw new Exception("bad boolean");
		}

		if (varValue.equals("true") || varValue.equals("false")) {
			return Boolean.parseBoolean(varValue);
		} else {
			return (Double.parseDouble(varValue) != 0.0);
		}
	}

	private static String stringHelper(String varValue) throws Exception {
		Pattern p = Pattern.compile(BlockCompiler.STRING_VALUE);
		Matcher m = p.matcher(varValue);
		if (!(m.matches())) {
			throw new Exception("bad string");
		}

		// to check boundaries.
		return varValue.substring(1, varValue.length() - 1);

	}

	private static char charHelper(String varValue) throws Exception {
		Pattern p = Pattern.compile(BlockCompiler.CHAR_VALUE);
		Matcher m = p.matcher(varValue);
		if (!(m.matches())) {
			throw new Exception("bad char");
		}

		// to check boundaries.
		char charVal = varValue.charAt(1);
		return charVal;
	}

	enum typeCases {

		BOOLEAN("boolean"), INT("int"), DOUBLE("double"), STRING("String"), CHAR("char"), ISFINAL("final");

		private final String myType;

		typeCases(String string) {
			myType = string;
		}

	}
}
