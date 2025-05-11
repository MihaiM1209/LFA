package org.example.Lab6;

import java.io.*;
import java.nio.file.*;
import java.util.*;

public class Main {
    public static void main(String[] args) throws IOException {
        String input = Files.readString(Path.of("src/main/java/org/example/Lab6/SampleInput.txt"));
        List<Token> tokens = DSLTokenizer.tokenize(input);
        Parser parser = new Parser(tokens);
        ASTNode ast = parser.parse();
        ASTPrinter.print(ast);
    }
}
