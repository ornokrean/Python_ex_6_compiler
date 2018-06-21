package oop.ex6.main;

import oop.ex6.Compiler.FileCompiler;
import oop.ex6.main.compilerExceptions.InvalidLineException;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;

public class Sjavac {
	/**
	 * A class that compiles a Java-scrips of type s-java .
	 */

	/*
	 * A variable indicating the Correct input size.
	 */
	private static final int INPUT_SIZE = 1;
	/*
	 *  A variable indicating the path location in the args, given.
	 */
	private final static int PATH = 0;

	private final static String BAD_INPUT_ERROR = "ERROR: Wrong usage. Should receive 1 arguments";
	private static final int IO_ERROR = 2;
	private static final int COMPILATION_ERROR = 1;
	private static final int VALID_CODE = 0;

	/*
	 * Check if the input is valid. In case that the input is not valid print error message to the
	 * screen and exit.
	 * @param input The input that the program gets.
	 */
	private static void checkInput(String[] input) throws IOException {
		if (input.length != INPUT_SIZE) {
			throw new IOException(BAD_INPUT_ERROR);
		}
	}

	public static void main(String[] args) throws Exception {

		try (BufferedReader reader = new BufferedReader(new FileReader(args[PATH]))) {
			checkInput(args);
			FileCompiler compiler = new FileCompiler(reader);
			compiler.compile();
			System.out.print(VALID_CODE);
		} catch (IOException e) {
			System.out.print(IO_ERROR);
			System.err.println(e.getMessage());

		} catch (InvalidLineException e) {
			System.out.print(COMPILATION_ERROR);
			System.err.println(e.getMessage());
			throw e;
		}
	}


}