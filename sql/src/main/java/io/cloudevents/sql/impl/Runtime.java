package io.cloudevents.sql.impl;

public class Runtime {

    private final TypeCastingProvider typeCastingProvider;

    public Runtime(TypeCastingProvider typeCastingProvider) {
        this.typeCastingProvider = typeCastingProvider;
    }

    public TypeCastingProvider getTypeCastingProvider() {
        return typeCastingProvider;
    }
}
