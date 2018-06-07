package oop.ex6.main.Compiler;


public class BlockCompiler extends Compiler{

    protected Compiler myCompiler;
    private BlockCompiler parentBlock = null;
    final int start;
    final int end;

    public BlockCompiler(int start, int end,Compiler myCompiler) {
        this.start = start;
        this.end = end;
        this.myCompiler = myCompiler;
    }

    public BlockCompiler(int start, int end,Compiler myCompiler,BlockCompiler parentBlock) {
        this(start,end,myCompiler);
        this.parentBlock = parentBlock;
    }


}
