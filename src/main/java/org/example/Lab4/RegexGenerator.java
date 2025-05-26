package org.example.Lab4;

import java.util.*;
import java.util.stream.Collectors;

public class RegexGenerator {

    // Base class for parse tree nodes
    public static abstract class Node {
        public abstract List<String> generate();
        public abstract MatchResult match(String s, int pos);
    }

    // Result of matching: explanation steps, new position, success flag
    public static class MatchResult {
        public final List<String> explanation;
        public final int newPos;
        public final boolean success;

        public MatchResult(List<String> explanation, int newPos, boolean success) {
            this.explanation = explanation;
            this.newPos = newPos;
            this.success = success;
        }
    }

    // Literal node
    public static class LiteralNode extends Node {
        public final String value;
        public LiteralNode(String value) { this.value = value; }

        @Override
        public List<String> generate() {
            return Collections.singletonList(value);
        }

        @Override
        public MatchResult match(String s, int pos) {
            if (s.startsWith(value, pos)) {
                return new MatchResult(
                        Collections.singletonList("Matched literal '" + value + "' at position " + pos),
                        pos + value.length(),
                        true
                );
            } else {
                return new MatchResult(
                        Collections.singletonList("Failed to match literal '" + value + "' at position " + pos),
                        pos,
                        false
                );
            }
        }
    }

    // Sequence node
    public static class SequenceNode extends Node {
        public final List<Node> elements;
        public SequenceNode(List<Node> elements) { this.elements = elements; }

        @Override
        public List<String> generate() {
            List<String> result = Collections.singletonList("");
            for (Node elem : elements) {
                List<String> subs = elem.generate();
                List<String> newResult = new ArrayList<>();
                for (String prefix : result) {
                    for (String sub : subs) {
                        newResult.add(prefix + sub);
                    }
                }
                result = newResult;
            }
            return result;
        }

        @Override
        public MatchResult match(String s, int pos) {
            List<String> explanation = new ArrayList<>();
            int currentPos = pos;
            for (int i = 0; i < elements.size(); i++) {
                MatchResult res = elements.get(i).match(s, currentPos);
                explanation.addAll(res.explanation);
                if (!res.success) {
                    return new MatchResult(explanation, res.newPos, false);
                }
                currentPos = res.newPos;
            }
            return new MatchResult(explanation, currentPos, true);
        }
    }

    // Alternation node
    public static class AlternationNode extends Node {
        public final List<Node> options;
        public AlternationNode(List<Node> options) { this.options = options; }

        @Override
        public List<String> generate() {
            List<String> results = new ArrayList<>();
            for (Node option : options) {
                results.addAll(option.generate());
            }
            return results;
        }

        @Override
        public MatchResult match(String s, int pos) {
            List<String> explanation = new ArrayList<>();
            List<List<String>> altExplanations = new ArrayList<>();
            for (Node option : options) {
                MatchResult res = option.match(s, pos);
                if (res.success) {
                    explanation.add("Matched alternation option at position " + pos);
                    explanation.addAll(res.explanation);
                    return new MatchResult(explanation, res.newPos, true);
                } else {
                    altExplanations.add(res.explanation);
                }
            }
            explanation.add("Failed to match any alternation option at position " + pos);
            for (List<String> altExp : altExplanations) {
                explanation.addAll(altExp);
            }
            return new MatchResult(explanation, pos, false);
        }
    }

    // Repeat node
    public static class RepeatNode extends Node {
        public final Node node;
        public final int min;
        public final int max;

        public RepeatNode(Node node, int min, int max) {
            this.node = node;
            this.min = min;
            this.max = max;
        }

        @Override
        public List<String> generate() {
            List<String> subs = node.generate();
            List<String> results = new ArrayList<>();
            for (int count = min; count <= max; count++) {
                if (count == 0) {
                    results.add("");
                } else {
                    List<String> temp = Collections.singletonList("");
                    for (int i = 0; i < count; i++) {
                        List<String> newTemp = new ArrayList<>();
                        for (String prefix : temp) {
                            for (String sub : subs) {
                                newTemp.add(prefix + sub);
                            }
                        }
                        temp = newTemp;
                    }
                    results.addAll(temp);
                }
            }
            return results;
        }

        @Override
        public MatchResult match(String s, int pos) {
            int count = 0;
            List<String> explanation = new ArrayList<>();
            int currentPos = pos;

            while (count < max) {
                MatchResult res = node.match(s, currentPos);
                if (res.success && res.newPos > currentPos) {
                    explanation.addAll(res.explanation);
                    count++;
                    currentPos = res.newPos;
                } else {
                    break;
                }
            }
            if (count < min) {
                explanation.add("Repeat failed: expected at least " + min + " matches but got " + count + " at position " + pos);
                return new MatchResult(explanation, currentPos, false);
            }
            explanation.add("Matched repeat node " + count + " times from position " + pos + " to " + currentPos);
            return new MatchResult(explanation, currentPos, true);
        }
    }

    // Parsing functions

    private String s;
    private int pos;

    public RegexGenerator(String s) {
        this.s = s;
        this.pos = 0;
    }

    private Node parseExpression(Set<Character> stopChars) throws Exception {
        List<Node> alternatives = new ArrayList<>();
        List<Node> sequence = new ArrayList<>();

        while (pos < s.length() && !stopChars.contains(s.charAt(pos))) {
            char c = s.charAt(pos);
            if (c == '|') {
                alternatives.add(sequenceToNode(sequence));
                sequence = new ArrayList<>();
                pos++; // skip '|'
            } else {
                Node node = parseAtom();
                sequence.add(node);
            }
        }
        alternatives.add(sequenceToNode(sequence));

        if (alternatives.size() == 1) {
            return alternatives.get(0);
        } else {
            return new AlternationNode(alternatives);
        }
    }

    private Node sequenceToNode(List<Node> seq) {
        if (seq.isEmpty()) {
            return new LiteralNode("");
        }
        if (seq.size() == 1) {
            return seq.get(0);
        }
        return new SequenceNode(seq);
    }

    private Node parseAtom() throws Exception {
        if (pos >= s.length()) {
            throw new Exception("Unexpected end of input");
        }

        char c = s.charAt(pos);
        Node node;

        if (c == '(') {
            pos++; // skip '('
            node = parseExpression(new HashSet<>(Collections.singletonList(')')));
            if (pos >= s.length() || s.charAt(pos) != ')') {
                throw new Exception("Missing closing parenthesis");
            }
            pos++; // skip ')'
        } else {
            int start = pos;
            while (pos < s.length() && "() *+|^".indexOf(s.charAt(pos)) == -1) {
                pos++;
            }
            String literal = s.substring(start, pos);
            node = new LiteralNode(literal);
        }

        // Check quantifiers *, +, ^number
        if (pos < s.length()) {
            char q = s.charAt(pos);
            if (q == '*') {
                node = new RepeatNode(node, 0, 5);
                pos++;
            } else if (q == '+') {
                node = new RepeatNode(node, 1, 5);
                pos++;
            } else if (q == '^') {
                pos++;
                int numStart = pos;
                while (pos < s.length() && Character.isDigit(s.charAt(pos))) {
                    pos++;
                }
                if (numStart == pos) {
                    throw new Exception("Expected number after '^'");
                }
                int number = Integer.parseInt(s.substring(numStart, pos));
                node = new RepeatNode(node, number, number);
            }
        }
        return node;
    }

    public Node parse() throws Exception {
        pos = 0;
        Node tree = parseExpression(new HashSet<>());
        if (pos != s.length()) {
            throw new Exception("Unexpected characters at end of regex");
        }
        return tree;
    }

    // Dynamic sequence processing (step-by-step explanation)
    public static String dynamicSequenceProcessing(Node tree, String testStr) {
        MatchResult res = tree.match(testStr, 0);
        List<String> explanation = new ArrayList<>(res.explanation);
        if (!res.success) {
            explanation.add("Matching failed.");
        } else if (res.newPos < testStr.length()) {
            explanation.add("Extra characters remain after position " + res.newPos + ".");
        } else {
            explanation.add("String fully matched the regex!");
        }
        return explanation.stream().collect(Collectors.joining("\n"));
    }

    // Main for demonstration
    public static void main(String[] args) {
        String[] regexList = {
                "(S|T)(U|V)W*Y+24",
                "L(M|N)O^3P*Q(2|3)",
                "R*S(T|U|V)W(X|Y|Z)^2",
                "O(P|Q|R)+2(3|4)" // example from variant 3
        };

        for (int i = 0; i < regexList.length; i++) {
            String regex = regexList[i];
            System.out.println("===== Expression " + (i + 1) + ": " + regex + " =====");
            try {
                RegexGenerator parser = new RegexGenerator(regex);
                Node tree = parser.parse();

                List<String> validStrings = tree.generate();
                System.out.println("First 10 strings:");
                for (int j = 0; j < Math.min(10, validStrings.size()); j++) {
                    System.out.println(validStrings.get(j));
                }
                System.out.println("Total count: " + validStrings.size());
            } catch (Exception e) {
                System.out.println("Error generating strings for " + regex + ": " + e.getMessage());
            }
            System.out.println();
        }

        Scanner scanner = new Scanner(System.in);
        System.out.println("===== Bonus: Dynamic Sequence Processing =====");
        System.out.print("Enter a regex: ");
        String regex = scanner.nextLine();
        System.out.print("Enter test string for dynamic processing: ");
        String testStr = scanner.nextLine();

        try {
            RegexGenerator parser = new RegexGenerator(regex);
            Node tree = parser.parse();
            String result = dynamicSequenceProcessing(tree, testStr);
            System.out.println(result);
        } catch (Exception e) {
            System.out.println("Regex parsing error: " + e.getMessage());
        }

        scanner.close();
    }
}
