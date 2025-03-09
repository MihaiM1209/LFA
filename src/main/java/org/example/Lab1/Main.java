package org.example.Lab1;

import java.util.List;



public class Main {
    public static void main(String[] args) {

        Grammar grammar = new Grammar();


        List<String> generatedStrings = grammar.generateStrings("S", 5); // Generate strings from the Grammar
        System.out.println("Generated strings:");
        for (String str : generatedStrings) {
            System.out.println(str);


        }


        FiniteAutomaton fa = new FiniteAutomaton(grammar);


        System.out.println("\nCheck if strings are accepted:");
        String[] testStrings = {"ab", "cdb", "abca", "dba"};
        for (String test : testStrings) {
            System.out.println("Is '" + test + "' accepted? " + fa.isAccepted(test)); // FA checks if string is accepted
        }
    }
}
