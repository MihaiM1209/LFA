# Regular Expressions

### Course: Formal Languages & Finite Automata

### Author: Mustea Mihai

## Theory

Lexical analysis is the process of transforming raw text input into a series of tokens — atomic units that carry semantic meaning. This process is foundational in compilers, interpreters, and language processors, especially when dealing with domain-specific languages (DSLs).

In this project, we implement a lexer in Java using regular expressions and pattern matching, specifically for a DSL that defines network configuration. The lexer identifies various components such as keywords, identifiers, IP addresses, MAC addresses, strings, and numbers.


## Objectives

1. Understand how a lexer (tokenizer) processes structured text
2. Use Java’s built-in regex library (java.util.regex)
3. Correctly prioritize token patterns to avoid incorrect matches
4. Build a working lexical analyzer for a network configuration DSL


## Token Types Supported

Keywords: e.g., device, interface, vlan, etc.
Identifiers: Custom names like router1, eth0
IP addresses: e.g., 192.168.0.1
MAC addresses: e.g., 00ab.cd34.ef56
Numbers: e.g., 10
Strings: e.g., "Main uplink"


## Implementation Breakdown

### Step 1. Defining Token Patterns
```java
private static final String ID = "[a-zA-Z_][a-zA-Z0-9_-]*";
private static final String NUMBER = "\\d+";
private static final String STRING = "\"[^\"]*\"";
private static final String IPV4_ADDRESS = "(?:\\d{1,3}\\.){3}\\d{1,3}";
private static final String MAC_ADDRESS = "[0-9A-Fa-f]{4}\\.[0-9A-Fa-f]{4}\\.[0-9A-Fa-f]{4}";
```
These regex patterns define the structure of tokens we want to recognize:
1. ID: Names of things (e.g. router1)
2. NUMBER: Integers (e.g. 10)
3. STRING: Quoted text (e.g. "Main uplink")
4. IPV4_ADDRESS: IPv4 format IPs (e.g. 192.168.0.1)
5. MAC_ADDRESS: Cisco-style MACs (e.g. 00ab.cd34.ef56)

### Step 2. Keyword List
```java
private static final String[] keywords = {
    "network", "device", "module", "slot", "interface", "vlan", "route", "dhcp", "acl",
    "link", "coordinates", "power", "gateway", "dns", "bandwidth", "allow", "deny",
    "from", "to", "pool", "name", "desc", "cable", "length", "functional", "static"
};
```
These are reserved words in the DSL. They have special meaning and are not treated as regular IDs.

### Step 3. Adding Token Patterns Dynamically
```java
private static final List<Pattern> tokenPatterns = new ArrayList<>();
private static final Map<Pattern, String> tokenTypes = new HashMap<>();

static {
    addToken("IPV4_ADDRESS", IPV4_ADDRESS);
    addToken("MAC_ADDRESS", MAC_ADDRESS);
    addToken("STRING", STRING);
    addToken("NUMBER", NUMBER);

    for (String keyword : keywords) {
        addToken("KEYWORD_" + keyword.toUpperCase(), "\\b" + keyword + "\\b");
    }

    addToken("ID", ID);
}
```
1. This block sets up the list of all supported tokens.
2. The order matters: we add specific patterns first (IP, MAC) and generic ones last (like ID).
3. addToken(...) compiles the pattern and associates it with a token name.

### Step 4. Helper to Register Token Types
```java
private static void addToken(String type, String regex) {
    Pattern pattern = Pattern.compile("^" + regex);
    tokenPatterns.add(pattern);
    tokenTypes.put(pattern, type);
}
```
1. Registers a regex as a pattern and maps it to a string label (like KEYWORD_DEVICE, ID, etc.).
2. ^ ensures the match starts at the beginning of the current string.


### Step 5. Tokenization Logic
```java
public static List<String[]> tokenize(String text) {
    List<String[]> tokens = new ArrayList<>();
    String remainingText = text.trim();

    while (!remainingText.isEmpty()) {
        boolean matched = false;

        for (Pattern pattern : tokenPatterns) {
            Matcher matcher = pattern.matcher(remainingText);
            if (matcher.find()) {
                String tokenValue = matcher.group();
                tokens.add(new String[]{tokenTypes.get(pattern), tokenValue});
                remainingText = remainingText.substring(tokenValue.length()).trim();
                matched = true;
                break;
            }
        }

        if (!matched) {
            System.err.println("Unknown token at: " + remainingText);
            break;
        }
    }

    return tokens;
}
```
1. Iteratively tries each pattern on the remaining text.
2. If a match is found, it adds the (TYPE, VALUE) pair to the result.
3. If no pattern matches, it reports an error and exits.


### Step 6. Test Example
```java
public static void main(String[] args) {
    String sample = "device router1 interface eth0 ip 192.168.0.1 mac 00ab.cd34.ef56 vlan 10 desc \"Main uplink\"";

    List<String[]> result = tokenize(sample);
    for (String[] token : result) {
        System.out.println("(" + token[0] + ", " + token[1] + ")");
    }
}
```
1. Sample DSL input is passed into the lexer.
2. Output is printed line by line as (TOKEN_TYPE, VALUE).


###Output
```java
(KEYWORD_DEVICE, device)
(ID, router1)
(KEYWORD_INTERFACE, interface)
(ID, eth0)
(ID, ip)
(IPV4_ADDRESS, 192.168.0.1)
(ID, mac)
(MAC_ADDRESS, 00ab.cd34.ef56)
(KEYWORD_VLAN, vlan)
(NUMBER, 10)
(KEYWORD_DESC, desc)
(STRING, "Main uplink")
```


## Conclusions / Results

This Java lexer:

1. Parses structured DSL input correctly
2.Uses prioritized regex rules to avoid conflicts
3. Provides solid error handling for unknown tokens
4. Is a modular, extensible base for full parsers or interpreters


This lab demonstrated the power of regex-based tokenization, how token order impacts parsing, and how to design a robust lexer for a practical DSL.









