package io.cloudevents.sql.impl.functions;

import io.cloudevents.CloudEvent;
import io.cloudevents.sql.EvaluationContext;
import io.cloudevents.sql.EvaluationRuntime;
import io.cloudevents.sql.Type;

public class IntFunction extends BaseOneArgumentFunction<String> {

    public IntFunction() {
        super("INT", String.class);
    }

    @Override
    public Object invoke(EvaluationContext ctx, EvaluationRuntime evaluationRuntime, CloudEvent event, String argument) {
        return evaluationRuntime.cast(ctx, argument, Type.INTEGER);
    }
}
