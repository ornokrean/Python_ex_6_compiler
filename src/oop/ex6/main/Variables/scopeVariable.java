//package oop.ex6.main.Variables;
//
//public abstract class scopeVariable {
//	boolean isFinal;
//	String name;
//
//
//	public scopeVariable() {
//	}
//
//	public scopeVariable(boolean isFinal, String name) {
//		this.isFinal = isFinal;
//		this.name = name;
//	}
//}

package oop.ex6.main.Variables;

public  class scopeVariable<T> {
	boolean isFinal;
	String name;
	T type;
	boolean isAssigned = true;


	public scopeVariable(boolean isFinal, String name, T type) {
		this.isFinal = isFinal;
		this.name = name;
		this.type = type;
	}

	public scopeVariable(boolean isFinal, String name, T type,boolean isAssigned) {
		this(isFinal,name,type);
		this.isAssigned = isAssigned;
	}

	public boolean isFinal() {
		return isFinal;
	}

	public String getName() {
		return name;
	}

	public T getType() {
		return type;
	}
}

