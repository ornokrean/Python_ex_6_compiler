package oop.ex6.main.Compiler;


import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class BlockCompiler extends FileCompiler {

    protected FileCompiler myCompiler;
    private BlockCompiler parentBlock = null;
     int start;
     int end;

     enum lineCase{
         IF_WHILE,FUNCTION_CALL,VAR_USAGE,RETURN,END_BLOCK
     }


    public BlockCompiler(int start, int end,FileCompiler myCompiler) {
        this.start = start;
        this.end = end;
        this.myCompiler = myCompiler;
        this.code = myCompiler.code;
    }


    public BlockCompiler(int start, int end, FileCompiler myCompiler, BlockCompiler parentBlock) {
        this(start,end,myCompiler);
        this.parentBlock = parentBlock;
    }



    @Override
    public void compile() throws Exception{
        // first off we check if the last 2 lines contains the return and "}"  statement.
        checkReturnStatment();
        for (int i = start; i < end; i++) {

        }

        //compiling my subBlocks ?

    }
    private void   isBooleanConditionValid(String line)throws Exception{
         throw  new Exception("bad boolean input");
    }
    private void isVarUsageValid() throws Exception{
        throw  new Exception("bad variable usage input");
    }

//](true|false|[-]?[0-9]+[.]?[0-9]*|[-]?[.][0-9]+)[)][\s]*[{][\s]*$
    private  lineCase getLineCase(String line){

        // if or while case.
        Pattern p =  Pattern.compile("^[\\s]*(if|while)[\\s]*[(].+[)][\\s]*[{]");
        Matcher m = p.matcher(line);

        if(m.matches()){ return lineCase.IF_WHILE;}

        // end of block case
        p =  Pattern.compile("^[\\s]*}[\\s]*$");
        m = p.matcher(line);
        if (m.matches()){
            return lineCase.END_BLOCK;
        }

        // return line case.
        p =  Pattern.compile("^[\\s]*(return;)[\\s]*^");
        m =  p.matcher(line);
        if (m.matches()){
            return lineCase.RETURN;
        }

        // var usage call case
        p =  Pattern.compile("^[\\s]((final)?[\\s]+(int|double|char|boolean|String)[\\s].*|([a-zA-Z]+|[_]+[a-zA-Z]+)[a-zA-Z_0-9]*)");
        m = p.matcher(line);

        if(m.matches()){ return lineCase.VAR_USAGE;}



        return null;//TODO take care of return
    }

    private void checkReturnStatment() throws Exception {
        Pattern p =  Pattern.compile("^[\\s]*}[\\s]*$");
        Matcher m = p.matcher(code.get(end));
        if (!m.matches()){
            throw new Exception("bad end of block");
        }
        p =  Pattern.compile("^[\\s]*return;[\\s]*^");
        m = p.matcher(code.get(end-1));

        if (!m.matches()){
            throw new Exception("bad end of block");
        }
        }

}
