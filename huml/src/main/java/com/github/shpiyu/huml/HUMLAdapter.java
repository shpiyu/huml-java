package com.github.shpiyu.huml;

import java.io.IOException;

/**
 * Adapter class for serializing and deserializing objects to and from HUML format.
 */
public abstract class HumlAdapter<T> {
    public abstract T fromHUML(HumlReader reader) throws IOException;
    public abstract void toHUML(HumlWriter writer, T value) throws IOException;
}
