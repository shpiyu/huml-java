package com.github.shpiyu.huml;

import java.io.IOException;

/**
 * Adapter class for serializing and deserializing objects to and from HUML format.
 */
public abstract class HUMLAdapter<T> {
    public abstract T fromHUML(HUMLReader reader) throws IOException;
    public abstract void toHUML(HUMLWriter writer, T value) throws IOException;
}
