package oop.ex6.main;

import oop.ex6.Compiler.FileCompiler;
import oop.ex6.CompilerExceptions.InvalidLineException;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;

/**
 * A class that compiles a Java-scrips of type s-java .
 */
public class Sjavac {
	private static final String SJAVA_SUFFIX = ".sjava";
	private static final String NOT_SJAVA_WARNING = "Warning: Running on a non s-java file";


	/*
	 * A variable indicating the Correct input size.
	 */
	private static final int INPUT_SIZE = 1;
	/*
	 *  A variable indicating the path location in the args, given.
	 */
	private final static int PATH = 0;
	private final static String BAD_INPUT_ERROR = "ERROR: Wrong usage. Should receive 1 argument";
	private static final int IO_ERROR = 2;
	private static final int COMPILING_ERROR = 1;
	private static final int VALID_CODE = 0;

	/*
	 * Check if the input is valid. In case that the input is not valid print error message to the
	 * screen and exit.
	 * @param input The input that the program gets.
	 */
	private static void checkInput(String[] input) throws IOException {
		if (!input[PATH].endsWith(SJAVA_SUFFIX)) {
			System.err.println(NOT_SJAVA_WARNING);
		}
		if (input.length < INPUT_SIZE) {
			throw new IOException(BAD_INPUT_ERROR);
		}
	}

	/**
	 * this is the main function, it will run the compiler and will print the results
	 * @param args the args of the main given from the user
	 */
	public static void main(String[] args) {
		try (BufferedReader reader = new BufferedReader(new FileReader(args[PATH]))) {
			checkInput(args);
			FileCompiler compiler = new FileCompiler(reader);
			compiler.compile();
			System.out.print(VALID_CODE);
		} catch (IOException e) {
			System.out.print(IO_ERROR);
			System.err.println(e.getMessage());
		} catch (InvalidLineException e) {
			System.out.print(COMPILING_ERROR);
			System.err.println(e.getMessage());
		}
	}


}