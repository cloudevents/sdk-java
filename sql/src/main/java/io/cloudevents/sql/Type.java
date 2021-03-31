package io.cloudevents.sql;

import java.util.Objects;

public enum Type {
    INTEGER(Integer.class),
    STRING(String.class),
    BOOLEAN(Boolean.class),
    ANY(Object.class);

    private final Class<?> clazz;

    Type(Class<?> clazz) {
        this.clazz = clazz;
    }

    public Class<?> valueClass() {
        return clazz;
    }

    public static Type fromValue(Object value) {
        Objects.requireNonNull(value);
        if (Integer.class.equals(value.getClass())) {
            return INTEGER;
        } else if (String.class.equals(value.getClass())) {
            return STRING;
        } else if (Boolean.class.equals(value.getClass())) {
            return BOOLEAN;
        }
        return ANY;
    }

    public static Type fromClass(Class<?> clazz) {
        Objects.requireNonNull(clazz);
        if (Integer.class.equals(clazz)) {
            return INTEGER;
        } else if (String.class.equals(clazz)) {
            return STRING;
        } else if (Boolean.class.equals(clazz)) {
            return BOOLEAN;
        }
        return ANY;
    }

}
