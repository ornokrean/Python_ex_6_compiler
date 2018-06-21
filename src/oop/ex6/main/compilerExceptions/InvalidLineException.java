package oop.ex6.main.compilerExceptions;
public class InvalidLineException extends Exception {
    private static final long serialVersionUID = 1L;

    /**
     * A default constructor for the InvalidLineException.
     */
    public InvalidLineException(){super();}

    /**
     * A  constructor for the InvalidNameException that receives a message.
     * @param message The message to carry out.
     */
    public InvalidLineException(String message){super(message);}
}
