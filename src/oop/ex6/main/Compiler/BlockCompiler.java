package oop.ex6.main.Compiler;


import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class BlockCompiler extends FileCompiler {

    protected FileCompiler myCompiler;
    private BlockCompiler parentBlock = null;
    int start;
    int end;
    private static final String  VAR_ASSIGNMENT = "(([a-zA-Z]*|[_])[\\w]+)[\\s]+[=].*";

    private static final String VAR_DECLERATION = "(final)?[\\s]+(int|double|char|boolean|String)[\\s].*";

    enum LineCase {
         IF_WHILE,FUNCTION_CALL,VAR_USAGE,RETURN,END_BLOCK,NO_MATCH;
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
            LineCase lineCase = getLineCase(code.get(i));
            switch (lineCase){
                case RETURN:
                    break;
                case END_BLOCK:
                    break;
                case IF_WHILE:
                    break;
                case VAR_USAGE:
                    break;
                case FUNCTION_CALL:
                    break;
                case NO_MATCH:
                    throw new Exception("bad line format");
            }
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
    private LineCase getLineCase(String line){

        // if or while case.
        Pattern p =  Pattern.compile("^[\\s]*(if|while)[\\s]*[(].+[)][\\s]*[{]");
        Matcher m = p.matcher(line);

        if(m.matches()){ return LineCase.IF_WHILE;}

        // end of block case
        p =  Pattern.compile("^[\\s]*}[\\s]*$");
        m = p.matcher(line);
        if (m.matches()){
            return LineCase.END_BLOCK;
        }


        //^[\s]*(return;)[\s]*^
        //[\\s]*((final)?[\\s]+(int|double|char|boolean|String)[\\s].*|(([a-zA-Z]*|[_])[\\w]+)[\\s]+[=].*)
        // return line case.
        p =  Pattern.compile("^[\\s]*(return;)[\\s]*^");
        m =  p.matcher(line);
        if (m.matches()){ return LineCase.RETURN; }


        // var usage call case
        p =  Pattern.compile("[\\s]*((final)?[\\s]+(int|double|char|boolean|String)[\\s].*|(([a-zA-Z]*|[_])[\\w]+)[\\s]+[=].*)");
        m = p.matcher(line);

        if(m.matches()){ return LineCase.VAR_USAGE;}

        p =  Pattern.compile("[\\s]*[a-zA-Z][\\w]+[(].*[)][\\s]*[;]");
        m = p.matcher(line);

        if(m.matches()){ return LineCase.FUNCTION_CALL;}

        return LineCase.NO_MATCH;//TODO take care of return
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
