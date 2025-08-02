package com.github.shpiyu.huml;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.StringReader;
import java.util.LinkedHashMap;
import java.util.Map;

/**
 * Reads HUML format from a string.
 */
public class HUMLReader {
    private BufferedReader reader;

    private final String SCALAR_DELIM = ":";
    private final String VECTOR_DELIM = "::";

    /**
     * Creates a new HUMLReader instance.
     * 
     * @param input The input string to read from.
     */
    public HUMLReader(String input) {
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
            
            if (line.contains(VECTOR_DELIM)) {
                String[] parts = line.split(VECTOR_DELIM, 2);
                String key = parts[0].trim();
                String[] values = parts[1].trim().split(",");
                for (int i = 0; i < values.length; i++) {
                    values[i] = values[i].trim();
                }
                result.put(key, values);
            } else if (line.contains(SCALAR_DELIM)) {
                String[] parts = line.split(SCALAR_DELIM, 2);
                String key = parts[0].trim();
                String value = parts[1].trim();
                result.put(key, value);
            }
        }
        return result;
    }
}
