# Regular Expressions

### Course: Formal Languages & Finite Automata

### Author: Mihai Mustea

## Theory

Regular expressions (regex) are powerful tools for string pattern matching. They are used in various domains like compilers, text editors, search engines, and input validation. A regex defines a language and can be parsed as a finite automaton, making it a fundamental concept in formal language theory.

Regex patterns often include:

- Literals (e.g., a, b, 1)  
- Concatenation (e.g., ab)  
- Alternation (e.g., a|b)
- Quantifiers (e.g., *, +, ?)
- Groups (e.g., (ab|cd)*)
- Character sets/ranges (e.g., [a-z], [abc])

This project aims to both generate strings that match a given regex and simulate regex matching, step by step.

## Objectives

-Implement a custom regex parser in Python.

-Generate random valid strings from regex patterns.

-Simulate the matching process, visualizing how a string is evaluated against a pattern.

-Practice working with abstract syntax trees (ASTs) and recursive descent parsing.

## Implementation

### 1. Generating Strings for Expression 1: (S|T)(U|V)W*Y+24
```java
// Generate combinations like SUWYY24, TVWWYYY24, etc.
for (char firstLetter : new char[] {'S', 'T'}) {
    for (char secondLetter : new char[] {'U', 'V'}) {
        for (int wCount = 0; wCount <= 5; wCount++) {
            for (int yCount = 1; yCount <= 5; yCount++) {
                String wPart = "W".repeat(wCount);
                String yPart = "Y".repeat(yCount);
                String combo = firstLetter + "" + secondLetter + wPart + yPart + "24";
                firstPart.add(combo);
            }
        }
    }
}
```
This snippet generates all valid strings matching the regular expression (S|T)(U|V)W*Y+24. It uses nested loops to create combinations of the allowed characters and repetitions of 'W' and 'Y'.

### 2. Step-by-Step String Processing
```java
if (str.charAt(0) == 'S' || str.charAt(0) == 'T') {
    explanation.append("Step 1: Matched ").append(str.charAt(0)).append(" as (S|T)\n");
} else {
    return "Does not match step 1";
}
```
This logic begins the manual step-by-step matching for Expression 1. It checks whether the first character is either 'S' or 'T', returning an error if not. Each step continues similarly, building up a full match explanation.


### 3. Regex Check for Expression 2: L(M|N)O000*P*Q(2|3)
```java
if (str.matches("L(M|N)O000*P*Q(2|3)")) {
    System.out.println("Matches expression 2!");
}
```
This checks if a string fits the pattern where:

1. It starts with L, followed by M or N
2. Then O000* (starts with 'O' and allows optional '0' characters)
3. Then zero or more Ps
4. Then Q and ends in 2 or 3


### 4. Generating Final Pattern 3: R*S(T|U|V)W(X|Y|Z){2}
```java
for (int rCount = 0; rCount <= 5; rCount++) {
    String rPart = "R".repeat(rCount);
    for (char mid : new char[] {'T', 'U', 'V'}) {
        for (char x : new char[] {'X', 'Y', 'Z'}) {
            for (char y : new char[] {'X', 'Y', 'Z'}) {
                String combo = rPart + "S" + mid + "W" + x + y;
                thirdPart.add(combo);
            }
        }
    }
}
```
This loop constructs strings for Expression 3, ensuring:

1. 0 to 5 repetitions of R
2. Then S, then one of T, U, or V
3. Then W
4. Then two characters chosen from X, Y, or Z

## Conclusion

This Java project showcases how regular expressions can be understood, implemented, and visualized through programmatic generation and analysis of valid string sequences. By splitting each pattern into logical parts and simulating both the generation and validation processes, it bridges the gap between theoretical regex and practical application.

The three supported regex patterns—each with their unique structure—demonstrate:

🔤 The flexibility of character choices using (A|B) syntax

🔁 Repetition mechanics like *, +, and exact counts

🔍 How to manually trace a match using logic and string parsing


This project not only tests regex matches using .matches() but also provides a step-by-step breakdown for Expression 1, making it an excellent educational tool for learning how regex patterns operate under the hood.

Whether you're learning regular expressions or need a foundation for more complex string parsing, this codebase provides a clear, modular starting point.











