package com.github.shpiyu.huml.parser;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.regex.Pattern;

import com.github.shpiyu.huml.HumlDocument;
import com.github.shpiyu.huml.HumlValue;

public class Parser {
    private static final Pattern NUMBER_PATTERN = Pattern.compile("-?\\d+(\\.\\d+)?");
    private static final Pattern BOOLEAN_PATTERN = Pattern.compile("true|false", Pattern.CASE_INSENSITIVE);

    public static HumlDocument parse(String huml) {
        if (huml == null || huml.trim().isEmpty()) {
            throw new ParserException("Empty document is undefined");
        }

        String[] lines = huml.split("\\r?\\n");
        Map<String, HumlValue> rootMap = new LinkedHashMap<>(); 
        parseBlock(lines, 0, 0, rootMap);

        return new HumlDocument(HumlValue.ofDict(rootMap));
    }

    /**
     * Recursive block parser
     */
    private static int parseBlock(String[] lines, int startLine, int indentLevel, Map<String, HumlValue> map) {
        for (int i = startLine; i < lines.length; i++) {
            String line = lines[i];
            if (line.trim().isEmpty() || line.trim().startsWith("#")) {
                continue; // skip blank lines and comments
            }

            int leadingSpaces = countLeadingSpaces(line);
            if (leadingSpaces < indentLevel) {
                return i; // end of this block
            }
            if (leadingSpaces > indentLevel) {
                throw new ParserException("Invalid indentation at line " + (i + 1));
            }

            String trimmed = line.trim();

            // Handle vectors
            if (trimmed.contains("::")) {
                String[] parts = trimmed.split("::", 2);
                String key = parts[0].trim();
                String valuePart = parts.length > 1 ? parts[1].trim() : "";

                if (valuePart.contains(",") && valuePart.contains(":")) {
                    // inline dict
                    Map<String, HumlValue> dict = new LinkedHashMap<>();
                    for (String pair: valuePart.split(",")) {
                        String[] kv = pair.split(":", 2);
                        if (kv.length != 2) {
                            throw new ParserException("Invalid key-value pair at line " + (i + 1));
                        }
                        String k = kv[0].trim();
                        String v = kv[1].trim();
                        dict.put(k, parseScalar(v));
                    }
                    map.put(key, HumlValue.ofDict(dict));
                } else if (valuePart.contains(",")) {
                    // inline list
                    List<HumlValue> list = new ArrayList<>();
                    for (String item : valuePart.split(",")) {
                        list.add(parseScalar(item.trim()));
                    }
                    map.put(key, HumlValue.ofList(list));
                } else if (!valuePart.isEmpty()) {
                    if (valuePart.equals("[]")) {
                        // empty list
                        map.put(key, HumlValue.ofList(List.of()));
                    } else if (valuePart.equals("{}")) {
                        // empty dict
                        map.put(key, HumlValue.ofDict(Map.of()));
                    } else {
                        // single value list
                        map.put(key, HumlValue.ofList(List.of(parseScalar(valuePart))));
                    }
                } else {
                    // multi-line vector
                    int j = i + 1;
                    while (j < lines.length && (lines[j].trim().isEmpty() || lines[j].trim().startsWith("#"))) {
                        j++;
                    }
                    if (j < lines.length && lines[j].trim().startsWith("-")) {
                        // multi-line list
                        List<HumlValue> list = new ArrayList<>();
                        i = parseList(lines, i + 1, indentLevel + 2, list) - 1;
                        map.put(key, HumlValue.ofList(list));
                    } else {
                        // multi-line dict
                        Map<String, HumlValue> dict = new LinkedHashMap<>();
                        i = parseBlock(lines, i + 1, indentLevel + 2, dict) - 1;
                        map.put(key, HumlValue.ofDict(dict));
                    }
                }
            }
            // Handle dict entry
            else if (trimmed.contains(":")) {
                String[] parts = trimmed.split(":", 2);
                String key = parts[0].trim();
                String valuePart = parts.length > 1 ? parts[1].trim() : "";

                if (!valuePart.isEmpty()) {
                    map.put(key, parseScalar(valuePart));
                } else {
                    // nested dict
                    Map<String, HumlValue> child = new LinkedHashMap<>();
                    i = parseBlock(lines, i + 1, indentLevel + 2, child) - 1;
                    map.put(key, HumlValue.ofDict(child));
                }
            } else {
                throw new ParserException("Invalid line at " + (i + 1) + ": " + line);
            }
        }
        return lines.length;
    }

    /**
     * Parses a list block
     */
    private static int parseList(String[] lines, int startLine, int indentLevel, List<HumlValue> list) {
        for (int i = startLine; i < lines.length; i++) {
            String line = lines[i];
            if (line.trim().isEmpty() || line.trim().startsWith("#")) {
                continue;
            }

            if (!line.trim().startsWith("- ")) {
                throw new ParserException("Invalid list item at line " + (i + 1));
            }

            int leadingSpaces = countLeadingSpaces(line);
            if (leadingSpaces < indentLevel) {
                return i; // end of list
            }
            if (leadingSpaces > indentLevel) {
                throw new ParserException("Invalid list indentation at line " + (i + 1));
            }

            // remove the "- " prefix from list item
            line = line.trim().substring(2);

            // nested vector
            if (line.equals("::")) {
                int j = i + 1;
                while (j < lines.length && (lines[j].trim().isEmpty() || lines[j].trim().startsWith("#"))) {
                    j++;
                }
                if (j < lines.length && lines[j].trim().startsWith("-")) {
                    // multi-line list
                    List<HumlValue> nestedList = new ArrayList<>();
                    i = parseList(lines, i + 1, indentLevel + 2, nestedList) - 1;
                    list.add(HumlValue.ofList(nestedList));
                    continue;
                } else {
                    // multi-line dict
                    Map<String, HumlValue> nestedDict = new LinkedHashMap<>();
                    i = parseBlock(lines, i + 1, indentLevel + 2, nestedDict) - 1;
                    list.add(HumlValue.ofDict(nestedDict));
                    continue;
                }
            }

            list.add(parseScalar(line.trim()));
        }
        return lines.length;
    }

    /**
     * Parses a scalar value from a string
     */
    private static HumlValue parseScalar(String raw) {
        if (raw.startsWith("\"") && raw.endsWith("\"")) {
            return HumlValue.ofString(raw.substring(1, raw.length() - 1));
        }
        if (raw.equalsIgnoreCase("null")) {
            return HumlValue.nullValue();
        }
        if (BOOLEAN_PATTERN.matcher(raw).matches()) {
            return HumlValue.ofBoolean(Boolean.parseBoolean(raw));
        }
        if (NUMBER_PATTERN.matcher(raw).matches()) {
            if (raw.contains(".")) {
                return HumlValue.ofNumber(Double.parseDouble(raw));
            } else {
                return HumlValue.ofNumber(Long.parseLong(raw));
            }
        }
        return HumlValue.ofString(raw); // fallback
    }

    private static int countLeadingSpaces(String s) {
        int count = 0;
        while (count < s.length() && s.charAt(count) == ' ') {
            count++;
        }
        return count;
    }
}
