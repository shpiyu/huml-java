package com.github.shpiyu.huml;

public class HUMLWriter {
    private StringBuilder sb = new StringBuilder();

    public void writeField(String key, String value) {
        sb.append(key).append(": ").append(value).append("\n");
    }

    public String getOutput() {
        return sb.toString();
    }
}
