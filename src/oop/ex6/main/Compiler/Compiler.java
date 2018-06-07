package oop.ex6.main.Compiler;


import oop.ex6.main.Variables.Variables;

import java.io.BufferedReader;
import java.util.ArrayList;
import java.util.HashSet;

public class Compiler {

    protected ArrayList<String> code;

    protected HashSet<String> functionsList;

//    private BufferedReader codeReader;

    protected HashSet<Variables> vars;

    public Compiler() {
    }

    public Compiler(BufferedReader codeReader){
        initiateCompiler(codeReader);
    }

    private void initiateCompiler(BufferedReader codeReader){
        // start reading the files and adding b
    }
}
