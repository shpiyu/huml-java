package com.github.shpiyu.huml;

import java.io.IOException;

public abstract class HUMLAdapter<T> {
    public abstract T fromHUML(HUMLReader reader) throws IOException;
    public abstract void toHUML(HUMLWriter writer, T value) throws IOException;
}
