package oop.ex6.main;

import oop.ex6.Compiler.FileCompiler;
import oop.ex6.main.compilerExceptions.InvalidLineException;

import java.io.*;

public class  Sjavac {
    private static final int  INPUT_SIZE = 1;
    private final static int PATH = 0;
    private final static String BAD_INPUT_ERROR = "ERROR: Wrong usage. Should receive 1 arguments";
    /*
     * Check if the input is valid. In case that the input is not valid print error message to the
     * screen and exit.
     * @param input The input that the program gets.
     */
    private static void checkInput(String[] input) throws IOException{
        if (input.length != INPUT_SIZE) {
            throw new IOException(BAD_INPUT_ERROR);
        }
        // TODO to check is it a valid path!!!
    }
    public static void main(String[] args)throws Exception{
        try {
            checkInput(args);
            BufferedReader reader = new BufferedReader(new FileReader(args[PATH]));
            FileCompiler compiler = new FileCompiler(reader);
            compiler.compile();
            System.out.print(0);

        }

        catch (IOException e){
            System.out.print(2);
//            System.err.println(e.getMessage());
//            throw e;

        }catch (InvalidLineException e){
            System.out.print(1);
//            System.err.println(e.getMessage());
//            throw e;
        }
    }



}