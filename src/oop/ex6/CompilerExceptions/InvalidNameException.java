package oop.ex6.CompilerExceptions;

public class InvalidNameException extends InvalidLineException {

	private static final long serialVersionUID = 1L;

	/**
	 * A  constructor for the InvalidNameException that receives a message.
	 *
	 * @param msg The message to carry out.
	 */
	public InvalidNameException(String msg) {
		super(msg);
	}
}
