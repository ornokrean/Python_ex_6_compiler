package oop.ex6.main.Variables;

public class CharVariable extends Variable{
	char value;

	public CharVariable(boolean isFinal, String name, char value) {
		super(isFinal, name);
		this.value = value;
	}
}
