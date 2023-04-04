public enum SyntaxKind {
    IDENTIFIER,
    NUM,

    LINE_COMMENT,
    BLOCK_COMMENT,

    MODULE_TK,
    ENDMODULE_TK,
    WIRE_TK,
    REG_TK,
    INPUT_TK,
    OUTPUT_TK,
    ASSIGN_TK,
    ALWAYS_TK,
    CASE_TK,
    ENDCASE_TK,
    BEGIN_TK,
    END_TK,
    HEX_TK,
    COLON,           // :
    QUESTION,        // ?
    AT,

    NOT,
    AND,
    OR,

    BIT_AND,
    BIT_OR,
    BIT_XOR,
    BIT_NEGATE,

    PLUS,
    MINUS,
    MULTI,
    DIV,
    MOD,

    LESS,
    LEQ,
    GREATER,
    GEQ,
    EQL,
    NEQ,

    ASSIGN,     // =
    SEMICOLON,  // ;
    COMMA,      // ,
    L_PARENT,   // (
    R_PARENT,   // )
    L_BRACKET,  // [
    R_BRACKET,  // ]
    L_BRACE,    // {
    R_BRACE,    // }
    DOT,        // .

    EOF,
    ERROR
}


