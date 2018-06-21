package oop.ex6.main.compilerExceptions;
public class InvalidVariableUsageException extends InvalidLineException {
    private static final long serialVersionUID = 1L;
    public InvalidVariableUsageException() { super(); }
    public InvalidVariableUsageException(String message) {
        super(message);
    }
}
