package com.github.shpiyu.huml;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

/**
 * Mapper class for serializing and deserializing objects to and from HUML format. Allows 
 * registration of custom adapters for specific types. 
 */
public class HumlMapper {

    private final Map<Class<?>, HumlAdapter<?>> adapters = new HashMap<>();

    /**
     * Registers an adapter for a specific type.
     * 
     * @param type The type to register the adapter for.
     * @param adapter The adapter to register.
     */
    public <T> void registerAdapter(Class<T> type, HumlAdapter<T> adapter) {
        adapters.put(type, adapter);
    }

    @SuppressWarnings("unchecked")
    public <T> String writeValueAsString(T value) throws IOException {
        HumlWriter writer = new HumlWriter();
        HumlAdapter<T> adapter = (HumlAdapter<T>) adapters.get(value.getClass());
        if (adapter == null) {
            throw new IllegalArgumentException("No adapter found for type: " + value.getClass());
        }
        adapter.toHUML(writer, value);
        return writer.getOutput();
    }

    @SuppressWarnings("unchecked")
    public <T> T readValue(String input, Class<T> type) throws IOException {
        HumlReader reader = new HumlReader(input);
        HumlAdapter<T> adapter = (HumlAdapter<T>) adapters.get(type);
        if (adapter == null) {
            throw new IllegalArgumentException("No adapter found for type: " + type);
        }
        return adapter.fromHUML(reader);
    }
}

