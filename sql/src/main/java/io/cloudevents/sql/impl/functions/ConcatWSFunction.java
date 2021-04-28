package io.cloudevents.sql.impl.functions;

import io.cloudevents.CloudEvent;
import io.cloudevents.sql.EvaluationContext;
import io.cloudevents.sql.EvaluationRuntime;
import io.cloudevents.sql.Type;

import java.util.List;
import java.util.stream.Collectors;

public class ConcatWSFunction extends BaseFunction {

    public ConcatWSFunction() {
        super("CONCAT_WS");
    }

    @Override
    public Object invoke(EvaluationContext ctx, EvaluationRuntime evaluationRuntime, CloudEvent event, List<Object> arguments) {
        return arguments.stream()
            .skip(1)
            .map(o -> (String) o)
            .collect(Collectors.joining((String) arguments.get(0)));
    }

    @Override
    public Type typeOfParameter(int i) throws IllegalArgumentException {
        return Type.STRING;
    }

    @Override
    public int arity() {
        return 1;
    }

    @Override
    public boolean isVariadic() {
        return true;
    }
}
