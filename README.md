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
Starts with the initial non-terminal S. Expands using production rules until all non-terminals are replaced. Returns fully generated strings from the language.


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
Converts each production rule into state transitions. If a rule leads to a terminal, it creates a transition to a final state (X). If a rule contains a non-terminal, it creates a transition between states.



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
Starts from state S. Moves through valid state transitions using input characters. If the last state is final, the string is accepted.



## Conclusions
This project successfully implements regular grammars and their conversion to finite automata, demonstrating key concepts in formal languages. It allows string generation, grammar-to-automaton conversion, and string validation using state transitions. The structured approach ensures clarity and efficiency, making it a solid foundation for further exploration in automata theory and computational linguistics. 


