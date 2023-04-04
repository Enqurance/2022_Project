import java.util.HashMap;

public class Token {
    int line;
    SyntaxKind kind;
    String content;

    public Token(int line) {
        this.content = "";
        this.line = line;
        this.kind = SyntaxKind.ERROR;
    }

    public Token(String content, int line, SyntaxKind kind) {
        this.content = content;
        this.line = line;
        this.kind = kind;
    }

    public String getContent() {
        return content;
    }

    public int getLine() {
        return line;
    }

    public SyntaxKind getKind() {
        return kind;
    }

    public String getSyntaxName() {
        return syntaxKind2Name.get(this.kind);
    }

    private static HashMap<SyntaxKind, String> syntaxKind2Name;
    static {
        syntaxKind2Name = new HashMap<>();
        syntaxKind2Name.put(SyntaxKind.IDENTIFIER, "IDENFR");
        syntaxKind2Name.put(SyntaxKind.NUM, "NUM");

        syntaxKind2Name.put(SyntaxKind.MODULE_TK, "MODULE");
        syntaxKind2Name.put(SyntaxKind.ENDMODULE_TK, "ENDMODULE");
        syntaxKind2Name.put(SyntaxKind.WIRE_TK, "WIRE");
        syntaxKind2Name.put(SyntaxKind.REG_TK, "REG");
        syntaxKind2Name.put(SyntaxKind.INPUT_TK, "INPUT");
        syntaxKind2Name.put(SyntaxKind.OUTPUT_TK, "OUTPUT");
        syntaxKind2Name.put(SyntaxKind.ASSIGN_TK, "ASSIGN");
        syntaxKind2Name.put(SyntaxKind.ALWAYS_TK, "ALWAYS");
        syntaxKind2Name.put(SyntaxKind.CASE_TK, "CASE");
        syntaxKind2Name.put(SyntaxKind.ENDCASE_TK, "ENDCASE");
        syntaxKind2Name.put(SyntaxKind.BEGIN_TK, "BEGIN");
        syntaxKind2Name.put(SyntaxKind.END_TK, "END");
        syntaxKind2Name.put(SyntaxKind.HEX_TK, "HEX");
        syntaxKind2Name.put(SyntaxKind.AT, "@");
        syntaxKind2Name.put(SyntaxKind.DOT, ".");
        syntaxKind2Name.put(SyntaxKind.BIT_AND, "&");
        syntaxKind2Name.put(SyntaxKind.BIT_OR, "|");
        syntaxKind2Name.put(SyntaxKind.BIT_XOR, "^");
        syntaxKind2Name.put(SyntaxKind.BIT_NEGATE, "~");
        syntaxKind2Name.put(SyntaxKind.COLON, ":");
        syntaxKind2Name.put(SyntaxKind.QUESTION, "?");

        syntaxKind2Name.put(SyntaxKind.NOT, "NOT");
        syntaxKind2Name.put(SyntaxKind.AND, "AND");
        syntaxKind2Name.put(SyntaxKind.OR, "OR");
        syntaxKind2Name.put(SyntaxKind.PLUS, "PLUS");
        syntaxKind2Name.put(SyntaxKind.MINUS, "MINU");
        syntaxKind2Name.put(SyntaxKind.MULTI, "MULT");
        syntaxKind2Name.put(SyntaxKind.DIV, "DIV");
        syntaxKind2Name.put(SyntaxKind.MOD, "MOD");
        syntaxKind2Name.put(SyntaxKind.LESS, "LSS");
        syntaxKind2Name.put(SyntaxKind.LEQ, "LEQ");
        syntaxKind2Name.put(SyntaxKind.GREATER, "GRE");
        syntaxKind2Name.put(SyntaxKind.GEQ, "GEQ");
        syntaxKind2Name.put(SyntaxKind.EQL, "EQL");
        syntaxKind2Name.put(SyntaxKind.NEQ, "NEQ");
        syntaxKind2Name.put(SyntaxKind.ASSIGN, "ASSIGN");
        syntaxKind2Name.put(SyntaxKind.SEMICOLON, "SEMICN");
        syntaxKind2Name.put(SyntaxKind.COMMA, "COMMA");
        syntaxKind2Name.put(SyntaxKind.L_PARENT, "LPARENT");
        syntaxKind2Name.put(SyntaxKind.R_PARENT, "RPARENT");
        syntaxKind2Name.put(SyntaxKind.L_BRACKET, "LBRACK");
        syntaxKind2Name.put(SyntaxKind.R_BRACKET, "RBRACK");
        syntaxKind2Name.put(SyntaxKind.L_BRACE, "LBRACE");
        syntaxKind2Name.put(SyntaxKind.R_BRACE, "RBRACE");
    }
}
