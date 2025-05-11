# Chomsky Normal Form

### Course: Formal Languages & Finite Automata

### Author: Mihai Mustea

## Theory
A Context-Free Grammar (CFG) is a formalism used in computer science and linguistics to describe the syntax of programming languages and structured expressions. A CFG consists of terminals (symbols that appear in the language), non-terminals (syntactic variables that define patterns of terminals), a start symbol, and a set of production rules that describe how terminals and non-terminals can be combined.

For many parsing algorithms—particularly the Cocke–Younger–Kasami (CYK) algorithm—the grammar needs to be in a specific format known as Chomsky Normal Form (CNF). In CNF, every production rule must follow one of these three forms: a non-terminal producing exactly two non-terminals (e.g., A → BC), a non-terminal producing a single terminal (e.g., A → a), or in certain cases, the start symbol producing the empty string (A → ε).

To convert any CFG into CNF, several transformation steps are required. The first step is to eliminate epsilon (ε) productions, which are rules that allow non-terminals to derive the empty string. Next, unit productions—rules where one non-terminal directly derives another (e.g., A → B)—are removed. Afterward, inaccessible symbols (symbols that cannot be reached from the start symbol) and non-productive symbols (symbols that cannot eventually produce terminal strings) are eliminated. Finally, the grammar is normalized so that all remaining rules conform to the CNF structure by replacing terminals in long rules with temporary variables and breaking down rules longer than two symbols into binary productions using helper variables.

This transformation makes the grammar suitable for efficient parsing while preserving the language it defines. The process is algorithmic and systematic, making it ideal for automation in compiler construction and formal language analysis.


## Objectives

The main objective of this project is to implement a system that can transform any given context-free grammar (CFG) into its equivalent **Chomsky Normal Form (CNF)** using Java. This transformation is essential for enabling parsing algorithms, such as CYK, which require grammars to be in CNF format.

To achieve this, the program is designed to carry out the following tasks:

* Parse and store the grammar components: non-terminals, terminals, and production rules.
* Eliminate **epsilon (ε) productions**, ensuring that nullable variables are handled correctly.
* Eliminate **unit (renaming) productions** where non-terminals derive other single non-terminals.
* Remove **inaccessible symbols** that cannot be reached from the start symbol.
* Remove **non-productive symbols** that do not contribute to terminal derivations.
* **Convert all remaining rules** into CNF by replacing terminals in complex rules and restructuring long productions into binary rules.
* Verify the correctness of the transformation through unit tests using JUnit.

The final output should be a CNF-compliant grammar that maintains the language equivalence of the original CFG.



## Implementation description

### Grammar Class

1. Constructor & Fields
```java
public class Grammar {
    private List<String> nonTerminals;
    private List<String> terminals;
    private Map<String, List<String>> rules;

    public Grammar(List<String> nonTerminals, List<String> terminals, Map<String, List<String>> rules) {
        this.nonTerminals = nonTerminals;
        this.terminals = terminals;
        this.rules = rules;
    }
```

This constructor initializes a Grammar object with a set of non-terminal symbols, terminal symbols, and production rules. Each rule maps a non-terminal to a list of its productions.


2. Printing the Grammar
```java
public void printGrammar() {
    for (String nonTerminal : rules.keySet()) {
        List<String> productions = rules.get(nonTerminal);
        System.out.println(nonTerminal + " -> " + String.join(" | ", productions));
    }
}
```
The printGrammar method prints each production rule in a readable format. It shows each non-terminal followed by its alternatives using |, just like in formal grammar notation.



3. Epsilon (ε) Elimination
```java
public void eliminateEpsilonProductions() {
    Set<String> nullable = new HashSet<>();
    for (String nt : rules.keySet()) {
        for (String prod : rules.get(nt)) {
            if (prod.equals("ε")) {
                nullable.add(nt);
                break;
            }
        }
    }
```
This snippet identifies nullable non-terminals — those that can derive ε (empty string). It's the first step in eliminating ε-productions.


```java
    for (String nt : rules.keySet()) {
        List<String> updated = new ArrayList<>();
        for (String prod : rules.get(nt)) {
            if (prod.equals("ε")) continue;
            Set<String> parts = new HashSet<>();
            parts.add(prod);
```
This section constructs all valid combinations of the production with nullable symbols removed (without ε itself). It ensures that grammar is preserved even after eliminating ε.



4. Unit Production Elimination
```java
public void eliminateRenaming() {
    Map<String, Set<String>> unitPairs = new HashMap<>();

    for (String A : nonTerminals) {
        unitPairs.put(A, new HashSet<>());
        unitPairs.get(A).add(A);
    }

    for (String A : nonTerminals) {
        for (String B : rules.get(A)) {
            if (nonTerminals.contains(B)) {
                unitPairs.get(A).add(B);
            }
        }
    }
```
This code builds a map of unit pairs, tracking which non-terminals are connected via unit productions (e.g., A -> B). It is part of the process of eliminating rules like A -> B that simply redirect to another non-terminal.



5. Removing Inaccessible Symbols
```java
public void eliminateInaccessibleSymbols() {
    Set<String> accessible = new HashSet<>();
    accessible.add("S");
    Queue<String> queue = new LinkedList<>();
    queue.add("S");
```
This section identifies accessible symbols — ones that can be reached starting from the start symbol. It avoids keeping rules and non-terminals that are isolated from the grammar.



6. CNF Conversion — Terminal Replacement
```java
if (production.length() > 1) {
    for (int i = 0; i < production.length(); i++) {
        char ch = production.charAt(i);
        String s = String.valueOf(ch);
        if (terminals.contains(s)) {
            String newVar = getNewVariable();
            termMapping.put(s, newVar);
            newRules.put(newVar, List.of(s));
            newProd.append(newVar);
        } else {
            newProd.append(s);
        }
    }
}
```
This part of the CNF conversion ensures that terminals do not appear in multi-symbol productions. It replaces them with new variables so all rules are in proper CNF form.



7. CNF Conversion — Breaking Long Productions
```java
while (newProd.length() > 2) {
    String first = newProd.substring(0, 1);
    String second = newProd.substring(1, 2);
    String rest = newProd.substring(2);

    String newVar = getNewVariable();
    newRules.put(newVar, List.of(first + second));
    newProd = newVar + rest;
}
```
This loop recursively breaks productions longer than 2 symbols into binary productions, which is a strict requirement of CNF.













