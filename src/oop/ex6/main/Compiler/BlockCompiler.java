package oop.ex6.main.Compiler;


public class BlockCompiler extends FileCompiler {

    protected FileCompiler myCompiler;
    private BlockCompiler parentBlock = null;
    final int start;
    final int end;

    public BlockCompiler(int start, int end,FileCompiler myCompiler) {
        this.start = start;
        this.end = end;
        this.myCompiler = myCompiler;
    }

    public BlockCompiler(int start, int end, FileCompiler myCompiler, BlockCompiler parentBlock) {
        this(start,end,myCompiler);
        this.parentBlock = parentBlock;
    }

    @Override
    public void compile() {


        //compiling my subBlocks ?
        super.compile();
    }
}
