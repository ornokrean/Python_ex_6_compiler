//package oop.ex6.main.Variables;
//
//public abstract class Variable {
//	boolean isFinal;
//	String name;
//
//
//	public Variable() {
//	}
//
//	public Variable(boolean isFinal, String name) {
//		this.isFinal = isFinal;
//		this.name = name;
//	}
//}

package oop.ex6.main.Variables;

public  class Variable<T> {
	boolean isFinal;
	String name;
	T value;

	public Variable(boolean isFinal, String name,T value) {
		this.isFinal = isFinal;
		this.name = name;
		this.value = value;
	}

	public boolean isFinal() {
		return isFinal;
	}

	public String getName() {
		return name;
	}

	public T getValue() {
		return value;
	}
}

