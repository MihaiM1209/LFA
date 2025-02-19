package org.example.Lab1;

import java.util.*;

public class FiniteAutomaton {
    // State class to represent each state in the FA
    private static class State {
        String name;
        Map<String, State> transitions;

        State(String name) {
            this.name = name;
            transitions = new HashMap<>();
        }

        void addTransition(String symbol, State nextState) {
            transitions.put(symbol, nextState);
        }

        State getTransition(String symbol) {
            return transitions.get(symbol);
        }
    }

    // FA components
    private final State startState;
    private final Set<State> acceptingStates;

    public FiniteAutomaton(Grammar grammar) {
        this.startState = new State("q0");
        this.acceptingStates = new HashSet<>();
        buildAutomaton(grammar);
    }

    private void buildAutomaton(Grammar grammar) {
        // Based on the grammar, we can set up the FA here.
        // Simplified construction of FA based on production rules (a more complete solution would need detailed states and transitions).

        // Let's assume a simplified model for the states.
        State q0 = startState;
        State q1 = new State("q1");
        State q2 = new State("q2");

        // Add transitions according to the grammar rules
        q0.addTransition("a", q1);
        q1.addTransition("b", q1);
        q1.addTransition("c", q2);
        q2.addTransition("d", q2);
        q2.addTransition("a", q0);

        // Add accepting states
        acceptingStates.add(q0);  // Example accepting state
    }

    // Method to check if a string is accepted by the FA
    public boolean isAccepted(String input) {
        State currentState = startState;
        for (char symbol : input.toCharArray()) {
            String symbolStr = String.valueOf(symbol);
            currentState = currentState.getTransition(symbolStr);
            if (currentState == null) {
                return false; // No transition exists
            }
        }
        return acceptingStates.contains(currentState);
    }
}
