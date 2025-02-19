package org.example.Lab1;

import java.util.List;  // Explicit import for List
import java.util.*;     // Import other necessary utilities like HashSet, Arrays, etc.

public class Main {
    public static void main(String[] args) {
        // Step 1: Create the Grammar object
        Grammar grammar = new Grammar();

        // Step 2: Generate 5 valid strings from the grammar
        List<String> generatedStrings = grammar.generateStrings("S", 5); // Generate strings from the Grammar
        System.out.println("Generated strings:");
        for (String str : generatedStrings) {
            System.out.println(str); // Print generated strings
        }

        // Step 3: Create Finite Automaton from Grammar
        FiniteAutomaton fa = new FiniteAutomaton(grammar); // Pass grammar to FA constructor

        // Step 4: Check if some input strings are accepted by the FA
        System.out.println("\nCheck if strings are accepted:");
        String[] testStrings = {"ab", "cdb", "abca", "dba"};
        for (String test : testStrings) {
            System.out.println("Is '" + test + "' accepted? " + fa.isAccepted(test)); // FA checks if string is accepted
        }
    }
}
