package io.cloudevents.sql;

import io.cloudevents.sql.impl.EvaluationRuntimeBuilder;

public interface EvaluationRuntime {

    boolean canCast(Object value, Type target);

    Object cast(EvaluationContext ctx, Object value, Type target);

    Function resolveFunction(String name, int args) throws IllegalStateException;

    static EvaluationRuntimeBuilder builder() {
        return new EvaluationRuntimeBuilder();
    }

}
