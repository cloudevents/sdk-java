package io.cloudevents.sql.impl.functions;

import io.cloudevents.CloudEvent;
import io.cloudevents.sql.EvaluationContext;
import io.cloudevents.sql.EvaluationRuntime;
import io.cloudevents.sql.Type;

public class BoolFunction extends BaseOneArgumentFunction<String> {

    public BoolFunction() {
        super("BOOL", String.class);
    }

    @Override
    Object invoke(EvaluationContext ctx, EvaluationRuntime evaluationRuntime, CloudEvent event, String argument) {
        return evaluationRuntime.cast(ctx, argument, Type.BOOLEAN);
    }
}
