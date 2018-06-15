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

	enum defaultVal{

		BOOLEAN("boolean"), INT("int"), DOUBLE("double"), STRING("String"), CHAR("char"),ISFINAL("final"),
		DECLARATION("declaration");

		private final String myType;

		defaultVal(String string) {myType = string; }
		public String getType() {return myType;}
	}


	public scopeVariable(boolean isFinal, String name, String type) {
		this.isFinal = isFinal;
		this.name = name;
		this.myType =type;
	}

	public scopeVariable(boolean isFinal, String name, String type,boolean isAssigned) throws Exception{
		this(isFinal,name,type);
		if (isFinal && !isAssigned) {
//			System.out.println(isAssigned + " " + isFinal);
			throw new Exception("final with no value");
		}
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

	public boolean isAssigned() {
		return isAssigned;
	}

	public void setAssigned(boolean assigned) {
		isAssigned = assigned;
	}

	public String getDefaultVal(){

		if(myType.equals(defaultVal.STRING.getType())){
			return "\"a-string\"";
		}
		else if (myType.equals(defaultVal.CHAR.getType())){
			return "\'a\'";
		}
		else if (myType.equals(defaultVal.DOUBLE.getType())){
			return "0.0";
		}
		else if (myType.equals(defaultVal.BOOLEAN.getType())){
			return "true";
		}
		else{
			return "0";
		}
	}
	public Boolean isBoolean(){
		return (myType.equals(defaultVal.BOOLEAN.getType())||myType.equals(defaultVal.INT.getType())||
				myType.equals(defaultVal.DOUBLE.getType()));
	}
}

