import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.InputStreamReader;

public class VerilogCompiler {
    private final String file;

    VerilogCompiler(String fileName) {
        file = fileName;
    }

    public void compiling() {
        Lexer.getInstance().initialize();
        readFile(Lexer.getInstance());
    }

    private void readFile(Lexer lexer) {
        try {
            FileInputStream in = new FileInputStream(file);
            InputStreamReader inReader = new InputStreamReader(in);
            BufferedReader bufReader = new BufferedReader(inReader);

            String line;
            while ((line = bufReader.readLine()) != null) {
                lexer.updateTokens(line);
            }
            lexer.getTokens().add(new Token("END", 0, SyntaxKind.EOF));

            bufReader.close();
            inReader.close();
            in.close();
        } catch (Exception e) {
            e.printStackTrace();
            System.out.println(file + " read error!");
        }
    }

    private class Parser {
        
    }
}
