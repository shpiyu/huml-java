package com.github.shpiyu.huml;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.StringReader;
import java.util.LinkedHashMap;
import java.util.Map;

public class HUMLReader {
    private BufferedReader reader;

    public HUMLReader(String input) {
        this.reader = new BufferedReader(new StringReader(input));
    }

    public Map<String, Object> readDocument() throws IOException {
        Map<String, Object> result = new LinkedHashMap<>();
        String line;
        while ((line = reader.readLine()) != null) {
            if (line.trim().isEmpty()) continue;
            if (line.contains(":")) {
                String[] parts = line.split(":", 2);
                result.put(parts[0].trim(), parts[1].trim());
            }
        }
        return result;
    }
}
