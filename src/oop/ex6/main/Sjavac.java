package oop.ex6.main;

import oop.ex6.main.Compiler.FileCompiler;
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
    private static void checkInput(String[] input) throws Exception{
        if (input.length != INPUT_SIZE) {
            throw new Exception(BAD_INPUT_ERROR);
        }
        // TODO to check is it a valid path!!!
    }
    public static void main(String[] args)throws Exception{
        try {
            checkInput(args);
            BufferedReader reader = new BufferedReader(new FileReader(args[PATH]));
            FileCompiler compiler = new FileCompiler(reader);
            compiler.compile();
            System.out.println(0);

            //TODO we need to separate into IO errors and Compilation Errors
        }
        catch (IOException e){
            System.out.println(2);
//            System.err.println(e.getMessage());
//            throw e;

        }catch (Exception e){
            System.out.println(1);
            System.err.println(e.getMessage());
            throw e;

        }
    }



}