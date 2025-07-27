package com.github.shpiyu.huml;

import java.util.Collection;
import java.util.Iterator;

public class HUMLWriter {
    private final StringBuilder sb = new StringBuilder();

    public void writeField(String key, String value) {
        sb.append(key).append(": ").append(value).append("\n");
    }

    /**
     * Writes a list field in HUML format: fieldName:: value1, value2, "string value"
     * @param key The field name
     * @param values The collection of values to write
     */
    public void writeList(String key, Collection<?> values) {
        if (values == null || values.isEmpty()) {
            sb.append(key).append(":: []\n");
            return;
        }

        sb.append(key).append(":: ");
        Iterator<?> it = values.iterator();
        while (it.hasNext()) {
            Object value = it.next();
            if (value instanceof String) {
                // Escape and quote string values
                sb.append('"').append(escapeString((String) value)).append('"');
            } else if (value instanceof Character) {
                // Handle character values
                sb.append('"').append(escapeChar((char) value)).append('"');
            } else {
                // Handle numbers, booleans, etc.
                sb.append(value);
            }
            
            if (it.hasNext()) {
                sb.append(", ");
            }
        }
        sb.append("\n");
    }

    private String escapeString(String value) {
        // Escape double quotes and backslashes
        return value.replace("\\", "\\\\").replace("\"", "\\\"");
    }

    private String escapeChar(char c) {
        // Simple character escaping for now
        if (c == '\\' || c == '"') {
            return "\\" + c;
        }
        return String.valueOf(c);
    }

    public String getOutput() {
        return sb.toString();
    }
}
