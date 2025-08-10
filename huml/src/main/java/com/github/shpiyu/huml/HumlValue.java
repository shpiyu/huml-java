package com.github.shpiyu.huml;

import java.util.Collections;
import java.util.List;
import java.util.Map;

public class HumlValue {
    private final HumlType type;
    private final Object value;

    private HumlValue(HumlType type, Object value) {
        this.type = type;
        this.value = value;
    }

    public static HumlValue ofString(String val) {
        return new HumlValue(HumlType.STRING, val);
    }

    public static HumlValue ofNumber(Number val) {
        return new HumlValue(HumlType.NUMBER, val);
    }

    public static HumlValue ofBoolean(boolean val) {
        return new HumlValue(HumlType.BOOLEAN, val);
    }

    public static HumlValue ofList(List<HumlValue> val) {
        return new HumlValue(HumlType.LIST, Collections.unmodifiableList(val));
    }

    public static HumlValue ofDict(Map<String, HumlValue> val) {
        return new HumlValue(HumlType.DICT, Collections.unmodifiableMap(val));
    }

    public static HumlValue nullValue() {
        return new HumlValue(HumlType.NULL, null);
    }

    public HumlType getType() {
        return type;
    }

    public boolean isNull() {
        return type == HumlType.NULL;
    }

    public String asString() {
        return type == HumlType.STRING ? String.valueOf(value) : null;
    }

    public Integer asInteger() {
        return type == HumlType.NUMBER ? Integer.parseInt(value.toString()) : null;
    }

    public Long asLong() {
        return type == HumlType.NUMBER ? Long.parseLong(value.toString()) : null;
    }

    public Float asFloat() {
        return type == HumlType.NUMBER ? Float.parseFloat(value.toString()) : null;
    }

    public Double asDouble() {
        return type == HumlType.NUMBER ? Double.parseDouble(value.toString()) : null;
    }

    public Short asShort() {
        return type == HumlType.NUMBER ? Short.parseShort(value.toString()) : null;
    }

    public Byte asByte() {
        return type == HumlType.NUMBER ? Byte.parseByte(value.toString()) : null;
    }

    public Boolean asBoolean() {
        return type == HumlType.BOOLEAN ? (Boolean) value : null;
    }

    public List<HumlValue> asList() {
        return type == HumlType.LIST ? (List<HumlValue>) value : Collections.emptyList();
    }

    public Map<String, HumlValue> asDict() {
        return type == HumlType.DICT ? (Map<String, HumlValue>) value : Collections.emptyMap();
    }

    @Override
    public String toString() {
        return String.valueOf(value);
    }    
}
