package org.example.Lab6;

import java.util.*;

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
