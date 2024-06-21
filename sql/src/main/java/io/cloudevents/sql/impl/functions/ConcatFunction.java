package io.cloudevents.sql.impl.functions;

import io.cloudevents.CloudEvent;
import io.cloudevents.sql.EvaluationContext;
import io.cloudevents.sql.EvaluationRuntime;
import io.cloudevents.sql.Type;
import io.cloudevents.sql.impl.runtime.EvaluationResult;

import java.util.List;
import java.util.stream.Collectors;

public class ConcatFunction extends BaseFunction {

    public ConcatFunction() {
        super("CONCAT");
    }

    @Override
    public EvaluationResult invoke(EvaluationContext ctx, EvaluationRuntime evaluationRuntime, CloudEvent event, List<Object> arguments) {
        return new EvaluationResult(arguments.stream()
            .map(o -> (String) o)
            .collect(Collectors.joining()));
    }

    @Override
    public Type typeOfParameter(int i) throws IllegalArgumentException {
        return Type.STRING;
    }

    @Override
    public int arity() {
        return 0;
    }

    @Override
    public boolean isVariadic() {
        return true;
    }

    @Override
    public Type returnType() {
        return Type.STRING;
    }
}
