package io.cloudevents.sql.impl.functions;

import io.cloudevents.CloudEvent;
import io.cloudevents.sql.EvaluationContext;
import io.cloudevents.sql.EvaluationRuntime;
import io.cloudevents.sql.impl.runtime.EvaluationResult;

import java.util.function.Function;

public class InfallibleOneArgumentFunction<T, U> extends BaseOneArgumentFunction<T, U> {

    private final Function<T, U> functionImplementation;

    public InfallibleOneArgumentFunction(String name, Class<T> argumentClass, Class<U> returnClass, Function<T, U> functionImplementation) {
        super(name, argumentClass, returnClass);
        this.functionImplementation = functionImplementation;
    }

    @Override
    EvaluationResult invoke(EvaluationContext ctx, EvaluationRuntime evaluationRuntime, CloudEvent event, T argument) {
        return new EvaluationResult(this.functionImplementation.apply(argument));
    }
}
