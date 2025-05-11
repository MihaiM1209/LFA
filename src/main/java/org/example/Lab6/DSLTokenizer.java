package org.example.Lab6;

import java.util.*;
import java.util.regex.*;

public class DSLTokenizer {
    private static final List<Pattern> tokenPatterns = new ArrayList<>();
    private static final Map<Pattern, TokenType> tokenTypes = new HashMap<>();

    static {
        addToken(TokenType.IPV4_ADDRESS, "(?:\\d{1,3}\\.){3}\\d{1,3}");
        addToken(TokenType.MAC_ADDRESS, "[0-9A-Fa-f]{4}\\.[0-9A-Fa-f]{4}\\.[0-9A-Fa-f]{4}");
        addToken(TokenType.STRING, "\"[^\"]*\"");
        addToken(TokenType.NUMBER, "\\d+");
        addToken(TokenType.ID, "[a-zA-Z_][a-zA-Z0-9_-]*");
    }

    private static void addToken(TokenType type, String regex) {
        Pattern pattern = Pattern.compile("^" + regex);
        tokenPatterns.add(pattern);
        tokenTypes.put(pattern, type);
    }

    public static List<Token> tokenize(String input) {
        List<Token> tokens = new ArrayList<>();
        String remaining = input.trim();

        while (!remaining.isEmpty()) {
            boolean matched = false;

            for (Pattern pattern : tokenPatterns) {
                Matcher matcher = pattern.matcher(remaining);
                if (matcher.find()) {
                    String value = matcher.group();
                    tokens.add(new Token(tokenTypes.get(pattern), value));
                    remaining = remaining.substring(value.length()).trim();
                    matched = true;
                    break;
                }
            }

            if (!matched) {
                tokens.add(new Token(TokenType.UNKNOWN, remaining.substring(0, 1)));
                remaining = remaining.substring(1).trim();
            }
        }

        return tokens;
    }
}

