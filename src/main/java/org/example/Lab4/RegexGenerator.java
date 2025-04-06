package org.example.Lab4;

import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

public class RegexGenerator {

    public static List<String> generateValidStrings() {
        List<String> firstPart = new ArrayList<>();
        List<String> secondPart = new ArrayList<>();
        List<String> thirdPart = new ArrayList<>();

        // 1. (S|T)(U|V)W*Y+24
        for (char firstLetter : new char[] {'S', 'T'}) {
            for (char secondLetter : new char[] {'U', 'V'}) {
                for (int wCount = 0; wCount <= 5; wCount++) {
                    for (int yCount = 1; yCount <= 5; yCount++) {
                        String wPart = new String(new char[wCount]).replace('\0', 'W');
                        String yPart = new String(new char[yCount]).replace('\0', 'Y');
                        String combo = firstLetter + "" + secondLetter + wPart + yPart + "24";
                        firstPart.add(combo);
                    }
                }
            }
        }

        // 2) L(M|N)O^3P*Q(2|3)
        for (char letter : new char[] {'M', 'N'}) {
            for (int pCount = 0; pCount <= 2; pCount++) {
                String pPart = new String(new char[pCount]).replace('\0', 'P');
                for (char digit : new char[] {'2', '3'}) {
                    String combo = "L" + letter + "000" + pPart + "Q" + digit;
                    secondPart.add(combo);
                }
            }
        }

        // 3) R*S(T|U|V)W(X|Y|Z)^2
        List<String> xyzPairs = new ArrayList<>();
        for (char c1 : new char[] {'X', 'Y', 'Z'}) {
            for (char c2 : new char[] {'X', 'Y', 'Z'}) {
                xyzPairs.add(c1 + "" + c2);
            }
        }

        for (int rCount = 0; rCount <= 5; rCount++) {
            String rPart = new String(new char[rCount]).replace('\0', 'R');
            for (char middleLetter : new char[] {'T', 'U', 'V'}) {
                for (String pair : xyzPairs) {
                    String combo = rPart + "S" + middleLetter + "W" + pair;
                    thirdPart.add(combo);
                }
            }
        }

        List<String> result = new ArrayList<>();
        result.addAll(firstPart);
        result.addAll(secondPart);
        result.addAll(thirdPart);
        return result;
    }

    public static String sequenceProcessing(String str) {
        StringBuilder explanation = new StringBuilder();
        int idx = 0;

        // Step 1: (S|T)
        if (str.length() < 1 || (str.charAt(0) != 'S' && str.charAt(0) != 'T')) {
            return "Does not match step 1";
        }
        explanation.append("Step 1: Matched '").append(str.charAt(0)).append("' as (S|T)\n");
        idx++;

        // Step 2: (U|V)
        if (str.length() < 2 || (str.charAt(1) != 'U' && str.charAt(1) != 'V')) {
            return "Does not match step 2";
        }
        explanation.append("Step 2: Matched '").append(str.charAt(1)).append("' as (U|V)\n");
        idx++;

        // Step 3: W*
        int wCount = 0;
        while (idx < str.length() && str.charAt(idx) == 'W') {
            wCount++;
            idx++;
        }
        explanation.append("Step 3: Matched 'W' repeated ").append(wCount).append(" times\n");

        // Step 4: Y+
        int yCount = 0;
        while (idx < str.length() && str.charAt(idx) == 'Y') {
            yCount++;
            idx++;
        }
        if (yCount < 1) {
            return "Does not match step 4";
        }
        explanation.append("Step 4: Matched 'Y' repeated ").append(yCount).append(" times\n");

        // Step 5: 24
        if (idx + 2 <= str.length() && str.substring(idx, idx + 2).equals("24")) {
            explanation.append("Step 5: Matched '24'\n");
            idx += 2;
        } else {
            return "Does not match step 5";
        }

        // Final check
        if (idx == str.length()) {
            explanation.append("String fully matched expression 1!");
        } else {
            explanation.append("String has extra chars beyond the pattern.");
        }

        return explanation.toString();
    }

    public static void main(String[] args) {
        List<String> validStrings = generateValidStrings();

        // Expression 1 matches
        System.out.println("===== Expression 1 matches =====");
        for (int i = 0; i < 10 && i < validStrings.size(); i++) {
            System.out.println(validStrings.get(i));
        }
        System.out.println("Total count: " + validStrings.size());

        // Expression 2 matches
        System.out.println("\n===== Expression 2 matches =====");
        int totalCount = 0;
        for (int i = 0; i < validStrings.size(); i++) {
            if (validStrings.get(i).matches("L(M|N)O000*P*Q(2|3)")) {
                System.out.println(validStrings.get(i));
                totalCount++;
            }
        }
        System.out.println("Total count: " + totalCount);

        // Expression 3 matches
        System.out.println("\n===== Expression 3 matches =====");
        totalCount = 0;
        for (int i = 0; i < validStrings.size(); i++) {
            if (validStrings.get(i).matches("R*S(T|U|V)W(X|Y|Z)")) {
                System.out.println(validStrings.get(i));
                totalCount++;
            }
        }
        System.out.println("Total count: " + totalCount);

        // Bonus point test string
        System.out.println("\n===== Bonus point test string =====");
        Scanner scanner = new Scanner(System.in);
        System.out.print("Enter test string for expression 1 (ex. SUWWYY24): ");
        String testString = scanner.nextLine();
        System.out.println(sequenceProcessing(testString));
    }
}
