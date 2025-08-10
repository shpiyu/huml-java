package com.github.shpiyu.huml;

public class HumlDocument {
    private final HumlValue root;

    public HumlDocument(HumlValue root) {
        this.root = root;
    }

    public HumlValue getRoot() {
        return root;
    }

    public HumlValue get(String key) {
        if (root.getType() != HumlType.DICT) {
            throw new IllegalStateException("Root is not a dictionary");
        }
        return root.asDict().getOrDefault(key, HumlValue.nullValue());
    }

    public boolean isEmpty() {
        return root.isNull() || 
               (root.getType() == HumlType.DICT && root.asDict().isEmpty()) ||
               (root.getType() == HumlType.LIST && root.asList().isEmpty());
    }
}
