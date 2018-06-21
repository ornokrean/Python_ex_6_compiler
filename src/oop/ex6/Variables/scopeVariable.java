//package oop.ex6.Variables;
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

package oop.ex6.Variables;

public class scopeVariable {
	static final String DEFAULT_STRING = "\"a-string\"";
	static final String DEFAULT_CHAR = "\'a\'";
	static final String DEFAULT_DOUBLE = "0.0";
	static final String DEFAULT_BOOLEAN = "true";
	static final String DEFAULT_INT = "0";
	boolean isFinal;
	String name;
	String myType;
	private int isAssigned = -1;

	enum defaultVal{
		BOOLEAN("boolean","true"), INT("int","0"), DOUBLE("double","0.0"), STRING("String","\"a-string\""), CHAR("char","\'a\'");

		private final String myType;
		private final String defaultVal;
		defaultVal(String myType,String myDefault) { this.myType = myType; this.defaultVal = myDefault; }
		public String getType() {return myType;}

		public String getDefaultVal(){return defaultVal;}
	}


	public scopeVariable(boolean isFinal, String name, String type) {
		this.isFinal = isFinal;
		this.name = name;
		this.myType =type;
	}

	public scopeVariable(boolean isFinal, String name, String type, int isAssigned) throws Exception{
		this(isFinal,name,type);
		if (isFinal && isAssigned == -1) {
			throw new Exception("final with no value");
		}
		this.isAssigned = isAssigned;

	}

	public boolean isAssigned(){
		return (isAssigned != -1);
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

	public void setAssigned(int assigned) {
		isAssigned = assigned;
	}

	public int getVarLineNum(){
		return isAssigned;
	}

	public String getDefaultVal(){
		if(myType.equals(defaultVal.STRING.getType())){
			return defaultVal.STRING.getDefaultVal();
		}
		else if (myType.equals(defaultVal.CHAR.getType())){
			return defaultVal.CHAR.getDefaultVal();
		}
		else if (myType.equals(defaultVal.DOUBLE.getType())){
			return defaultVal.DOUBLE.getDefaultVal();
		}
		else if (myType.equals(defaultVal.BOOLEAN.getType())){
			return defaultVal.BOOLEAN.getDefaultVal();
		}
		else{
			return defaultVal.INT.getDefaultVal();
		}
	}
	public Boolean isBoolean(){
		return ((myType.equals(defaultVal.BOOLEAN.getType())||myType.equals(defaultVal.INT.getType())||
				myType.equals(defaultVal.DOUBLE.getType()))&& isAssigned());
	}
}

