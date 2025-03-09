Formal Languages & Finite Automata – Variant 22

### Author: Mustea Mihai
### FAF-231

## Theory
A formal language is a set of strings made from symbols in a finite alphabet, defined by rules called a grammar. Grammars are categorized into four types (Chomsky hierarchy), with regular grammars (Type-3) being the simplest.

A finite automaton (FA) is a simple machine that processes strings and decides whether they belong to a language. It has states, transitions, a start state, and accept states.

Finite automata and regular grammars are equivalent: any regular grammar can be converted into a finite automaton, and vice versa. This makes FAs essential for tasks like pattern matching, lexical analysis, and designing compilers.


This project includes:
A grammar definition and string generation.
Conversion of grammar to a finite automaton.
Checking if a string is accepted by the automaton.


## Grammar Variant 22:
VN = {S, D, F}   (Non-terminals)  
VT = {a, b, c, d}  (Terminals)  
P  = {
    S → aS     
    S → bS    
    S → cD   
    D → dD    
    D → bF  
    D → a
    F → bS
    F → a
}

## Structure:
📂 src/main/java/org/example  
 ┣ 📜 Main.java       
 ┣ 📜 Grammar.java   
 ┗ 📜 FiniteAutomaton.java  


Generated strings:
aS
bS
cD

Check if strings are accepted:
Is 'ab' accepted? false
Is 'cdb' accepted? false
Is 'abca' accepted? true
Is 'dba' accepted? false


## Generating Strings from the Grammar
```
public List<String> generateStrings(int count) {
    List<String> generatedStrings = new ArrayList<>();
    Random random = new Random();

    for (int i = 0; i < count; i++) {
        StringBuilder str = new StringBuilder("S"); // Start from the initial symbol
        while (str.toString().matches(".*[SDF].*")) { // Expand until no non-terminals are left
            for (int j = 0; j < str.length(); j++) {
                char symbol = str.charAt(j);
                if (grammarRules.containsKey(symbol)) {
                    List<String> productions = grammarRules.get(symbol);
                    str.replace(j, j + 1, productions.get(random.nextInt(productions.size())));
                    break;
                }
            }
        }
        generatedStrings.add(str.toString());
    }
    return generatedStrings;
}
```
How it works:
The convertToFiniteAutomaton() method transforms a context-free grammar into a finite automaton by converting its production rules into state transitions. It begins by creating an empty finite automaton and then iterates through each non-terminal in the grammar. For each production rule, it determines whether it consists only of a terminal or a terminal followed by a non-terminal. If the rule contains only a terminal, it creates a transition leading to a special final state, ‘X’. If the rule consists of a terminal followed by a non-terminal, it establishes a transition between the corresponding states. Once all rules are processed, the method returns the completed finite automaton, which can now recognize valid sequences based on the grammar.


## Converting Grammar to a Finite Automaton
```
public FiniteAutomaton convertToFiniteAutomaton() {
    FiniteAutomaton fa = new FiniteAutomaton();

    for (char nonTerminal : grammarRules.keySet()) {
        for (String production : grammarRules.get(nonTerminal)) {
            if (production.length() == 1) { // Terminal only
                fa.addTransition(nonTerminal, production.charAt(0), 'X'); // X is final state
            } else { // Terminal followed by a non-terminal
                fa.addTransition(nonTerminal, production.charAt(0), production.charAt(1));
            }
        }
    }
    return fa;
}
```
How it works:
The generateStrings(int count) method creates random strings based on a given grammar. It starts by initializing an empty list to store the generated strings and begins each string with the starting symbol ‘S’. The method then expands this symbol by continuously replacing non-terminals with randomly chosen production rules from the grammar. This process continues until no non-terminals remain in the string, ensuring that only terminals are left. Once a fully formed string is generated, it is added to the list. The method repeats this process until the desired number of strings is generated. Finally, it returns the list of randomly generated strings, providing different variations based on the grammar’s rules.


## Checking if a String is Accepted by the Automaton
```
public boolean isAccepted(String input) {
    char currentState = 'S'; // Start state

    for (char symbol : input.toCharArray()) {
        if (!transitions.containsKey(currentState) || !transitions.get(currentState).containsKey(symbol)) {
            return false; // No valid transition
        }
        currentState = transitions.get(currentState).get(symbol); // Move to next state
    }

    return finalStates.contains(currentState); // Check if in final state
}
```
How it works:
The isAccepted(String input) method checks whether a given input string is accepted by the finite automaton. It begins by setting the current state to the start state, represented by the character ‘S’. The method then processes each symbol in the input string one by one. For each symbol, it checks if there is a valid transition from the current state to the next state. If no valid transition exists, the method immediately returns false, indicating that the string is not accepted. If a valid transition exists, the automaton moves to the next state. After processing all symbols in the input, the method checks if the automaton is in one of its final states. If it is, the string is accepted and the method returns true; otherwise, it returns false. This process ensures that the string is valid according to the finite automaton’s transitions and final states.



## Conclusions
In conclusion, the methods we’ve discussed collectively form a robust system for simulating and manipulating a finite automaton based on a context-free grammar. The convertToFiniteAutomaton() method bridges the gap between a grammar and an automaton by transforming production rules into state transitions, allowing the automaton to process valid strings. On the other hand, the generateStrings(int count) method allows for the random generation of strings based on the grammar, showcasing the grammar's ability to produce different valid sequences. Finally, the isAccepted(String input) method serves as the key to validating whether a given string can be processed by the automaton, ensuring it follows the transitions and ends in an acceptable state. Together, these methods provide a complete framework for converting, generating, and validating strings within the context of formal language theory.
