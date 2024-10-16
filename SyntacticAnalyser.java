import java.util.ArrayDeque;
import java.util.Deque;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Arrays;

public class SyntacticAnalyser {

    private static Map<Pair<String, Token.TokenType>, String[]> parsingTable = new HashMap<>();

    static {
        parsingTable.put(new Pair<>("prog", Token.TokenType.PUBLIC), new String[]{"PUBLIC", "CLASS", "ID", "LBRACE", "PUBLIC", "STATIC", "VOID", "MAIN", "LPAREN", "STRINGARR", "ARGS", "RPAREN", "LBRACE", "los", "RBRACE", "RBRACE"});
        
        parsingTable.put(new Pair<>("los", Token.TokenType.WHILE), new String[]{"stat", "los"});
        parsingTable.put(new Pair<>("los", Token.TokenType.FOR), new String[]{"stat", "los"});
        parsingTable.put(new Pair<>("los", Token.TokenType.IF), new String[]{"stat", "los"});
        parsingTable.put(new Pair<>("los", Token.TokenType.ID), new String[]{"stat", "los"});
        parsingTable.put(new Pair<>("los", Token.TokenType.TYPE), new String[]{"stat", "los"});
        parsingTable.put(new Pair<>("los", Token.TokenType.PRINT), new String[]{"stat", "los"});
        parsingTable.put(new Pair<>("los", Token.TokenType.SEMICOLON), new String[]{"stat", "los"});
        parsingTable.put(new Pair<>("los", Token.TokenType.RBRACE), new String[]{"ε"});

        parsingTable.put(new Pair<>("stat", Token.TokenType.TYPE), new String[]{"decl", "SEMICOLON"});
        parsingTable.put(new Pair<>("stat", Token.TokenType.PRINT), new String[]{"print"});
        parsingTable.put(new Pair<>("stat", Token.TokenType.WHILE), new String[]{"whilestat"});
        parsingTable.put(new Pair<>("stat", Token.TokenType.FOR), new String[]{"forstat"});
        parsingTable.put(new Pair<>("stat", Token.TokenType.IF), new String[]{"ifstat"});
        parsingTable.put(new Pair<>("stat", Token.TokenType.ID), new String[]{"assign", "SEMICOLON"});
        parsingTable.put(new Pair<>("stat", Token.TokenType.SEMICOLON), new String[]{"SEMICOLON"});

        parsingTable.put(new Pair<>("whilestat", Token.TokenType.WHILE), new String[]{"WHILE", "LPAREN", "relexpr", "boolexpr", "RPAREN", "LBRACE", "los", "RBRACE"});

        parsingTable.put(new Pair<>("forstat", Token.TokenType.FOR), new String[]{"FOR", "LPAREN", "forstart", "SEMICOLON", "relexpr", "boolexpr", "SEMICOLON", "forarith", "RPAREN", "LBRACE", "los", "RBRACE"});

        parsingTable.put(new Pair<>("forstart", Token.TokenType.TYPE), new String[]{"decl"});
        parsingTable.put(new Pair<>("forstart", Token.TokenType.ID), new String[]{"assign"});
        parsingTable.put(new Pair<>("forstart", Token.TokenType.SEMICOLON), new String[]{"ε"});

        parsingTable.put(new Pair<>("forarith", Token.TokenType.ID), new String[]{"arithexpr"});
        parsingTable.put(new Pair<>("forarith", Token.TokenType.NUM), new String[]{"arithexpr"});
        parsingTable.put(new Pair<>("forarith", Token.TokenType.LPAREN), new String[]{"arithexpr"});
        parsingTable.put(new Pair<>("forarith", Token.TokenType.RPAREN), new String[]{"ε"});

        parsingTable.put(new Pair<>("ifstat", Token.TokenType.IF), new String[]{"IF", "LPAREN", "relexpr", "boolexpr", "RPAREN", "LBRACE", "los", "RBRACE", "elseifstat"});

        parsingTable.put(new Pair<>("elseifstat", Token.TokenType.TYPE), new String[]{"ε"});
        parsingTable.put(new Pair<>("elseifstat", Token.TokenType.PRINT), new String[]{"ε"});
        parsingTable.put(new Pair<>("elseifstat", Token.TokenType.WHILE), new String[]{"ε"});
        parsingTable.put(new Pair<>("elseifstat", Token.TokenType.FOR), new String[]{"ε"});
        parsingTable.put(new Pair<>("elseifstat", Token.TokenType.IF), new String[]{"ε"});
        parsingTable.put(new Pair<>("elseifstat", Token.TokenType.ELSE), new String[]{"elseorelseif", "LBRACE", "los", "RBRACE", "elseifstat"});
        parsingTable.put(new Pair<>("elseifstat", Token.TokenType.RBRACE), new String[]{"ε"});
        parsingTable.put(new Pair<>("elseifstat", Token.TokenType.SEMICOLON), new String[]{"ε"});

        parsingTable.put(new Pair<>("elseorelseif", Token.TokenType.ELSE), new String[]{"ELSE", "possif"});
        parsingTable.put(new Pair<>("possif", Token.TokenType.IF), new String[]{"if", "LPAREN", "relexpr", "boolexpr", "RPAREN", "ε"});
        parsingTable.put(new Pair<>("possif", Token.TokenType.LBRACE), new String[]{"ε"});

        parsingTable.put(new Pair<>("assign", Token.TokenType.ID), new String[]{"ID", "assign", "expr"});

        parsingTable.put(new Pair<>("decl", Token.TokenType.TYPE), new String[]{"type", "ID", "possassign"});

        parsingTable.put(new Pair<>("possassign", Token.TokenType.ASSIGN), new String[]{"ASSIGN", "expr"});
        parsingTable.put(new Pair<>("possassign", Token.TokenType.SEMICOLON), new String[]{"ε"});

        parsingTable.put(new Pair<>("print", Token.TokenType.PRINT), new String[]{"PRINT", "LPAREN", "printexpr", "RPAREN"});

        parsingTable.put(new Pair<>("type", Token.TokenType.TYPE), new String[]{"TYPE"});

        parsingTable.put(new Pair<>("expr", Token.TokenType.ID), new String[]{"relexpr", "boolexpr"});
        parsingTable.put(new Pair<>("expr", Token.TokenType.TRUE), new String[]{"relexpr", "boolexpr"});
        parsingTable.put(new Pair<>("expr", Token.TokenType.FALSE), new String[]{"relexpr", "boolexpr"});
        parsingTable.put(new Pair<>("expr", Token.TokenType.NUM), new String[]{"relexpr", "boolexpr"});
        parsingTable.put(new Pair<>("expr", Token.TokenType.LPAREN), new String[]{"relexpr", "boolexpr"});
        parsingTable.put(new Pair<>("expr", Token.TokenType.SQUOTE), new String[]{"charexpr"});

        parsingTable.put(new Pair<>("charexpr", Token.TokenType.SQUOTE), new String[]{"SQUOTE", "CHARLIT", "SQUOTE"});

        parsingTable.put(new Pair<>("boolexpr", Token.TokenType.EQUAL), new String[]{"boolop", "relexpr", "boolexpr"});
        parsingTable.put(new Pair<>("boolexpr", Token.TokenType.NEQUAL), new String[]{"boolop", "relexpr", "boolexpr"});
        parsingTable.put(new Pair<>("boolexpr", Token.TokenType.AND), new String[]{"boolop", "relexpr", "boolexpr"});
        parsingTable.put(new Pair<>("boolexpr", Token.TokenType.OR), new String[]{"boolop", "relexpr", "boolexpr"});
        parsingTable.put(new Pair<>("boolexpr", Token.TokenType.SEMICOLON), new String[]{"ε"});
        parsingTable.put(new Pair<>("boolexpr", Token.TokenType.RPAREN), new String[]{"ε"});

        parsingTable.put(new Pair<>("boolop", Token.TokenType.EQUAL), new String[]{"booleq"});
        parsingTable.put(new Pair<>("boolop", Token.TokenType.NEQUAL), new String[]{"booleq"});
        parsingTable.put(new Pair<>("boolop", Token.TokenType.AND), new String[]{"booleq"});
        parsingTable.put(new Pair<>("boolop", Token.TokenType.OR), new String[]{"boollog"});

        parsingTable.put(new Pair<>("booleq", Token.TokenType.EQUAL), new String[]{"EQUAL"});
        parsingTable.put(new Pair<>("booleq", Token.TokenType.NEQUAL), new String[]{"NEQUAL"});

        parsingTable.put(new Pair<>("boollog", Token.TokenType.AND), new String[]{"AND"});
        parsingTable.put(new Pair<>("boollog", Token.TokenType.OR), new String[]{"OR"});

        parsingTable.put(new Pair<>("relexpr", Token.TokenType.LPAREN), new String[]{"arithexpr", "relexprprime"});
        parsingTable.put(new Pair<>("relexpr", Token.TokenType.ID), new String[]{"arithexpr", "relexprprime"});
        parsingTable.put(new Pair<>("relexpr", Token.TokenType.TRUE), new String[]{"TRUE"});
        parsingTable.put(new Pair<>("relexpr", Token.TokenType.FALSE), new String[]{"FALSE"});
        parsingTable.put(new Pair<>("relexpr", Token.TokenType.NUM), new String[]{"arithexpr", "relexprprime"});

        parsingTable.put(new Pair<>("relexprprime", Token.TokenType.EQUAL), new String[]{"ε"});
        parsingTable.put(new Pair<>("relexprprime", Token.TokenType.NEQUAL), new String[]{"ε"});
        parsingTable.put(new Pair<>("relexprprime", Token.TokenType.LT), new String[]{"relop", "arithexpr"});
        parsingTable.put(new Pair<>("relexprprime", Token.TokenType.GT), new String[]{"relop", "arithexpr"});
        parsingTable.put(new Pair<>("relexprprime", Token.TokenType.LE), new String[]{"relop", "arithexpr"});
        parsingTable.put(new Pair<>("relexprprime", Token.TokenType.GE), new String[]{"relop", "arithexpr"});
        parsingTable.put(new Pair<>("relexprprime", Token.TokenType.RPAREN), new String[]{"ε"});
        parsingTable.put(new Pair<>("relexprprime", Token.TokenType.AND), new String[]{"ε"});
        parsingTable.put(new Pair<>("relexprprime", Token.TokenType.OR), new String[]{"ε"});
        parsingTable.put(new Pair<>("relexprprime", Token.TokenType.SEMICOLON), new String[]{"ε"});

        parsingTable.put(new Pair<>("relop", Token.TokenType.LT), new String[]{"LT"});
        parsingTable.put(new Pair<>("relop", Token.TokenType.GT), new String[]{"GT"});
        parsingTable.put(new Pair<>("relop", Token.TokenType.LE), new String[]{"LE"});
        parsingTable.put(new Pair<>("relop", Token.TokenType.GE), new String[]{"GE"});

        parsingTable.put(new Pair<>("arithexpr", Token.TokenType.ID), new String[]{"term", "arithexprprime"});
        parsingTable.put(new Pair<>("arithexpr", Token.TokenType.NUM), new String[]{"term", "arithexprprime"});
        parsingTable.put(new Pair<>("arithexpr", Token.TokenType.LPAREN), new String[]{"term", "arithexprprime"});

        parsingTable.put(new Pair<>("arithexprprime", Token.TokenType.PLUS), new String[]{"PLUS", "term", "arithexprprime"});
        parsingTable.put(new Pair<>("arithexprprime", Token.TokenType.MINUS), new String[]{"MINUS", "term", "arithexprprime"});
        parsingTable.put(new Pair<>("arithexprprime", Token.TokenType.RPAREN), new String[]{"ε"});
        parsingTable.put(new Pair<>("arithexprprime", Token.TokenType.EQUAL), new String[]{"ε"});
        parsingTable.put(new Pair<>("arithexprprime", Token.TokenType.NEQUAL), new String[]{"ε"});
        parsingTable.put(new Pair<>("arithexprprime", Token.TokenType.LT), new String[]{"ε"});
        parsingTable.put(new Pair<>("arithexprprime", Token.TokenType.GT), new String[]{"ε"});
        parsingTable.put(new Pair<>("arithexprprime", Token.TokenType.LE), new String[]{"ε"});
        parsingTable.put(new Pair<>("arithexprprime", Token.TokenType.GE), new String[]{"ε"});
        parsingTable.put(new Pair<>("arithexprprime", Token.TokenType.RPAREN), new String[]{"ε"});
        parsingTable.put(new Pair<>("arithexprprime", Token.TokenType.AND), new String[]{"ε"});
        parsingTable.put(new Pair<>("arithexprprime", Token.TokenType.OR), new String[]{"ε"});
        parsingTable.put(new Pair<>("arithexprprime", Token.TokenType.SEMICOLON), new String[]{"ε"});

        parsingTable.put(new Pair<>("term", Token.TokenType.ID), new String[]{"factor", "termprime"});
        parsingTable.put(new Pair<>("term", Token.TokenType.NUM), new String[]{"factor", "termprime"});
        parsingTable.put(new Pair<>("term", Token.TokenType.LPAREN), new String[]{"factor", "termprime"});

        parsingTable.put(new Pair<>("termprime", Token.TokenType.TIMES), new String[]{"TIMES", "factor", "termprime"});
        parsingTable.put(new Pair<>("termprime", Token.TokenType.DIVIDE), new String[]{"DIVIDE", "factor", "termprime"});
        parsingTable.put(new Pair<>("termprime", Token.TokenType.MOD), new String[]{"MOD", "factor", "termprime"});
        parsingTable.put(new Pair<>("termprime", Token.TokenType.RPAREN), new String[]{"ε"});
        parsingTable.put(new Pair<>("termprime", Token.TokenType.EQUAL), new String[]{"ε"});
        parsingTable.put(new Pair<>("termprime", Token.TokenType.NEQUAL), new String[]{"ε"});
        parsingTable.put(new Pair<>("termprime", Token.TokenType.LT), new String[]{"ε"});
        parsingTable.put(new Pair<>("termprime", Token.TokenType.GT), new String[]{"ε"});
        parsingTable.put(new Pair<>("termprime", Token.TokenType.LE), new String[]{"ε"});
        parsingTable.put(new Pair<>("termprime", Token.TokenType.GE), new String[]{"ε"});
        parsingTable.put(new Pair<>("termprime", Token.TokenType.RPAREN), new String[]{"ε"});
        parsingTable.put(new Pair<>("termprime", Token.TokenType.AND), new String[]{"ε"});
        parsingTable.put(new Pair<>("termprime", Token.TokenType.OR), new String[]{"ε"});
        parsingTable.put(new Pair<>("termprime", Token.TokenType.SEMICOLON), new String[]{"ε"});
        parsingTable.put(new Pair<>("termprime", Token.TokenType.PLUS), new String[]{"ε"});
        parsingTable.put(new Pair<>("termprime", Token.TokenType.MINUS), new String[]{"ε"});

        parsingTable.put(new Pair<>("factor", Token.TokenType.ID), new String[]{"ID"});
        parsingTable.put(new Pair<>("factor", Token.TokenType.NUM), new String[]{"NUM"});
        parsingTable.put(new Pair<>("factor", Token.TokenType.LPAREN), new String[]{"LPAREN", "arithexpr", "RPAREN"});

        parsingTable.put(new Pair<>("printexpr", Token.TokenType.DQUOTE), new String[]{"DQUOTE", "STRINGLIT", "DQUOTE"});
        parsingTable.put(new Pair<>("printexpr", Token.TokenType.ID), new String[]{"relexpr", "boolexpr"});
        parsingTable.put(new Pair<>("printexpr", Token.TokenType.TRUE), new String[]{"relexpr", "boolexpr"});
        parsingTable.put(new Pair<>("printexpr", Token.TokenType.FALSE), new String[]{"relexpr", "boolexpr"});
        parsingTable.put(new Pair<>("printexpr", Token.TokenType.NUM), new String[]{"relexpr", "boolexpr"});
        parsingTable.put(new Pair<>("printexpr", Token.TokenType.LPAREN), new String[]{"relexpr", "boolexpr"});
    }
 
    public static ParseTree parse(List<Token> tokens) throws SyntaxException {
        Deque<Pair<String, TreeNode>> stack = new ArrayDeque<>();
        Deque<TreeNode> parentStack = new ArrayDeque<>();

        ParseTree parseTree = new ParseTree();
        TreeNode progNode = new TreeNode(TreeNode.Label.prog, null); // Create the root node
        parseTree.setRoot(progNode);
        parentStack.push(progNode); // Add root to the parent stack
        stack.push(new Pair<>("prog", progNode)); // Push the start symbol onto the stack

        int i = 0; // Index for tokens
        while (!stack.isEmpty() && i < tokens.size()) {
            Pair<String, TreeNode> top = stack.pop(); // Pop the top of the stack
            TreeNode currentParent = top.snd(); // Get the current parent node from stack
            Token currentToken = tokens.get(i); // Get current token

            // Skip ε (epsilon) productions
            if (top.fst().equals("ε")) {
                TreeNode epsilonNode = new TreeNode(TreeNode.Label.epsilon, currentParent);
                currentParent.addChild(epsilonNode);
                continue; // Skip epsilon productions
            }

            if (isNonTerminal(top.fst())) {
                System.out.println("Expanding non-terminal: " + top.fst());

                String[] production = parsingTable.get(new Pair<>(top.fst(), currentToken.getType()));
                if (production != null) {
                    TreeNode nonTerminalNode;
                    if (top.fst().equals("prog")) {
                        nonTerminalNode = progNode; // Use the existing root node for "prog"
                    } else {
                        nonTerminalNode = new TreeNode(TreeNode.Label.valueOf(top.fst()), currentParent);
                        currentParent.addChild(nonTerminalNode);
                    }

                    // Push children of the current non-terminal to the stack in reverse order
                    for (int j = production.length - 1; j >= 0; j--) {
                        stack.push(new Pair<>(production[j], nonTerminalNode));
                    }

                    System.out.println("Production for " + top.fst() + " expanded: " + Arrays.toString(production));
                } else {
                    System.err.println("No production rule found for: " + top.fst() + " with token " + currentToken);
                    throw new SyntaxException("No production rule for: " + top.fst() + " with token " + currentToken);
                }
            } else {  // Terminal
                System.out.println("Matching terminal: " + top.fst());
                if (top.fst().equals(currentToken.getType().toString())) {
                    TreeNode terminalNode = new TreeNode(TreeNode.Label.terminal, currentToken, currentParent);
                    currentParent.addChild(terminalNode);
                    i++; // Move to the next token
                } else {
                    System.err.println("Unexpected token found: " + currentToken + ", expected: " + top.fst());
                    throw new SyntaxException("Unexpected token: " + currentToken);
                }
            }
        }

        // If any tokens remain or the stack isn't empty, it's an error
        if (i < tokens.size()) {
            System.err.println("Input not fully consumed, remaining tokens: " + tokens.subList(i, tokens.size()));
            throw new SyntaxException("Input not fully consumed");
        }
        if (!stack.isEmpty()) {
            System.err.println("Stack not empty after processing, stack: " + stack);
            throw new SyntaxException("Stack not empty after processing");
        }

        return parseTree; // Return the constructed parse tree
    }

    private static boolean isTerminal(String symbol) {
        // Check if the symbol is a terminal token type
        return !isNonTerminal(symbol);
    }

    private static boolean isNonTerminal(String symbol) {
        // Check if the symbol is a non-terminal by checking the parsing table
        return parsingTable.keySet().stream().anyMatch(pair -> pair.fst().equals(symbol));
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
