package oop.ex6.main.Variables;

import java.util.regex.Matcher;
import java.util.regex.Pattern;


public class VariableFactory {
    private static final String BAD_VARIABLE_DECLERATION = "ERROR: Wrong type variable in line : ";

    enum typeCases{

        BOOLEAN("boolean"), INT("int"), DOUBLE("double"), STRING("String"), CHAR("Char"),ISFINAL("final");

        private final String myType;

        private final String WORDREGEX = "\\b[a-zA-z_0-9]*\\b";


        typeCases(String string) {myType = string; }
        public String getMyType() {return myType;}
    }
//    private static final String[] availableTypes = {"boolean","int","double","String","Char"};


    public static Variable variableFactory(String isFinal, String type, String varName,String varValue) throws ExceptionInInitializerError{
        Boolean finalFlag = false;
        if(isFinal.equals(typeCases.ISFINAL.myType)){finalFlag = true;}

        if(type.equals(typeCases.BOOLEAN.myType)){
            Boolean bool = booleanHelper(varValue);
            return new Variable<>(finalFlag,varName,bool);


        }else if(type.equals(typeCases.INT.myType)){
            int num = intHelper(varValue);
            return new Variable<>(finalFlag,varName,num);


        }else if(type.equals(typeCases.DOUBLE.myType)){
            double num = doubleHelper(varValue);
            return new Variable<>(finalFlag,varName,num);


        }else if(type.equals(typeCases.STRING.myType)){
            String string = stringHelper(varValue);
            return new Variable<>(finalFlag,varName,string);

        }else if(type.equals(typeCases.CHAR.myType)){
            char charVar = charHelper(varValue);
            return new Variable<>(finalFlag,varName,charVar);
        }
        else {
            throw new ExceptionInInitializerError(BAD_VARIABLE_DECLERATION);
        }
    }


    private static int intHelper(String varValue) {
        try{
            return Integer.parseInt(varValue);
        }catch (NumberFormatException e){
            throw new ExceptionInInitializerError("bad int");
        }
    }

    private static double doubleHelper(String varValue) {
        try{
            return Double.parseDouble(varValue);
        }catch (NumberFormatException e){
            throw new ExceptionInInitializerError("bad double");
        }
    }

    private static Boolean booleanHelper(String varValue){
        Pattern p = Pattern.compile("(true|false|[-]?[0-9]+[.]?[0-9]*|[-]?[.][0-9]+)\b");
        Matcher m = p.matcher(varValue);
        if(!(m.matches())){ throw new ExceptionInInitializerError("bad boolean");}

        if(varValue.equals("true")||varValue.equals("false")){
            return Boolean.parseBoolean(varValue);
        }
        else {
            return (Double.parseDouble(varValue) != 0.0);
        }
    }

    private static String stringHelper(String varValue){
        Pattern p = Pattern.compile("[\"][^\"]*[\"]");
        Matcher m = p.matcher(varValue);
        if(!(m.matches())){throw new ExceptionInInitializerError("bad string");}

        // to check boundaries.
        return varValue.substring(1,varValue.length()-1);

    }

    private static char  charHelper(String varValue){
        Pattern p = Pattern.compile("[\'][^\'][\']");
        Matcher m = p.matcher(varValue);
        if(!(m.matches())){throw new ExceptionInInitializerError("bad char");}

        // to check boundaries.
        char charVal = varValue.charAt(1);
        return charVal;
    }
}
