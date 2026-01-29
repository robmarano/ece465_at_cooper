package edu.cooper.ece465.session05.reflection;

import java.lang.reflect.Field;
import java.util.Collection;
import java.util.Map;

/**
 * A simplified JSON Serializer to demonstrate Reflection.
 * It does not handle every edge case (circular refs, arrays, etc.)
 * but shows the "Magic" behind Gson.
 */
public class MiniJsonSerializer {

    public String toJson(Object obj) throws IllegalAccessException {
        if (obj == null) {
            return "null";
        }

        Class<?> clazz = obj.getClass();

        // 1. Handle Primitives and Strings
        if (isPrimitiveOrString(clazz)) {
            return formatPrimitive(obj);
        }

        // 2. Handle Collections (Brief support)
        if (Collection.class.isAssignableFrom(clazz)) {
            // Skipping for MVP to keep it simple, or implement if needed
            // return handleCollection((Collection<?>) obj);
        }

        // 3. Handle Objects (Recursive)
        StringBuilder json = new StringBuilder();
        json.append("{\n");

        Field[] fields = clazz.getDeclaredFields();
        for (int i = 0; i < fields.length; i++) {
            Field field = fields[i];
            field.setAccessible(true); // The Magic: Access private fields

            String name = field.getName();
            Object value = field.get(obj);

            json.append("  \"").append(name).append("\": ");
            json.append(toJson(value)); // Recursion

            if (i < fields.length - 1) {
                json.append(",\n");
            } else {
                json.append("\n");
            }
        }
        json.append("}");
        return json.toString();
    }

    private boolean isPrimitiveOrString(Class<?> clazz) {
        return clazz.isPrimitive()
                || Number.class.isAssignableFrom(clazz)
                || Boolean.class.isAssignableFrom(clazz)
                || String.class.isAssignableFrom(clazz);
    }

    private String formatPrimitive(Object obj) {
        if (obj instanceof String) {
            return "\"" + obj + "\"";
        }
        return obj.toString();
    }
}
