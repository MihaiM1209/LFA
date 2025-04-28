package org.example.Lab5;

import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

import java.util.*;

public class TestGrammar {

    private Grammar setupGrammar() {
        List<String> nonTerminals = Arrays.asList("S", "A", "B", "C");
        List<String> terminals = Arrays.asList("a", "d");

        Map<String, List<String>> rules = new HashMap<>();
        rules.put("S", Arrays.asList("dB", "A"));
        rules.put("A", Arrays.asList("d", "dS", "aBdAB"));
        rules.put("B", Arrays.asList("a", "dA", "A", Grammar.EPSILON));
        rules.put("C", Arrays.asList("Aa"));

        return new Grammar(nonTerminals, terminals, rules);
    }

    @Test
    public void testInitialGrammar() {
        Grammar g = setupGrammar();
        assertEquals(4, g.getNonTerminals().size());
    }

    @Test
    public void testToCNF() {
        Grammar g = setupGrammar();
        g.toCNF(false);
        assertTrue(g.isCNF());
    }
}
