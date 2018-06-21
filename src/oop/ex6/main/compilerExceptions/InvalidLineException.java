package oop.ex6.main.compilerExceptions;
public class InvalidLineException extends Exception {
    private static final long serialVersionUID = 1L;
    public InvalidLineException() { super(); }
    public InvalidLineException(String message) { super(message);}
}
