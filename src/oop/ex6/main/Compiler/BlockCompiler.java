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

    enum LineCase {IF_WHILE,FUNCTION_CALL,VAR_USAGE,RETURN,END_BLOCK,NO_MATCH}


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
        //check signature
        int i = start;
        while (i< end-1){
            i++;

            LineCase lineCase = getLineCase(code.get(i));
            switch (lineCase){
                case RETURN:
                    break;
                case END_BLOCK:
                    break;
                case IF_WHILE:
                    i = subBlockGeneretor(i);
                    break;
                case VAR_USAGE:
                    break;
                case FUNCTION_CALL:
                    break;
                case NO_MATCH:
                    System.out.println(code.get(i));
                    // raise exception is necessary
            }
            for (BlockCompiler subblock:mySubBlocks
                 ) {
                subblock.compile();
            }
        }
    }
    private int subBlockGeneretor(int lineNumber) throws Exception{
        int startOfSubblock = lineNumber;
        compileHelper.changeCounter(parenthesisCounter,code.get(lineNumber));
        while (parenthesisCounter[0] != 0){
            lineNumber++;
            compileHelper.changeCounter(parenthesisCounter,code.get(lineNumber));
        }
        mySubBlocks.add(new BlockCompiler(startOfSubblock,lineNumber,myCompiler));
        return lineNumber;
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

        p =  Pattern.compile("(return;)[\\s]*");
        m =  p.matcher(line);
        if (m.matches()){ return LineCase.RETURN; }


        // var usage call case
        p =  Pattern.compile("((final)?[\\s]*(int|double|char|boolean|String)[\\s].*|(([a-zA-Z]*|[_])[\\w]+)[\\s]+[=].*)");
        m = p.matcher(line);

        if(m.matches()){ return LineCase.VAR_USAGE;}

        p =  Pattern.compile("[a-zA-Z][\\w]*[(].*[)][\\s]*(;|[{])");
        m = p.matcher(line);

        if(m.matches()){ return LineCase.FUNCTION_CALL;}

        return LineCase.NO_MATCH;//TODO take care of return
    }
    public int generateSubBlocks(String line,int lineNumber) throws Exception{
        int endline = lineNumber;

        Pattern p1 =  Pattern.compile("(void)\\s+[a-zA-Z][\\w]*[(].*[)][\\s]*(;|[{])");
        Matcher m1 = p1.matcher(line);

//        if(m1.matches()){ return LineCase.FUNCTION_CALL;}

        // if or while case.
        Pattern p2 =  Pattern.compile("^[\\s]*(if|while)[\\s]*[(].+[)][\\s]*[{]");
        Matcher m2 = p2.matcher(line);

        if(m2.matches() || m1.matches()){
            compileHelper.changeCounter(parenthesisCounter,code.get(endline));
            while (parenthesisCounter[0] != 0){
                endline++;
                compileHelper.changeCounter(parenthesisCounter,code.get(endline));
            }
        }
        return endline;
    }

    private void checkReturnStatment() throws Exception {
        Pattern p =  Pattern.compile("}[\\s]*$");
        Matcher m = p.matcher(code.get(end));
        if (!m.matches()){
            throw new Exception("bad end of block");
        }
        p =  Pattern.compile("(return;)[\\s]*");
        m = p.matcher(code.get(end-1));

        if (!m.matches()){
            throw new Exception("bad end of block");
        }
        }

}
