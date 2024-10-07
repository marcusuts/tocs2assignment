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
            if (isNonTerminal(symbol)) {
                applyRule(symbol, root);
            } else {
                matchTerminal(symbol);
            }
        }
        return new ParseTree(root);
    }

    private static boolean isNonTerminal(String symbol) {
        return Arrays.asList("prog", "los", "stat", "whilestat", "forstat", "forstart", "forarith", 
                             "ifstat", "elseifstat", "elseorelseif", "possif", "assign", "decl", 
                             "possassign", "print", "type", "expr", "boolexpr", "boolop", "booleq", 
                             "boollog", "relexpr", "relexprprime", "relop", "arithexpr", 
                             "arithexprprime", "term", "termprime", "factor", "printexpr", 
                             "charexpr", "epsilon", "terminal").contains(symbol);
    }


    private static void applyRule(String nonTerminal, TreeNode parent) throws SyntaxException {
        switch (nonTerminal) {
            case "prog":
                expectToken(Token.TokenType.PUBLIC, parent);
                expectToken(Token.TokenType.CLASS, parent);
                expectToken(Token.TokenType.ID, parent); // ID for class name
                expectToken(Token.TokenType.LBRACE, parent);
                expectToken(Token.TokenType.PUBLIC, parent);
                expectToken(Token.TokenType.STATIC, parent);
                expectToken(Token.TokenType.VOID, parent);
                expectToken(Token.TokenType.MAIN, parent);
                expectToken(Token.TokenType.LPAREN, parent);
                expectToken(Token.TokenType.STRINGARR, parent); // 'String[]'
                expectToken(Token.TokenType.ARGS, parent); // 'args'
                expectToken(Token.TokenType.RPAREN, parent);
                expectToken(Token.TokenType.LBRACE, parent);
                parseStack.push(new Pair<>("los", parent));

		// error is happening here (using sample input in Runner.java)
                expectToken(Token.TokenType.RBRACE, parent);
                expectToken(Token.TokenType.RBRACE, parent);
                break;

            case "los":
                // System.out.println("HI");
                parseStack.push(new Pair<>("stat", parent));
                parseStack.push(new Pair<>("los", parent)); // Allows for multiple statements
                break;

            case "stat":
                // Handle different types of statements
                parseStack.push(new Pair<>("decl", parent));  // Variable declaration
                parseStack.push(new Pair<>("assign", parent)); // Variable assignment
                parseStack.push(new Pair<>("print", parent)); 
                parseStack.push(new Pair<>("ifstat", parent));
                parseStack.push(new Pair<>("whilestat", parent));
                parseStack.push(new Pair<>("forstat", parent));
                parseStack.push(new Pair<>("epsilon", parent)); // Represents a standalone semicolon
                break;

            case "whilestat":
                expectToken(Token.TokenType.WHILE, parent);
                expectToken(Token.TokenType.LPAREN, parent);
                parseStack.push(new Pair<>("boolexpr", parent));
                expectToken(Token.TokenType.RPAREN, parent);
                expectToken(Token.TokenType.LBRACE, parent);
                parseStack.push(new Pair<>("los", parent));
                expectToken(Token.TokenType.RBRACE, parent);
                break;

            case "forstat":
                expectToken(Token.TokenType.FOR, parent);
                expectToken(Token.TokenType.LPAREN, parent);
                parseStack.push(new Pair<>("forstart", parent));
                expectToken(Token.TokenType.SEMICOLON, parent);
                parseStack.push(new Pair<>("boolexpr", parent));
                expectToken(Token.TokenType.SEMICOLON, parent);
                parseStack.push(new Pair<>("forarith", parent));
                expectToken(Token.TokenType.RPAREN, parent);
                expectToken(Token.TokenType.LBRACE, parent);
                parseStack.push(new Pair<>("los", parent));
                expectToken(Token.TokenType.RBRACE, parent);
                break;

            case "forstart":
                parseStack.push(new Pair<>("decl", parent)); // Variable declaration
                // Or process an assignment or ε (empty)
                parseStack.push(new Pair<>("assign", parent));
                break;

            case "forarith":
                parseStack.push(new Pair<>("arithexpr", parent));
                break;

            case "ifstat":
                expectToken(Token.TokenType.IF, parent);
                expectToken(Token.TokenType.LPAREN, parent);
                parseStack.push(new Pair<>("boolexpr", parent));
                expectToken(Token.TokenType.RPAREN, parent);
                expectToken(Token.TokenType.LBRACE, parent);
                parseStack.push(new Pair<>("los", parent));
                expectToken(Token.TokenType.RBRACE, parent);
                parseStack.push(new Pair<>("elseifstat", parent));
                break;

            case "elseifstat":
                parseStack.push(new Pair<>("elseorelseif", parent));
                break;

            case "elseorelseif":
                expectToken(Token.TokenType.ELSE, parent);
                parseStack.push(new Pair<>("possif", parent));
                break;

            case "possif":
                expectToken(Token.TokenType.IF, parent);
                expectToken(Token.TokenType.LPAREN, parent);
                parseStack.push(new Pair<>("boolexpr", parent));
                expectToken(Token.TokenType.RPAREN, parent);
                expectToken(Token.TokenType.LBRACE, parent);
                parseStack.push(new Pair<>("los", parent));
                expectToken(Token.TokenType.RBRACE, parent);
                break;

            case "assign":
                expectToken(Token.TokenType.ID, parent); // Variable name
                expectToken(Token.TokenType.ASSIGN, parent);
                parseStack.push(new Pair<>("expr", parent));
                expectToken(Token.TokenType.SEMICOLON, parent);
                break;

            case "decl":
                parseStack.push(new Pair<>("type", parent)); // Variable type
                expectToken(Token.TokenType.ID, parent); // Variable name
                parseStack.push(new Pair<>("possassign", parent));
                expectToken(Token.TokenType.SEMICOLON, parent);
                break;

            case "possassign":
                parseStack.push(new Pair<>("assign", parent)); // Check for assignment
                break;

            case "print":
                expectToken(Token.TokenType.PRINT, parent);
                expectToken(Token.TokenType.LPAREN, parent);
                parseStack.push(new Pair<>("printexpr", parent));
                expectToken(Token.TokenType.RPAREN, parent);
                expectToken(Token.TokenType.SEMICOLON, parent);
                break;

            case "type":
                expectToken(Token.TokenType.TYPE, parent);
                break;

            case "expr":
                parseStack.push(new Pair<>("relexpr", parent));
                break;

            case "boolexpr":
                parseStack.push(new Pair<>("boolop", parent));
                parseStack.push(new Pair<>("relexpr", parent));
                break;

            case "relexpr":
                parseStack.push(new Pair<>("arithexpr", parent));
                parseStack.push(new Pair<>("relexprprime", parent));
                break;

            case "relexprprime":
                parseStack.push(new Pair<>("relop", parent));
                parseStack.push(new Pair<>("arithexpr", parent));
                break;

            case "relop":
                expectToken(Token.TokenType.EQUAL, parent); // == operator
                parseStack.push(new Pair<>("boolop", parent));
                break;

            case "arithexpr":
                parseStack.push(new Pair<>("term", parent));
                parseStack.push(new Pair<>("arithexprprime", parent));
                break;

            case "arithexprprime":
                expectToken(Token.TokenType.PLUS, parent);
                expectToken(Token.TokenType.MINUS, parent);
                parseStack.push(new Pair<>("term", parent));
                parseStack.push(new Pair<>("arithexprprime", parent));
                break;

            case "term":
                parseStack.push(new Pair<>("factor", parent));
                parseStack.push(new Pair<>("termprime", parent));
                break;

            case "termprime":
                expectToken(Token.TokenType.TIMES, parent);
                expectToken(Token.TokenType.DIVIDE, parent);
                expectToken(Token.TokenType.MOD, parent);
                parseStack.push(new Pair<>("factor", parent));
                parseStack.push(new Pair<>("termprime", parent));
                break;

            case "factor":
                expectToken(Token.TokenType.LPAREN, parent);
                parseStack.push(new Pair<>("arithexpr", parent));
                expectToken(Token.TokenType.RPAREN, parent);
                parseStack.push(new Pair<>("ID", parent));
                parseStack.push(new Pair<>("NUM", parent)); // For numeric literals
                break;

            case "printexpr":
                parseStack.push(new Pair<>("relexpr", parent));
                expectToken(Token.TokenType.STRINGLIT, parent); // For string literals
                break;

            default:
                throw new SyntaxException("Unknown non-terminal: " + nonTerminal);
        }
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

// The following class may be helpful.

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
