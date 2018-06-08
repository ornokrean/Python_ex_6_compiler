package oop.ex6.main.Compiler;


import oop.ex6.main.Variables.Variable;

import java.io.BufferedReader;
import java.util.ArrayList;
import java.util.HashSet;

public class FileCompiler {

    protected ArrayList<String> code;

    protected HashSet<String> functionsList;

//    private BufferedReader codeReader;

    protected HashSet<Variable> vars;

    private BlockCompiler[] mySubBlocks;

    public FileCompiler(){
    }

    public FileCompiler(BufferedReader codeReader){
        initiateCompiler(codeReader);
    }
    private static String reformatLine(String line){

        return "";//TODO Set this function!!!
    }

    private void initiateCompiler(BufferedReader codeReader){
    }

    public void compile(){


        for (BlockCompiler block: mySubBlocks
             ) {
            block.compile();
        }
    }
}
