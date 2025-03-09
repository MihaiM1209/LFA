package org.example.Lab2;

import java.util.*;

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
        transitions.get("q1").put('b', new HashSet<>(Arrays.asList("q1", "q2"))); // NDFA case

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
