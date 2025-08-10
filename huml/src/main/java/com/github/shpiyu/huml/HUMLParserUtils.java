package com.github.shpiyu.huml;

/**
 * Utility functions for parsing HUML format.
 */
public class HumlParserUtils {
    public static String handleNullString(String value) {
        if (value == null) {
            return "null";
        }
        return "\"" + value + "\"";
    }

    public static Integer parseInt(Object value) {
        if (value == null || value.equals("null")) {
            return null;
        }
        return Integer.parseInt((String) value);
    }

    public static int parsePrimitiveInt(Object value) {
        if (value == null) {
            return 0;
        }
        return Integer.parseInt((String) value);
    }

    public static Double parseDouble(Object value) {
        if (value == null || value.equals("null")) {
            return null;
        }
        return Double.parseDouble((String) value);
    }

    public static double parsePrimitiveDouble(Object value) {
        if (value == null) {
            return 0.0;
        }
        return Double.parseDouble((String) value);
    }

    public static Float parseFloat(Object value) {
        if (value == null || value.equals("null")) {
            return null;
        }
        return Float.parseFloat((String) value);
    }

    public static float parsePrimitiveFloat(Object value) {
        if (value == null) {
            return 0.0f;
        }
        return Float.parseFloat((String) value);
    }

    public static Long parseLong(Object value) {
        if (value == null || value.equals("null")) {
            return null;
        }
        return Long.parseLong((String) value);
    }

    public static long parsePrimitiveLong(Object value) {
        if (value == null) {
            return 0L;
        }
        return Long.parseLong((String) value);
    }

    public static Short parseShort(Object value) {
        if (value == null || value.equals("null")) {
            return null;
        }
        return Short.parseShort((String) value);
    }

    public static short parsePrimitiveShort(Object value) {
        if (value == null) {
            return 0;
        }
        return Short.parseShort((String) value);
    }

    public static Byte parseByte(Object value) {
        if (value == null || value.equals("null")) {
            return null;
        }
        return Byte.parseByte((String) value);
    }

    public static byte parsePrimitiveByte(Object value) {
        if (value == null) {
            return 0;
        }
        return Byte.parseByte((String) value);
    }

    public static String parseString(Object value) {
        if (value == null || value.equals("null")) {
            return null;
        }
        String s = (String) value;
        if (s.isEmpty()) {
            return "";
        }
        return s.substring(1, s.length() - 1);
    }   
}
