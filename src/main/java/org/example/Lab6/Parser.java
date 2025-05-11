package org.example.Lab6;

import java.util.*;

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