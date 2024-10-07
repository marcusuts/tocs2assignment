import java.util.ArrayDeque;
import java.util.Arrays;
import java.util.Deque;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class SyntacticAnalyser {
    private static Deque<Pair<String, TreeNode>> parseStack;
    private static List<Token> tokens;
    private static int currentIndex = 0;
    private static Token currentToken;

    // Define the LL(1) parse table
    private static final Map<String, String[]> parseTable = new HashMap<>();

    static {
        parseTable.put("prog", new String[]{
            "PUBLIC", "CLASS", "ID", "LBRACE", "PUBLIC", "STATIC", "VOID", "MAIN",
            "LPAREN", "STRINGARR", "ARGS", "RPAREN", "LBRACE", "los", "RBRACE", "RBRACE"
        });
        parseTable.put("los", new String[]{
            "stat", "los", "epsilon" // Allows for multiple statements
        });
        parseTable.put("stat", new String[]{
            "decl", "assign", "print", "ifstat", "whilestat", "forstat", "epsilon"
        });
        parseTable.put("whilestat", new String[]{
            "WHILE", "LPAREN", "boolexpr", "RPAREN", "LBRACE", "los", "RBRACE"
        });
        parseTable.put("forstat", new String[]{
            "FOR", "LPAREN", "forstart", "SEMICOLON", "boolexpr", "SEMICOLON", "forarith", "RPAREN", "LBRACE", "los", "RBRACE"
        });
        parseTable.put("forstart", new String[]{
            "decl", "assign"
        });
        parseTable.put("forarith", new String[]{
            "arithexpr"
        });
        parseTable.put("ifstat", new String[]{
            "IF", "LPAREN", "boolexpr", "RPAREN", "LBRACE", "los", "RBRACE", "elseifstat"
        });
        parseTable.put("elseifstat", new String[]{
            "elseorelseif"
        });
        parseTable.put("elseorelseif", new String[]{
            "ELSE", "possif"
        });
        parseTable.put("possif", new String[]{
            "IF", "LPAREN", "boolexpr", "RPAREN", "LBRACE", "los", "RBRACE"
        });
        parseTable.put("assign", new String[]{
            "ID", "ASSIGN", "expr", "SEMICOLON"
        });
        parseTable.put("decl", new String[]{
            "type", "ID", "possassign", "SEMICOLON"
        });
        parseTable.put("possassign", new String[]{
            "ASSIGN", "expr", "epsilon" // "assign"
        });
        parseTable.put("print", new String[]{
            "PRINT", "LPAREN", "printexpr", "RPAREN", "SEMICOLON"
        });
        parseTable.put("type", new String[]{
            "TYPE"
        });
        parseTable.put("expr", new String[]{
            "relexpr"
        });
        parseTable.put("boolexpr", new String[]{
            "boolop", "relexpr"
        });
        parseTable.put("relexpr", new String[]{
            "arithexpr", "relexprprime"
        });
        parseTable.put("relexprprime", new String[]{
            "relop", "arithexpr"
        });
        parseTable.put("relop", new String[]{
            "EQUAL"
        });
        parseTable.put("arithexpr", new String[]{
            "term", "arithexprprime"
        });
        parseTable.put("arithexprprime", new String[]{
            "PLUS", "MINUS", "term", "arithexprprime"
        });
        parseTable.put("term", new String[]{
            "factor", "termprime"
        });
        parseTable.put("termprime", new String[]{
            "TIMES", "DIVIDE", "MOD", "factor", "termprime"
        });
        parseTable.put("factor", new String[]{
            "LPAREN", "arithexpr", "RPAREN", "ID", "NUM"
        });
        parseTable.put("printexpr", new String[]{
            "relexpr", "STRINGLIT"
        });
    }

    public static ParseTree parse(List<Token> inputTokens) throws SyntaxException {
        tokens = inputTokens;
        currentIndex = 0;
        currentToken = tokens.get(currentIndex);
        parseStack = new ArrayDeque<>();
        parseStack.push(new Pair<>("prog", null));
        TreeNode root = new TreeNode(TreeNode.Label.prog, null);

        while (!parseStack.isEmpty()) {
            Pair<String, TreeNode> top = parseStack.pop();
            String symbol = top.fst();
            System.out.println(symbol);

            if (isNonTerminal(symbol)) {
                // Check the parse table for the production rule
                String[] productionRule = parseTable.get(symbol);
                if (productionRule != null) {
                    for (int i = productionRule.length - 1; i >= 0; i--) {
                        parseStack.push(new Pair<>(productionRule[i], top.snd()));
                    }
                } else {
                    throw new SyntaxException("No production found for non-terminal: " + symbol);
                }
            } else {
                matchTerminal(symbol);
            }
        }
        return new ParseTree(root);
    }

    private static boolean isNonTerminal(String symbol) {
        return parseTable.containsKey(symbol);
    }

    private static void matchTerminal(String terminal) throws SyntaxException {
        if (currentToken.getType().name().equals(terminal)) {
            advanceToken();
        } else {
            throw new SyntaxException("Expected token: " + terminal + " but found " + currentToken.getType());
        }
    }

    private static void advanceToken() {
        currentIndex++;
        if (currentIndex < tokens.size()) {
            currentToken = tokens.get(currentIndex);
        }
    }

    private static void expectToken(Token.TokenType expectedType, TreeNode parent) throws SyntaxException {
        if (currentToken.getType() == expectedType) {
            parent.addChild(new TreeNode(TreeNode.Label.terminal, currentToken, parent));
            advanceToken();
        } else {
            throw new SyntaxException("Expected " + expectedType + " but found " + currentToken.getType());
        }
    }
}

// Helper Pair class remains unchanged
class Pair<A, B> {
    private final A a;
    private final B b;

    public Pair(A a, B b) {
        this.a = a;
        this.b = b;
    }

    public A fst() {
        return a;
    }

    public B snd() {
        return b;
    }

    @Override
    public int hashCode() {
        return 3 * a.hashCode() + 7 * b.hashCode();
    }

    @Override
    public String toString() {
        return "{" + a + ", " + b + "}";
    }

    @Override
    public boolean equals(Object o) {
        if ((o instanceof Pair<?, ?>)) {
            Pair<?, ?> other = (Pair<?, ?>) o;
            return other.fst().equals(a) && other.snd().equals(b);
        }
        return false;
    }
}
