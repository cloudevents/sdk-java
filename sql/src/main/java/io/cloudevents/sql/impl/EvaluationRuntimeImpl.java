package io.cloudevents.sql.impl;

import io.cloudevents.sql.EvaluationContext;
import io.cloudevents.sql.EvaluationRuntime;
import io.cloudevents.sql.Function;
import io.cloudevents.sql.Type;

public class EvaluationRuntimeImpl implements EvaluationRuntime {

    private static class SingletonContainer {
        private final static EvaluationRuntimeImpl INSTANCE = new EvaluationRuntimeImpl(new TypeCastingProvider(), FunctionTable.getDefaultInstance());
    }

    /**
     * @return instance of {@link EvaluationRuntimeImpl}
     */
    public static EvaluationRuntime getInstance() {
        return EvaluationRuntimeImpl.SingletonContainer.INSTANCE;
    }

    private final TypeCastingProvider typeCastingProvider;
    private final FunctionTable functionTable;

    public EvaluationRuntimeImpl(TypeCastingProvider typeCastingProvider, FunctionTable functionTable) {
        this.typeCastingProvider = typeCastingProvider;
        this.functionTable = functionTable;
    }

    @Override
    public boolean canCast(Object value, Type target) {
        return this.typeCastingProvider.canCast(value, target);
    }

    @Override
    public Object cast(EvaluationContext ctx, Object value, Type target) {
        return this.typeCastingProvider.cast(ctx, value, target);
    }

    @Override
    public Function resolveFunction(String name, int args) throws IllegalStateException {
        return functionTable.resolve(name, args);
    }
}
