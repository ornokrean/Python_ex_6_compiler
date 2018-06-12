package oop.ex6.main;

import oop.ex6.main.Compiler.FileCompiler;
import org.junit.Assert;
import org.junit.Test;

import java.io.*;
import java.util.ArrayList;

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

	static void  tests() throws FileNotFoundException,IOException{
    	BufferedReader redaer = new BufferedReader(new FileReader
			    ("/Users/or/Desktop/CS/Java/ex6/ex6_files/sjavac_tests.txt"));
    	ArrayList<String[]> filelines = new ArrayList<>();
    	String line;
    	int j=0;
    	while((line = redaer.readLine())!= null){
    		filelines.add(line.split(" "));
    		j++;
	    }


        String prefPath="./ex6_files/tests/";
		System.out.println("Starting tests...");
		int fileNum = 0;
        for (int i = 0; i < 506; i++) {
            String path=prefPath+"test"+i+".sjava";
	        if((new File(path)).exists()){
		        System.out.println("now testing file "+i);
		        int ans = fakeMain(new String[]{"test"+path+".sjava"});
		        boolean check = (ans == Integer.parseInt(filelines.get(fileNum)[1]));
		        fileNum++;
		        String testInfo = " (";
		        for (String s : filelines.get(fileNum)) {
			        testInfo += " "+s;
		        }
		        testInfo += " )";
                Assert.assertTrue("Test number "+i+" failed, should print "+filelines
		                .get(fileNum)[1]+" but printed "+ans+testInfo,check);
            }
        }
    }

	static int fakeMain(String[] args){

        try {
            checkInput(args);
            BufferedReader reader = new BufferedReader(new FileReader(args[PATH]));
            FileCompiler compiler = new FileCompiler(reader);
            compiler.compile();
	        return 0;
            // we need to separate into IO errors and Compilation Errors
        }catch (IOException r){
	        //System.err.println(r.getMessage());
			return 2;
        }catch (Exception e){
            //System.err.println(e.getMessage());
			return 1;
        }
    }


    public static void main(String[] args) throws Exception{
    	boolean test = true;
    	if (test) {
		    tests();
		    return;
	    }
        try {
            checkInput(args);

            BufferedReader reader = new BufferedReader(new FileReader(args[PATH]));
            FileCompiler compiler = new FileCompiler(reader);

            compiler.compile();

        // we need to separate into IO errors and Compilation Errors
        }catch (Exception e){
            System.err.println(e.getMessage());
        }
    }

}