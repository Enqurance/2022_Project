import java.util.ArrayList;
import java.util.HashMap;
import java.util.Objects;

public class Lexer {
    private static final Lexer lexer = new Lexer();

    private boolean begin;

    private final ArrayList<Token> tokens = new ArrayList<>();
    private char ch;
    private int l_index;
    private int l_num;
    private String token;
    private String line;
    private boolean is_line_comment;
    private boolean is_block_comment;

    private static final HashMap<String, SyntaxKind> reserved;

    static {
        reserved = new HashMap<>();
        reserved.put("module", SyntaxKind.MODULE_TK);
        reserved.put("endmodule", SyntaxKind.ENDMODULE_TK);
        reserved.put("wire", SyntaxKind.WIRE_TK);
        reserved.put("reg", SyntaxKind.REG_TK);
        reserved.put("input", SyntaxKind.INPUT_TK);
        reserved.put("output", SyntaxKind.OUTPUT_TK);
        reserved.put("assign", SyntaxKind.ASSIGN_TK);
        reserved.put("case", SyntaxKind.CASE_TK);
        reserved.put("endcase", SyntaxKind.ENDCASE_TK);
        reserved.put("begin", SyntaxKind.BEGIN_TK);
        reserved.put("end", SyntaxKind.END_TK);
        reserved.put("always", SyntaxKind.ALWAYS_TK);
    }

    private Lexer() {}

    public static Lexer getInstance() {
        return lexer;
    }

    public void initialize() {
        l_num = 1;
        begin = false;
        is_block_comment = false;
        is_line_comment = false;
        token = "";
    }

    public void updateTokens (String s) {
        line = s.trim();
        if (!begin) {
            int module_start = line.indexOf("module");
            if (module_start != -1) {
                l_index = module_start;
                begin = true;
            } else {
                l_num++;
                return;
            }
        } else l_index = 0;
        is_line_comment = false;
        if (is_block_comment) {
            eatBlockComment();
        }
        while (l_index < line.length()) {
            if (begin) {
                lexing();
                if (is_line_comment) {
                    break;
                }
            }
        }
        l_num++;
    }

    public void lexing() {
        getNextChar();
        // clear spaces
        while (l_index < line.length() && isSpace()) {
            getNextChar();
        }
        if (l_index > line.length()) {
            return;
        }

        StringBuilder sb = new StringBuilder();
        if (ch == '/') {
            dealDiv();
        } else if (ch == '.') {
            tokens.add(new Token(".", l_num, SyntaxKind.DOT));
        } else if (ch == '&') {
            tokens.add(new Token("&", l_num, SyntaxKind.BIT_AND));
        } else if (ch == '|') {
            tokens.add(new Token("|", l_num, SyntaxKind.BIT_OR));
        } else if (ch == '^') {
            tokens.add(new Token("^", l_num, SyntaxKind.BIT_XOR));
        } else if (ch == '~') {
            tokens.add(new Token("~", l_num, SyntaxKind.BIT_NEGATE));
        } else if (ch == '@') {
            tokens.add(new Token("@", l_num, SyntaxKind.AT));
        } else if (ch == ':') {
            tokens.add(new Token(":", l_num, SyntaxKind.COLON));
        } else if (ch == '\'') {
            getNextChar();
            tokens.add(new Token("'" + ch, l_num, SyntaxKind.HEX_TK));
        } else if (ch == '?') {
            tokens.add(new Token("?", l_num, SyntaxKind.QUESTION));
        }


        else if (ch == ',') {
            tokens.add(new Token(",", l_num, SyntaxKind.COMMA));
        } else if (ch == ';') {
            tokens.add(new Token(";", l_num, SyntaxKind.SEMICOLON));
        } else if (ch == '+') {
            tokens.add(new Token("+", l_num, SyntaxKind.PLUS));
        } else if (ch == '-') {
            tokens.add(new Token("-", l_num, SyntaxKind.MINUS));
        } else if (ch == '*') {
            tokens.add(new Token("*", l_num, SyntaxKind.MULTI));
        } else if (ch == '%') {
            tokens.add(new Token("%", l_num, SyntaxKind.MOD));
        } else if (ch == '(') {
            tokens.add(new Token("(", l_num, SyntaxKind.L_PARENT));
        } else if (ch == ')') {
            tokens.add(new Token(")", l_num, SyntaxKind.R_PARENT));
        } else if (ch == '[') {
            tokens.add(new Token("[", l_num, SyntaxKind.L_BRACKET));
        } else if (ch == ']') {
            tokens.add(new Token("]", l_num, SyntaxKind.R_BRACKET));
        } else if (ch == '{') {
            tokens.add(new Token("{", l_num, SyntaxKind.L_BRACE));
        } else if (ch == '}') {
            tokens.add(new Token("}", l_num, SyntaxKind.R_BRACE));
        } else if ("=><!&|".indexOf(ch) != -1) {
            dealOtherOp();
        } else if (isLetter()) {
            while (isLetter() || isDigit()) {
                sb.append(ch);
                if (l_index < line.length()) getNextChar();
                else {
                    l_index++;
                    break;
                }
            }
            token = sb.toString();
            l_index--;
            if (isReversed()) {
                tokens.add(new Token(token, l_num, reserved.get(token)));
                if (Objects.equals(token, "endmodule")) {
                    begin = false;
                }
            } else {
                tokens.add(new Token(token, l_num, SyntaxKind.IDENTIFIER));
            }
        } else if (isDigit()) {
            while (isDigit()) {
                sb.append(ch);
                if (l_index < line.length()) getNextChar();
                else {
                    l_index++;
                    break;
                }
            }
            token = sb.toString();
            l_index--;
            tokens.add(new Token(token, l_num, SyntaxKind.NUM));
        } else {
            tokens.add(new Token(l_num));
        }
    }

    public void getNextChar() {
        ch = line.charAt(l_index);
        l_index++;
    }

    public void dealDiv() {
        if (l_index < line.length()) {
            getNextChar();
            if (ch == '/') {
                is_line_comment = true;
                return;
            } else if (ch == '*') {
                is_block_comment = true;
                eatBlockComment();
                return;
            }
            l_index--;
        }
        tokens.add(new Token("/", l_num, SyntaxKind.DIV));
    }

    public void dealOtherOp() {
        if (ch == '=') {
            if (l_index < line.length()) {
                getNextChar();
                if (ch == '=') {
                    tokens.add(new Token("==", l_num, SyntaxKind.EQL));
                    return;
                }
                l_index--;
            }
            tokens.add(new Token("=", l_num, SyntaxKind.ASSIGN));
        } else if (ch == '!') {
            if (l_index < line.length()) {
                getNextChar();
                if (ch == '=') {
                    tokens.add(new Token("!=", l_num, SyntaxKind.NEQ));
                    return;
                }
                l_index--;
            }
            tokens.add(new Token("!", l_num, SyntaxKind.NOT));
        } else if (ch == '<') {
            if (l_index < line.length()) {
                getNextChar();
                if (ch == '=') {
                    tokens.add(new Token("<=", l_num, SyntaxKind.LEQ));
                    return;
                }
                l_index--;
            }
            tokens.add(new Token("<", l_num, SyntaxKind.LESS));
        } else if (ch == '>') {
            if (l_index < line.length()) {
                getNextChar();
                if (ch == '=') {
                    tokens.add(new Token(">=", l_num, SyntaxKind.GEQ));
                    return;
                }
                l_index--;
            }
            tokens.add(new Token(">", l_num, SyntaxKind.GREATER));
        } else if (ch == '&') {
            if (l_index < line.length()) {
                getNextChar();
                if (ch == '&') {
                    tokens.add(new Token("&&", l_num, SyntaxKind.AND));
                    return;
                }
                l_index--;
            }
            tokens.add(new Token(l_num));
        } else if (ch == '|') {
            if (l_index < line.length()) {
                getNextChar();
                if (ch == '|') {
                    tokens.add(new Token("||", l_num, SyntaxKind.OR));
                    return;
                }
                l_index--;
            }
            tokens.add(new Token(l_num));
        }
    }

    public boolean isSpace() {
        return "\t\r\n ".indexOf(ch) != -1;
    }

    public boolean isLetter() {
        return ('a' <= ch && ch <= 'z') || ('A' <= ch && ch <= 'Z') || ch == '_';
    }

    public boolean isDigit() {
        return '0' <= ch && ch <= '9';
    }

    public boolean isReversed() {
        return reserved.containsKey(token);
    }

    public void eatBlockComment() {
        while (l_index < line.length()) {
            getNextChar();
            if (ch == '*' && l_index < line.length()) {
                getNextChar();
                if (ch == '/') {
                    is_block_comment = false;
                    return;
                }
                l_index--;
            }
        }
    }

    public ArrayList<Token> getTokens() {
        return tokens;
    }
}
