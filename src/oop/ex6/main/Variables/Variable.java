package oop.ex6.main.Variables;

public abstract class Variable {
	boolean isFinal;
	String name;

	public Variable(boolean isFinal) {
		this.isFinal = isFinal;
	}

	public Variable() {
	}


	public Variable(boolean isFinal, String name) {
		this.isFinal = isFinal;
		this.name = name;
	}
}

