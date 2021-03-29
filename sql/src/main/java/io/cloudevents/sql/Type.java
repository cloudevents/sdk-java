package io.cloudevents.sql;

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
}
