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

public  class scopeVariable {
	boolean isFinal;
	String name;
	String myType;
	boolean isAssigned = true;


	public scopeVariable(boolean isFinal, String name, String type) {
		this.isFinal = isFinal;
		this.name = name;
		this.myType =type;
	}

	public scopeVariable(boolean isFinal, String name, String type,boolean isAssigned) {
		this(isFinal,name,type);
		this.isAssigned = isAssigned;
	}

	public boolean isFinal() {
		return isFinal;
	}

	public String getName() {
		return name;
	}

	public String getMyType() {
		return myType;
	}
}

