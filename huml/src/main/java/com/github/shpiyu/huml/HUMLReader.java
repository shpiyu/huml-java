package com.github.shpiyu.huml;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.StringReader;
import java.util.LinkedHashMap;
import java.util.Map;

/**
 * Reads HUML format from a string.
 */
public class HumlReader {
    private BufferedReader reader;

    private final String SCALAR_DELIM = ":";
    private final String VECTOR_DELIM = "::";

    /**
     * Creates a new HUMLReader instance.
     * 
     * @param input The input string to read from.
     */
    public HumlReader(String input) {
        this.reader = new BufferedReader(new StringReader(input));
    }

    /**
     * Reads a HUML document from a string.
     * 
     * @return A map of key-value pairs representing the document.
     * @throws IOException If an I/O error occurs.
     */
    public Map<String, Object> readDocument() throws IOException {
        Map<String, Object> result = new LinkedHashMap<>();
        String line;
        while ((line = reader.readLine()) != null) {
            line = line.trim();
            if (line.isEmpty()) continue;

            if (line.startsWith("#")) continue;
        
            if (line.contains(SCALAR_DELIM)) {
                String[] parts = line.split(SCALAR_DELIM, 2);
                String key = parts[0].trim();
                String value = ignoreComments(parts[1]).trim();
                result.put(key, value);
            }
        }
        return result;
    }

    private String ignoreComments(String line) {
        int index = line.indexOf("# ");
        if (index == -1) return line;
        return line.substring(0, index);
    }
}
