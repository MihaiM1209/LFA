# Determinism in Finite Automata. Conversion from NDFA 2 DFA. Chomsky Hierarchy.

### Course: Formal Languages & Finite Automata

### Author: Mustea Mihai

## Theory
This project is an implementation of a Finite Automaton that supports the conversion of a Non-Deterministic Finite Automaton (NDFA) to a Deterministic Finite Automaton (DFA). It is developed in Java as part of the study of Formal Languages & Finite Automata.

## Features

Define a finite automaton with states, alphabet, transitions, start state, and final states.

Determine whether a given finite automaton is deterministic or non-deterministic.

Convert an NDFA to a DFA using subset construction.

Print the structure of both the NDFA and the resulting DFA.

## Implementation Description

## 1. Main Program (Main.java)

The Main.java file serves as the entry point for the program. It defines an NDFA using a set of states, an alphabet, a transition function, a start state, and final states. Then, it checks if the NDFA is deterministic and prints the result. Finally, it calls the DFAConverter class to convert the NDFA to a DFA and prints the resulting DFA.

Key Parts:

1. States and Transitions: The NDFA is defined using sets of states, a set of input symbols (alphabet), and a transition table which maps each state  and symbol to a set of destination states.
2. Determinism Check: The program checks if the NDFA is deterministic using the isDeterministic() method, which ensures that no state has more than one possible destination for a given input symbol.
3. Conversion to DFA: The NDFA is then converted into a DFA using the DFAConverter.convertNDFAtoDFA() method.

```java
public class Main {
    public static void main(String[] args) {
        Set<String> states = new HashSet<>(Arrays.asList("q0", "q1", "q2"));
        Set<Character> alphabet = new HashSet<>(Arrays.asList('a', 'b'));

        Map<String, Map<Character, Set<String>>> transitions = new HashMap<>();
        transitions.put("q0", new HashMap<>());
        transitions.get("q0").put('a', new HashSet<>(Arrays.asList("q0")));
        transitions.get("q0").put('b', new HashSet<>(Arrays.asList("q1")));

        transitions.put("q1", new HashMap<>());
        transitions.get("q1").put('a', new HashSet<>(Arrays.asList("q0")));
        transitions.get("q1").put('b', new HashSet<>(Arrays.asList("q1", "q2")));

        transitions.put("q2", new HashMap<>());
        transitions.get("q2").put('b', new HashSet<>(Arrays.asList("q1")));

        String startState = "q0";
        Set<String> finalStates = new HashSet<>(Collections.singletonList("q2"));

        FiniteAutomaton ndfa = new FiniteAutomaton(states, alphabet, transitions, startState, finalStates);
        ndfa.printFA();
        System.out.println("Is Deterministic: " + ndfa.isDeterministic());

        FiniteAutomaton dfa = DFAConverter.convertNDFAtoDFA(ndfa);
        System.out.println("\nConverted DFA:");
        dfa.printFA();
    }
}
```

## 2. Finite Automaton Class (FiniteAutomaton.java)
The FiniteAutomaton class represents a finite automaton (either NDFA or DFA). It stores the states, alphabet, transitions, start state, and final states of the automaton. It also contains a method to check if the automaton is deterministic and a method to print the automaton’s details.

Key Methods:

1. isDeterministic(): This method checks whether the automaton is deterministic. It iterates over the transitions and checks if any state has more than one possible transition for the same input symbol.
2. printFA(): This method prints out the components of the automaton, such as states, alphabet, transitions, start state, and final states.

```java
class FiniteAutomaton {
    protected Set<String> states;
    protected Set<Character> alphabet;
    protected Map<String, Map<Character, Set<String>>> transitions;
    protected String startState;
    protected Set<String> finalStates;

    public FiniteAutomaton(Set<String> states, Set<Character> alphabet,
                           Map<String, Map<Character, Set<String>>> transitions,
                           String startState, Set<String> finalStates) {
        this.states = states;
        this.alphabet = alphabet;
        this.transitions = transitions;
        this.startState = startState;
        this.finalStates = finalStates;
    }

    public boolean isDeterministic() {
        for (Map<Character, Set<String>> stateTransitions : transitions.values()) {
            for (Set<String> destinationStates : stateTransitions.values()) {
                if (destinationStates.size() > 1) {
                    return false;
                }
            }
        }
        return true;
    }

    public void printFA() {
        System.out.println("States: " + states);
        System.out.println("Alphabet: " + alphabet);
        System.out.println("Transitions: " + transitions);
        System.out.println("Start State: " + startState);
        System.out.println("Final States: " + finalStates);
    }
}
```

## 3. DFA Converter Class (DFAConverter.java)
The DFAConverter class is responsible for converting the NDFA to a DFA. The conversion is done using the subset construction method, which involves:

1. Creating a new state for each possible combination of NDFA states.
2. Creating transitions based on the NDFA transitions.
3. Assigning the start state and final states of the DFA.

Key Methods:

1. convertNDFAtoDFA(): This method converts the NDFA to a DFA by applying the subset construction algorithm. It generates the new DFA states, transitions, and determines the final states.
2. epsilonClosure(): This is a helper function used to compute the epsilon closure of a set of states (though in this example, it doesn't handle epsilon transitions as there are none).
3. convertMapFormat(): This helper function converts the map format used for DFA transitions.


```java
class DFAConverter {
    public static FiniteAutomaton convertNDFAtoDFA(FiniteAutomaton ndfa) {
        Set<Set<String>> dfaStates = new HashSet<>();
        Map<Set<String>, String> stateNames = new HashMap<>();
        Map<String, Map<Character, String>> dfaTransitions = new HashMap<>();
        Queue<Set<String>> queue = new LinkedList<>();

        Set<String> startSet = epsilonClosure(new HashSet<>(Collections.singleton(ndfa.startState)), ndfa);
        queue.add(startSet);
        dfaStates.add(startSet);
        stateNames.put(startSet, String.join("", new TreeSet<>(startSet)));

        while (!queue.isEmpty()) {
            Set<String> currentSet = queue.poll();
            String currentStateName = stateNames.get(currentSet);
            dfaTransitions.put(currentStateName, new HashMap<>());

            for (char symbol : ndfa.alphabet) {
                Set<String> newStateSet = new HashSet<>();
                for (String state : currentSet) {
                    if (ndfa.transitions.containsKey(state) && ndfa.transitions.get(state).containsKey(symbol)) {
                        newStateSet.addAll(ndfa.transitions.get(state).get(symbol));
                    }
                }
                newStateSet = epsilonClosure(newStateSet, ndfa);
                if (!newStateSet.isEmpty()) {
                    if (!stateNames.containsKey(newStateSet)) {
                        queue.add(newStateSet);
                        dfaStates.add(newStateSet);
                        stateNames.put(newStateSet, String.join("", new TreeSet<>(newStateSet)));
                    }
                    dfaTransitions.get(currentStateName).put(symbol, stateNames.get(newStateSet));
                }
            }
        }

        Set<String> dfaFinalStates = new HashSet<>();
        for (Set<String> stateSet : dfaStates) {
            for (String state : stateSet) {
                if (ndfa.finalStates.contains(state)) {
                    dfaFinalStates.add(stateNames.get(stateSet));
                    break;
                }
            }
        }

        return new FiniteAutomaton(new HashSet<>(stateNames.values()), ndfa.alphabet, convertMapFormat(dfaTransitions), stateNames.get(startSet), dfaFinalStates);
    }
}
```

## Dependencies

This project uses standard Java libraries, so no additional dependencies are required.

#### Output Example
```
States: [q0, q1, q2]
Alphabet: [a, b]
Transitions: {q0={a=[q0], b=[q1]}, q1={a=[q0], b=[q1, q2]}, q2={b=[q1]}}
Start State: q0
Final States: [q2]
Is Deterministic: false

Converted DFA:
States: [q1, q1q2, q0]
Alphabet: [a, b]
Transitions: {q1={a=[q0], b=[q1q2]}, q1q2={a=[q0], b=[q1q2]}, q0={a=[q0], b=[q1]}}
Start State: q0
Final States: [q1q2]
```

## Conclusion

This project successfully demonstrates the process of converting a non-deterministic finite automaton (NDFA) to a deterministic finite automaton (DFA). The conversion is achieved using the subset construction algorithm, which creates new DFA states that represent sets of NDFA states. This allows the resulting DFA to simulate the behavior of the NDFA while ensuring determinism.

Key points include:

1. NDFA vs DFA: The NDFA allows multiple transitions for the same symbol, while the DFA ensures a single, deterministic transition for each symbol.
2. Subset Construction: This technique creates a DFA by mapping each subset of NDFA states to a new DFA state.
3. Determinism Check: The program verifies whether the input NDFA is deterministic before attempting the conversion.
4. Practical Use: Converting NDFA to DFA is essential for applications like lexical analysis in compilers, where deterministic machines are preferred for their efficiency.
5. While the current implementation is functional, potential improvements include handling epsilon transitions, optimizing the DFA with state minimization, and adding visualizations of the automata.

This project enhances understanding of automata theory and is a valuable tool for anyone working with finite automata and formal languages.



