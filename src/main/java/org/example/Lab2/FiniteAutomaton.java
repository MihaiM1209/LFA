package org.example.Lab2;

import java.util.*;



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

    // Print the details of the automaton
    public void printFA() {
        System.out.println("States: " + states);
        System.out.println("Alphabet: " + alphabet);
        System.out.println("Transitions: " + transitions);
        System.out.println("Start State: " + startState);
        System.out.println("Final States: " + finalStates);
    }
}
