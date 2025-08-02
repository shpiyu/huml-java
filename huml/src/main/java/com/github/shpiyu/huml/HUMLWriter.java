package com.github.shpiyu.huml;

/**
 * Writes HUML format to a string.
 */
public class HUMLWriter {
    private final StringBuilder sb = new StringBuilder();

    public void writeField(String key, String value) {
        sb.append(key).append(": ").append(value).append("\n");
    }

    public String getOutput() {
        return sb.toString();
    }
}
