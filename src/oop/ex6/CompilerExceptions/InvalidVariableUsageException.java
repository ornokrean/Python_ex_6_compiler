package oop.ex6.CompilerExceptions;

/**
 * Exception of the state of invalid variable usage
 */
public class InvalidVariableUsageException extends InvalidLineException {

	private static final long serialVersionUID = 1L;

	/**
	 * A  constructor for the InvalidNameException that receives a message.
	 *
	 * @param message The message to carry out.
	 */
	public InvalidVariableUsageException(String message) {
		super(message);
	}
}
