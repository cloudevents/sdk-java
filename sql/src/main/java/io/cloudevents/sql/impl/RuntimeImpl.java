package io.cloudevents.sql.impl;

import io.cloudevents.sql.Runtime;
import io.cloudevents.sql.TypeCastingProvider;

public class RuntimeImpl implements Runtime {

    private final TypeCastingProvider typeCastingProvider;

    public RuntimeImpl(TypeCastingProvider typeCastingProvider) {
        this.typeCastingProvider = typeCastingProvider;
    }

    @Override
    public TypeCastingProvider getTypeCastingProvider() {
        return typeCastingProvider;
    }
}
