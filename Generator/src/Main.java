import java.util.ArrayList;

public class Main {
    public static void main(String[] args) {
        HierarchyAnalyzer analyzer = new HierarchyAnalyzer();
        analyzer.buildUsageMap();

        VerilogCompiler verilogCompiler = new VerilogCompiler("alu.v");
        verilogCompiler.compiling();
        ArrayList<Token> tokens = Lexer.getInstance().getTokens();
        for (Token token : tokens) {
            System.out.println(token.content + " " + token.kind);
        }
    }
}

