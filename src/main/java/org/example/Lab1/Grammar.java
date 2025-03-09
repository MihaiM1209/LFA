package org.example.Lab1;

import java.util.*;

public class Grammar {

    private static final String S = "S";
    private static final String D = "D";
    private static final String F = "F";


    private static final Set<String> terminals = new HashSet<>(Arrays.asList("a", "b", "c", "d"));


    private final Map<String, List<String>> productions;

    public Grammar() {
        productions = new HashMap<>();

        productions.put(S, Arrays.asList("aS", "bS", "cD"));
        productions.put(D, Arrays.asList("dD", "bF", "a"));
        productions.put(F, Arrays.asList("bS", "a"));
    }


    public List<String> generateStrings(String nonTerminal, int maxDepth) {
        List<String> result = new ArrayList<>();
        generateRecursive(nonTerminal, "", result, maxDepth, 0);
        return result;
    }


    private void generateRecursive(String nonTerminal, String currentString, List<String> result, int maxDepth, int depth) {
        if (depth >= maxDepth) return;

        if (!productions.containsKey(nonTerminal)) {

            result.add(currentString + nonTerminal);
            return;
        }

        for (String production : productions.get(nonTerminal)) {
            generateRecursive(production, currentString, result, maxDepth, depth + 1);
        }
    }
}
