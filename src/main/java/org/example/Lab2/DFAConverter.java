package org.example.Lab2;

import java.util.*;

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

    private static Set<String> epsilonClosure(Set<String> states, FiniteAutomaton ndfa) {
        return states; // Assuming no epsilon transitions, return states as-is
    }

    private static Map<String, Map<Character, Set<String>>> convertMapFormat(Map<String, Map<Character, String>> input) {
        Map<String, Map<Character, Set<String>>> output = new HashMap<>();
        for (Map.Entry<String, Map<Character, String>> entry : input.entrySet()) {
            output.put(entry.getKey(), new HashMap<>());
            for (Map.Entry<Character, String> innerEntry : entry.getValue().entrySet()) {
                output.get(entry.getKey()).put(innerEntry.getKey(), new HashSet<>(Collections.singleton(innerEntry.getValue())));
            }
        }
        return output;
    }
}