import java.util.ArrayList;

public class Node {
    private final Token token;
    private final int type;
    private final String content;
    private final int line;
    private Node parent;
    private final ArrayList<Node> children = new ArrayList<>();

    /* type == 0 ==> isEnd*/
    /* type == 1 ==> isNotEnd*/
    public Node(Token token) {
        this.token = token;
        this.type = 0;
        this.content = token.content;
        line = token.line;
    }

    public Node(String content, int line) {
        this.token = null;
        this.line = line;
        this.type = 1;
        this.content = content;
    }

    public Token getToken() {
        return token;
    }

    public void addChild(Node node) {
        node.addParent(this);
        children.add(node);
    }

    public void addParent(Node node) {
        parent = node;
    }

    public ArrayList<Node> getChildren() {
        return children;
    }

    public void setParent(Node parent) {
        this.parent = parent;
    }

    public Node getParent() {
        return parent;
    }

    public int getLine() {
        return line;
    }
}
