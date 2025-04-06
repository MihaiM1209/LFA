package org.example.Lab3;

import java.util.*;
import java.util.regex.*;

public class DSLTokenizer {

    private static final String ID = "[a-zA-Z_][a-zA-Z0-9_-]*";
    private static final String NUMBER = "\\d+";
    private static final String STRING = "\"[^\"]*\"";
    private static final String IPV4_ADDRESS = "(?:\\d{1,3}\\.){3}\\d{1,3}";
    private static final String MAC_ADDRESS = "[0-9A-Fa-f]{4}\\.[0-9A-Fa-f]{4}\\.[0-9A-Fa-f]{4}";


    private static final String[] keywords = {
            "network", "device", "module", "slot", "interface", "vlan", "route", "dhcp", "acl", "link",
            "coordinates", "power", "gateway", "dns", "bandwidth", "allow", "deny", "from", "to", "pool",
            "name", "desc", "cable", "length", "functional", "static"
    };

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

    private static void addToken(String type, String regex) {
        Pattern pattern = Pattern.compile("^" + regex);
        tokenPatterns.add(pattern);
        tokenTypes.put(pattern, type);
    }

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

    public static void main(String[] args) {
        String sample = "device router1 interface eth0 ip 192.168.0.1 mac 00ab.cd34.ef56 vlan 10 desc \"Main uplink\"";

        List<String[]> result = tokenize(sample);
        for (String[] token : result) {
            System.out.println("(" + token[0] + ", " + token[1] + ")");
        }
    }
}
