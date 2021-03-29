package io.cloudevents.sql.impl;

import io.cloudevents.sql.Runtime;
import io.cloudevents.sql.TypeCastingProvider;

public class RuntimeImpl implements Runtime {

    private static class SingletonContainer {
        private final static RuntimeImpl INSTANCE = new RuntimeImpl(new TypeCastingProviderImpl());
    }

    /**
     * @return instance of {@link RuntimeImpl}
     */
    public static Runtime getInstance() {
        return RuntimeImpl.SingletonContainer.INSTANCE;
    }

    private final TypeCastingProvider typeCastingProvider;

    public RuntimeImpl(TypeCastingProvider typeCastingProvider) {
        this.typeCastingProvider = typeCastingProvider;
    }

    @Override
    public TypeCastingProvider getTypeCastingProvider() {
        return typeCastingProvider;
    }
}
