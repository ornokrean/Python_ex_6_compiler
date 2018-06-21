//package oop.ex6.Variables;
//
//public abstract class ScopeVariable {
//	boolean isFinal;
//	String name;
//
//
//	public ScopeVariable() {
//	}
//
//	public ScopeVariable(boolean isFinal, String name) {
//		this.isFinal = isFinal;
//		this.name = name;
//	}
//}

package oop.ex6.Variables;

import oop.ex6.main.compilerExceptions.InvalidVariableUsageException;

public class ScopeVariable {
	/**
	 * A class representing a variable in a scope of a block.
	 */
	/*
	 * A constant representing a value of an declared but not assigned variable.
	 */
	private static final int NOT_ASSIGNED = -1;
	/**
	 * a flag representing is the variable final.
	 */
	private boolean isFinal;
	/**
	 * a string representing the name of the variable
	 */
	private String name;
	/**
	 * a string representing the type of the variable.
	 */
	private String myType;
	/**
	 * A data member holding the line in which the variable was assigned , NOT_ASSIGNED otherwise.
	 */
	private int isAssigned = NOT_ASSIGNED;

	/**
	 * A constructor for the scope variable.
	 *
	 * @param isFinal a flag representing is the variable final.
	 * @param name    a string representing the name of the variable.
	 * @param type    a string representing the type of the variable.
	 */
	public ScopeVariable(boolean isFinal, String name, String type) {
		this.isFinal = isFinal;
		this.name = name;
		this.myType = type;
	}

	/**
	 * A constructor for the scope variable.
	 *
	 * @param isFinal    a flag representing is the variable final.
	 * @param name       a  string representing the name of the variable.
	 * @param type       a string representing the type of the variable.
	 * @param isAssigned a string representing the line type .
	 * @throws InvalidVariableUsageException
	 */
	public ScopeVariable(boolean isFinal, String name, String type, int isAssigned) throws
			InvalidVariableUsageException {
		this(isFinal, name, type);
		if (isFinal && isAssigned == NOT_ASSIGNED) {
			throw new InvalidVariableUsageException("final with no value");
		}
		this.isAssigned = isAssigned;

	}

	/**
	 * A function that returns whether the variable is assigned or not.
	 *
	 * @return a boolean representing if the var is assigned.
	 */
	public boolean isAssigned() {
		return (isAssigned != NOT_ASSIGNED);
	}

	/**
	 * Sets the field isAssigned with a new value.
	 *
	 * @param assigned the value to insert.
	 */
	public void setAssigned(int assigned) {
		isAssigned = assigned;
	}

	/**
	 * A function that returns whether the variable is final or not.
	 *
	 * @return a boolean representing if the var is final.
	 */
	public boolean isFinal() {
		return isFinal;
	}

	/**
	 * A function that returns the Name of the variable.
	 *
	 * @return the name of the variable.
	 */
	public String getName() {
		return name;
	}

	/**
	 * A function the returns the type of the variable.
	 *
	 * @return A string representation of the variable type.
	 */
	public String getMyType() {
		return myType;
	}

	/**
	 * A method that returns the line which the variable was assigned at.
	 *
	 * @return the line the variable was assigned at.
	 */
	public int getVarLineNum() {
		return isAssigned;
	}

	/**
	 * A function that returns a default value of a variable by its type in a string representation.
	 *
	 * @return A relevant default Value.
	 */
	public String getDefaultVal() {
		if (myType.equals(defaultVal.STRING.getType())) {
			return defaultVal.STRING.getDefaultVal();
		} else if (myType.equals(defaultVal.CHAR.getType())) {
			return defaultVal.CHAR.getDefaultVal();
		} else if (myType.equals(defaultVal.DOUBLE.getType())) {
			return defaultVal.DOUBLE.getDefaultVal();
		} else if (myType.equals(defaultVal.BOOLEAN.getType())) {
			return defaultVal.BOOLEAN.getDefaultVal();
		} else {
			return defaultVal.INT.getDefaultVal();
		}
	}

	/**
	 * A function the return whether the variable can be interpreted as a Boolean or not.
	 *
	 * @return true or false.
	 */
	public Boolean isBoolean() {
		return ((myType.equals(defaultVal.BOOLEAN.getType()) || myType.equals(defaultVal.INT.getType()) ||
				myType.equals(defaultVal.DOUBLE.getType())) && isAssigned());
	}

	/*
	 * A enum class representing  the different string representations for the types of variables available.
	 */
	private enum defaultVal {
		BOOLEAN("boolean", "true"), INT("int", "0"), DOUBLE("double", "0.0"),
		STRING("String", "\"a-string\""), CHAR("char", "\'a\'");

		private final String myType;
		private final String defaultVal;

		defaultVal(String myType, String myDefault) {
			this.myType = myType;
			this.defaultVal = myDefault;
		}

		public String getType() {
			return myType;
		}

		public String getDefaultVal() {
			return defaultVal;
		}
	}
}

