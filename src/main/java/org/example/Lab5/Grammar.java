package org.example.Lab5;

import java.util.*;

public class Grammar {
    public static final String EPSILON = "ε";

    private List<String> nonTerminals;
    private List<String> terminals;
    private Map<String, List<String>> rules;

    private int newVarCounter = 0;

    public Grammar(List<String> nonTerminals, List<String> terminals, Map<String, List<String>> rules) {
        this.nonTerminals = new ArrayList<>(nonTerminals);
        this.terminals = new ArrayList<>(terminals);
        this.rules = new HashMap<>(rules);
    }

    public List<String> getNonTerminals() {
        return nonTerminals;
    }

    public List<String> getTerminals() {
        return terminals;
    }

    public Map<String, List<String>> getRules() {
        return rules;
    }

    public void printGrammar() {
        for (String nonTerminal : rules.keySet()) {
            List<String> productions = rules.get(nonTerminal);
            System.out.print(nonTerminal + " -> ");
            System.out.println(String.join(" | ", productions));
        }
    }

    public void eliminateEpsilonProductions() {
        Set<String> nullable = new HashSet<>();

        for (Map.Entry<String, List<String>> entry : rules.entrySet()) {
            if (entry.getValue().contains(EPSILON)) {
                nullable.add(entry.getKey());
            }
        }

        boolean changed;
        do {
            changed = false;
            for (String nt : nonTerminals) {
                for (String prod : rules.getOrDefault(nt, new ArrayList<>())) {
                    if (prod.chars().allMatch(c -> nullable.contains(String.valueOf((char) c)))) {
                        if (nullable.add(nt)) {
                            changed = true;
                        }
                    }
                }
            }
        } while (changed);

        Map<String, List<String>> newRules = new HashMap<>();

        for (String nt : nonTerminals) {
            Set<String> newProds = new HashSet<>();
            for (String prod : rules.get(nt)) {
                List<Integer> nullableIndices = new ArrayList<>();
                for (int i = 0; i < prod.length(); i++) {
                    if (nullable.contains(String.valueOf(prod.charAt(i)))) {
                        nullableIndices.add(i);
                    }
                }

                int combinations = 1 << nullableIndices.size();
                for (int mask = 0; mask < combinations; mask++) {
                    StringBuilder newProd = new StringBuilder(prod);
                    for (int i = nullableIndices.size() - 1; i >= 0; i--) {
                        if (((mask >> i) & 1) == 0) {
                            newProd.deleteCharAt(nullableIndices.get(i));
                        }
                    }
                    if (newProd.length() > 0) newProds.add(newProd.toString());
                }

                if (!prod.equals(EPSILON)) newProds.add(prod);
            }
            newRules.put(nt, new ArrayList<>(newProds));
        }

        rules = newRules;
    }

    public void eliminateRenaming() {
        Map<String, Set<String>> unitPairs = new HashMap<>();
        for (String nt : nonTerminals) {
            unitPairs.put(nt, new HashSet<>());
            unitPairs.get(nt).add(nt);
        }

        boolean changed;
        do {
            changed = false;
            for (String A : nonTerminals) {
                Set<String> toAdd = new HashSet<>();
                for (String B : unitPairs.get(A)) {
                    for (String C : rules.getOrDefault(B, new ArrayList<>())) {
                        if (nonTerminals.contains(C) && unitPairs.get(A).add(C)) {
                            toAdd.add(C);
                            changed = true;
                        }
                    }
                }
                unitPairs.get(A).addAll(toAdd);
            }
        } while (changed);

        Map<String, List<String>> newRules = new HashMap<>();
        for (String A : nonTerminals) {
            Set<String> newProds = new HashSet<>();
            for (String B : unitPairs.get(A)) {
                for (String prod : rules.getOrDefault(B, new ArrayList<>())) {
                    if (!nonTerminals.contains(prod)) {
                        newProds.add(prod);
                    }
                }
            }
            newRules.put(A, new ArrayList<>(newProds));
        }

        rules = newRules;
    }

    public void eliminateInaccessibleSymbols() {
        Set<String> accessible = new HashSet<>();
        Queue<String> queue = new LinkedList<>();
        queue.add("S");
        accessible.add("S");

        while (!queue.isEmpty()) {
            String nt = queue.poll();
            for (String prod : rules.getOrDefault(nt, new ArrayList<>())) {
                for (char c : prod.toCharArray()) {
                    String symbol = String.valueOf(c);
                    if (nonTerminals.contains(symbol) && accessible.add(symbol)) {
                        queue.add(symbol);
                    }
                }
            }
        }

        nonTerminals.removeIf(nt -> !accessible.contains(nt));
        rules.keySet().retainAll(accessible);
    }

    public void eliminateNonProductiveSymbols() {
        Set<String> productive = new HashSet<>();
        boolean changed;
        do {
            changed = false;
            for (String nt : nonTerminals) {
                for (String prod : rules.get(nt)) {
                    if (prod.chars().allMatch(c -> terminals.contains(String.valueOf((char) c)) || productive.contains(String.valueOf((char) c)))) {
                        if (productive.add(nt)) changed = true;
                    }
                }
            }
        } while (changed);

        nonTerminals.removeIf(nt -> !productive.contains(nt));
        rules.keySet().retainAll(productive);
    }

    public void convertToCNF() {
        Map<String, String> termToVar = new HashMap<>();
        Map<String, List<String>> newRules = new HashMap<>();

        // Create a copy to avoid ConcurrentModificationException
        Map<String, List<String>> rulesCopy = new HashMap<>(rules);

        for (String nt : rulesCopy.keySet()) {
            List<String> updatedProds = new ArrayList<>();
            for (String prod : rulesCopy.get(nt)) {
                if (prod.length() == 1 && terminals.contains(prod)) {
                    updatedProds.add(prod);
                } else {
                    String newProd = prod;
                    for (char c : prod.toCharArray()) {
                        String symbol = String.valueOf(c);
                        if (terminals.contains(symbol)) {
                            termToVar.putIfAbsent(symbol, getNewVariable());
                            newRules.putIfAbsent(termToVar.get(symbol), new ArrayList<>(Collections.singletonList(symbol)));
                            newProd = newProd.replace(symbol, termToVar.get(symbol));
                        }
                    }

                    while (newProd.length() > 2) {
                        String left = newProd.substring(0, 2);
                        String rest = newProd.substring(2);
                        String newVar = getNewVariable();
                        newRules.putIfAbsent(newVar, new ArrayList<>(Collections.singletonList(left)));
                        newProd = newVar + rest;
                    }

                    updatedProds.add(newProd);
                }
            }
            newRules.put(nt, updatedProds);
        }

        rules = newRules;
    }

    private String getNewVariable() {
        while (true) {
            String var = String.valueOf((char) ('A' + newVarCounter++));
            if (!nonTerminals.contains(var) && !terminals.contains(var)) {
                nonTerminals.add(var);
                return var;
            }
        }
    }

    public boolean isCNF() {
        for (String nt : rules.keySet()) {
            for (String prod : rules.get(nt)) {
                if (prod.equals(EPSILON)) continue;
                if (prod.length() == 1 && terminals.contains(prod)) continue;
                if (prod.length() == 2 && prod.chars().allMatch(c -> nonTerminals.contains(String.valueOf((char) c)))) continue;
                return false;
            }
        }
        return true;
    }

    public void toCNF(boolean printSteps) {
        if (printSteps) {
            System.out.println("\n1. After eliminating epsilon productions:");
        }
        eliminateEpsilonProductions();
        if (printSteps) printGrammar();

        if (printSteps) {
            System.out.println("\n2. After eliminating renaming productions:");
        }
        eliminateRenaming();
        if (printSteps) printGrammar();

        if (printSteps) {
            System.out.println("\n3. After eliminating inaccessible symbols:");
        }
        eliminateInaccessibleSymbols();
        if (printSteps) printGrammar();

        if (printSteps) {
            System.out.println("\n4. After eliminating non-productive symbols:");
        }
        eliminateNonProductiveSymbols();
        if (printSteps) printGrammar();

        if (printSteps) {
            System.out.println("\n5. After converting to CNF:");
        }
        convertToCNF();
        if (printSteps) printGrammar();
    }
}
