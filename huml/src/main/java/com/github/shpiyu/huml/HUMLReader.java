package com.github.shpiyu.huml;

import java.io.IOException;

import com.github.shpiyu.huml.parser.Parser;

/**
 * Reads HUML format from a string.
 */
public class HumlReader {
    private String input;

    /**
     * Creates a new HUMLReader instance.
     * 
     * @param input The input string to read from.
     */
    public HumlReader(String input) {
        this.input = input;
    }

    /**
     * Reads a HUML document from a string.
     * 
     * @return A map of key-value pairs representing the document.
     * @throws IOException If an I/O error occurs.
     */
    public HumlDocument readDocument() throws IOException {
        return Parser.parse(input);
    }
}
