# DSL Parser Project

### Course: Formal Languages & Finite Automata

### Author: Mihai Mustea

## Theory
This project implements a simple parser for a Domain-Specific Language (DSL) designed to describe network devices and their properties. The main goal is to process structured textual input, tokenize it into meaningful components, and then build an Abstract Syntax Tree (AST) representing the hierarchical structure of the input.

Key concepts used:

Tokenization: Breaking down input text into atomic pieces called tokens (e.g., identifiers, numbers, IP addresses).

Parsing: Organizing tokens into a tree-like structure (AST) that represents the syntax and semantics of the input.

Abstract Syntax Tree (AST): A data structure that captures the grammatical structure of the input for further processing or analysis.


## Objectives

1. Develop a tokenizer to identify various token types such as IPv4 addresses, MAC addresses, strings, numbers, and identifiers.

2. Implement a parser that consumes tokens and constructs an AST representing devices and their properties.

3. Design the AST nodes to hold type and value information, supporting multiple children nodes for nested structures.

4. Provide a mechanism to print or traverse the AST for visualization or debugging.

5. Process sample input files and generate the corresponding AST to verify the parser's correctness.



## Implementation Description

## 1. ASTNode (ASTNode.java)
The project consists of several main components that work together to tokenize, parse, and represent the input DSL data as an Abstract Syntax Tree (AST):

```java
public class ASTNode {
    public final String type;
    public final String value;
    public final List<ASTNode> children = new ArrayList<>();

    public ASTNode(String type, String value) {
        this.type = type;
        this.value = value;
    }

    public void addChild(ASTNode child) {
        children.add(child);
    }

    public void print(String indent) {
        System.out.println(indent + type + ": " + value);
        for (ASTNode child : children) {
            child.print(indent + "  ");
        }
    }
}
```
This class represents nodes of the AST. Each node has a type and a value, and can contain multiple child nodes to build a hierarchical tree structure.

type: Describes the kind of AST node (e.g., "Device", "Name", "Property").

value: The actual value or text associated with the node.

children: List of child AST nodes to represent nested structures.

print(): Recursively prints the tree for debugging or visualization.


## 2. DSLTokenizer (DSLTokenizer.java)
This class is responsible for breaking the input string into meaningful tokens using regular expressions.
```java
public class DSLTokenizer {
    private static final List<Pattern> tokenPatterns = new ArrayList<>();
    private static final Map<Pattern, TokenType> tokenTypes = new HashMap<>();

    static {
        addToken(TokenType.IPV4_ADDRESS, "(?:\\d{1,3}\\.){3}\\d{1,3}");
        addToken(TokenType.MAC_ADDRESS, "[0-9A-Fa-f]{4}\\.[0-9A-Fa-f]{4}\\.[0-9A-Fa-f]{4}");
        addToken(TokenType.STRING, "\"[^\"]*\"");
        addToken(TokenType.NUMBER, "\\d+");
        addToken(TokenType.ID, "[a-zA-Z_][a-zA-Z0-9_-]*");
    }

    private static void addToken(TokenType type, String regex) {
        Pattern pattern = Pattern.compile("^" + regex);
        tokenPatterns.add(pattern);
        tokenTypes.put(pattern, type);
    }

    public static List<Token> tokenize(String input) {
        List<Token> tokens = new ArrayList<>();
        String remaining = input.trim();

        while (!remaining.isEmpty()) {
            boolean matched = false;

            for (Pattern pattern : tokenPatterns) {
                Matcher matcher = pattern.matcher(remaining);
                if (matcher.find()) {
                    String value = matcher.group();
                    tokens.add(new Token(tokenTypes.get(pattern), value));
                    remaining = remaining.substring(value.length()).trim();
                    matched = true;
                    break;
                }
            }

            if (!matched) {
                tokens.add(new Token(TokenType.UNKNOWN, remaining.substring(0, 1)));
                remaining = remaining.substring(1).trim();
            }
        }

        return tokens;
    }
}
```

Patterns are compiled and matched sequentially against the input.

Tokens recognized include IP addresses, MAC addresses, strings, numbers, and identifiers.

Unrecognized characters are marked as UNKNOWN.

## 3. Parser (Parser.java)
This class consumes the token list and builds the AST by applying grammar rules.
```java
public class Parser {
    private final List<Token> tokens;
    private int position = 0;

    public Parser(List<Token> tokens) {
        this.tokens = tokens;
    }

    public ASTNode parse() {
        ASTNode root = new ASTNode("Program", "");
        while (position < tokens.size()) {
            Token token = tokens.get(position);
            if (token.value.equals("device")) {
                root.addChild(parseDevice());
            } else {
                position++;
            }
        }
        return root;
    }

    private ASTNode parseDevice() {
        ASTNode device = new ASTNode("Device", "");
        position++; // skip "device"

        if (match(TokenType.ID)) {
            device.addChild(new ASTNode("Name", consume().value));
        }

        while (position < tokens.size() && !tokens.get(position).value.equals("device")) {
            if (match(TokenType.ID)) {
                device.addChild(new ASTNode("Property", consume().value));
            } else {
                position++;
            }
        }

        return device;
    }

    private boolean match(TokenType expected) {
        return position < tokens.size() && tokens.get(position).type == expected;
    }

    private Token consume() {
        return tokens.get(position++);
    }
}
```

The parser identifies device blocks and parses their properties.

Builds an AST subtree for each device, with children representing the device name and properties.

Uses helper methods match and consume to safely navigate the tokens.


## 4. Main (Main.java)
This is the program's entry point that orchestrates the flow: read input, tokenize, parse, and print the AST.
```java
public class Main {
    public static void main(String[] args) throws IOException {
        String input = Files.readString(Path.of("src/main/java/org/example/Lab6/SampleInput.txt"));
        List<Token> tokens = DSLTokenizer.tokenize(input);
        Parser parser = new Parser(tokens);
        ASTNode ast = parser.parse();
        ASTPrinter.print(ast);
    }
}
```

Reads input DSL text from SampleInput.txt.

Tokenizes the input to produce tokens.

Parses the tokens to generate the AST.

Prints the AST for user inspection.

# Conclusion
This project effectively showcases the fundamental principles of language processing by implementing a tokenizer and parser for a domain-specific language tailored to network device descriptions. Through the systematic breakdown of input into tokens and the subsequent construction of an Abstract Syntax Tree (AST), it provides a clear and structured representation of the input data, enabling easier analysis and manipulation.

The modular design of the project separates concerns between tokenization and parsing, which enhances maintainability and extensibility. The tokenizer leverages regular expressions to identify a diverse set of tokens such as IPv4 addresses, MAC addresses, strings, numbers, and identifiers, demonstrating the practical use of pattern matching in lexical analysis. The parser applies syntactic rules to organize these tokens into meaningful hierarchical structures that accurately represent devices and their associated properties within the DSL.

By building and traversing the AST, the project enables visualization of the parsed input, which is essential for debugging and further development such as semantic analysis or code generation. This step mirrors key processes in compiler construction and language interpretation, giving valuable insights into how programming languages and interpreters function under the hood.

Moreover, this foundational framework opens the door for future enhancements. For instance, the DSL can be extended with more complex grammar rules, additional token types, or integrated with tools that perform validation, transformation, or even execution of the described configurations. This flexibility illustrates the power and utility of abstract syntax trees as an intermediate representation in many software domains.

In summary, the project serves as both an educational exercise in compiler theory and a practical tool for processing and understanding structured domain-specific inputs. It lays a strong groundwork for anyone interested in language design, compiler development, or DSL implementation, highlighting the critical steps from raw input to structured data representation.




