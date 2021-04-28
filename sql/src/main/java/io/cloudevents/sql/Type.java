package io.cloudevents.sql;

import java.util.Objects;

/**
 * Type represents any of the types supported by the CloudEvents Expression Language and their relative Java classes.
 */
public enum Type {
    /**
     * The <i>Integer</i> type
     */
    INTEGER(Integer.class),
    /**
     * The <i>String</i> type
     */
    STRING(String.class),
    /**
     * The <i>Boolean</i> type
     */
    BOOLEAN(Boolean.class),
    /**
     * Any is a catch-all type that can be used to represent any of the above types in a function signature.
     */
    ANY(Object.class);

    private final Class<?> clazz;

    Type(Class<?> clazz) {
        this.clazz = clazz;
    }

    /**
     * @return the Java class corresponding to the CloudEvents Expression Language type.
     */
    public Class<?> valueClass() {
        return clazz;
    }

    /**
     * Compute the CloudEvents Expression Language type from a value.
     *
     * @param value the value to use
     * @return the type, or any if the value class is unrecognized.
     */
    public static Type fromValue(Object value) {
        Objects.requireNonNull(value);
        return fromClass(value.getClass());
    }

    /**
     * Compute the CloudEvents Expression Language type from a Java class.
     *
     * @param clazz the class to use
     * @return the type, or any if the class is unrecognized.
     */
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
