package com.github.shpiyu.huml;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

public class HUMLMapper {
    private final Map<Class<?>, HUMLAdapter<?>> adapters = new HashMap<>();

    public <T> void registerAdapter(Class<T> type, HUMLAdapter<T> adapter) {
        adapters.put(type, adapter);
    }

    @SuppressWarnings("unchecked")
    public <T> String writeValueAsString(T value) throws IOException {
        HUMLWriter writer = new HUMLWriter();
        HUMLAdapter<T> adapter = (HUMLAdapter<T>) adapters.get(value.getClass());
        if (adapter == null) {
            throw new IllegalArgumentException("No adapter found for type: " + value.getClass());
        }
        adapter.toHUML(writer, value);
        return writer.getOutput();
    }

    @SuppressWarnings("unchecked")
    public <T> T readValue(String input, Class<T> type) throws IOException {
        HUMLReader reader = new HUMLReader(input);
        HUMLAdapter<T> adapter = (HUMLAdapter<T>) adapters.get(type);
        if (adapter == null) {
            throw new IllegalArgumentException("No adapter found for type: " + type);
        }
        return adapter.fromHUML(reader);
    }
}

