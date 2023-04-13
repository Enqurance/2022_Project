import java.util.ArrayList;
import java.util.HashSet;

public class Parser {
    private final ArrayList<Token> tokens = new ArrayList<>();
    private final HashSet<String> moduleClasses = new HashSet<>();
    private int tokenPtr;
    private Token curToken;
    private Node treeHead;

    public Parser(ArrayList<Token> tokens, HashSet<String> moduleClasses) {
        this.tokens.addAll(tokens);
        this.moduleClasses.addAll(moduleClasses);
        this.tokenPtr = 0;
        this.curToken = tokens.get(tokenPtr);
    }

    public void getToken() {
        if (tokenPtr < tokens.size() - 1) {
            tokenPtr++;
            this.curToken = tokens.get(tokenPtr);
        }
    }

    public void parse() {
        CompUnit();
    }

    public void CompUnit() {
        ArrayList<Node> children = new ArrayList<>();
        while (tokenPtr < tokens.size()) {
            if (curToken.kind.equals(SyntaxKind.MODULE_TK)) {
                Node mDecl = MDecl();
                children.add(mDecl);
            } else if (curToken.kind.equals(SyntaxKind.WIRE_TK)) {

            } else if (curToken.kind.equals(SyntaxKind.IDENTIFIER)) {

            }
            getToken();
        }
        if (!children.isEmpty()) {
            this.treeHead = new Node("<CompUnit>", children.get(0).getLine());
        }
    }

    public Node MDecl() {
        ArrayList<Node> children = new ArrayList<>();
        if (curToken.kind.equals(SyntaxKind.MODULE_TK)) {
            children.add(new Node(curToken));
            getToken();
            if (curToken.kind.equals(SyntaxKind.IDENTIFIER)) {
                children.add(new Node(curToken));
                getToken();
                if (curToken.kind.equals(SyntaxKind.L_PARENT)) {
                    children.add(new Node(curToken));
                    getToken();

                }
            }
        }
        Node node = new Node("<MDecl>", children.get(0).getLine());
        connect(children, node);
        return node;
    }

    public Node Direction() {
        ArrayList<Node> children = new ArrayList<>();
        if (curToken.kind.equals(SyntaxKind.INPUT_TK) ||
                curToken.kind.equals(SyntaxKind.OUTPUT_TK)) {
            children.add(new Node(curToken));
        }
        Node node = new Node("<Direction>", children.get(0).getLine());
        connect(children, node);
        return node;
    }

    public ArrayList<Token> getTokens() {
        return tokens;
    }

    public HashSet<String> getModuleClasses() {
        return moduleClasses;
    }

    public Node getTreeHead() {
        return treeHead;
    }

    public void connect(ArrayList<Node> children, Node parent) {
        for (Node item : children) {
            parent.addChild(item);
        }
    }
}
