package oop.ex6.main.compilerExceptions;

public class InvalidNameException extends InvalidLineException {
    private static final long serialVersionUID = 1L;
    public InvalidNameException(){super();}
    public InvalidNameException(String msg){super(msg);}
}
