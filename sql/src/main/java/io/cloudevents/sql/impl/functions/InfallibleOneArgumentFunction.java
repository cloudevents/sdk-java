package io.cloudevents.sql.impl.functions;

import io.cloudevents.CloudEvent;
import io.cloudevents.sql.EvaluationContext;
import io.cloudevents.sql.EvaluationRuntime;

import java.util.function.Function;

public class InfallibleOneArgumentFunction<T> extends BaseOneArgumentFunction<T> {

    private final Function<T, Object> functionImplementation;

    public InfallibleOneArgumentFunction(String name, Class<T> argumentClass, Function<T, Object> functionImplementation) {
        super(name, argumentClass);
        this.functionImplementation = functionImplementation;
    }

    @Override
    Object invoke(EvaluationContext ctx, EvaluationRuntime evaluationRuntime, CloudEvent event, T argument) {
        return this.functionImplementation.apply(argument);
    }
}
