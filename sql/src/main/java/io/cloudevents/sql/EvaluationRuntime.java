package io.cloudevents.sql;

public interface EvaluationRuntime {

    boolean canCast(Object value, Type target);

    Object cast(EvaluationContext ctx, Object value, Type target);

    Function resolveFunction(String name, int args) throws IllegalStateException;

}
