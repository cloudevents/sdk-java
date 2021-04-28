package io.cloudevents.sql.impl.functions;

import io.cloudevents.CloudEvent;
import io.cloudevents.sql.EvaluationContext;
import io.cloudevents.sql.EvaluationRuntime;
import io.cloudevents.sql.Type;

import java.util.List;

public abstract class BaseOneArgumentFunction<T> extends BaseFunction {

    private final Type argumentClass;

    public BaseOneArgumentFunction(String name, Class<T> argumentClass) {
        super(name);
        this.argumentClass = Type.fromClass(argumentClass);
    }

    abstract Object invoke(EvaluationContext ctx, EvaluationRuntime evaluationRuntime, CloudEvent event, T argument);

    @SuppressWarnings("unchecked")
    @Override
    public Object invoke(EvaluationContext ctx, EvaluationRuntime evaluationRuntime, CloudEvent event, List<Object> arguments) {
        return this.invoke(ctx, evaluationRuntime, event, (T) arguments.get(0));
    }

    @Override
    public Type typeOfParameter(int i) {
        requireValidParameterIndex(i);
        return argumentClass;
    }

    @Override
    public int arity() {
        return 1;
    }

    @Override
    public boolean isVariadic() {
        return false;
    }
}
