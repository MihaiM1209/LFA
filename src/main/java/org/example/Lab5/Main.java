package org.example.Lab5;

import java.util.*;

public class Main {
    public static void main(String[] args) {
        List<String> nonTerminals = Arrays.asList("S", "A", "B", "C");
        List<String> terminals = Arrays.asList("a", "d");

        Map<String, List<String>> rules = new HashMap<>();
        rules.put("S", Arrays.asList("dB", "A"));
        rules.put("A", Arrays.asList("d", "dS", "aBdAB"));
        rules.put("B", Arrays.asList("a", "dA", "A", Grammar.EPSILON));
        rules.put("C", Arrays.asList("Aa"));

        Grammar grammar = new Grammar(nonTerminals, terminals, rules);

        System.out.println("Original Grammar:");
        grammar.printGrammar();

        grammar.toCNF(true);  // Pass `true` to print each CNF conversion step
    }
}
