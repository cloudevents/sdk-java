package io.cloudevents.sql.impl.functions;

import io.cloudevents.CloudEvent;
import io.cloudevents.sql.EvaluationContext;
import io.cloudevents.sql.EvaluationRuntime;
import io.cloudevents.sql.Type;

public class IsBoolFunction extends BaseOneArgumentFunction<String> {

    public IsBoolFunction() {
        super("IS_BOOL", String.class);
    }

    @Override
    Object invoke(EvaluationContext ctx, EvaluationRuntime evaluationRuntime, CloudEvent event, String argument) {
        return evaluationRuntime.canCast(argument, Type.BOOLEAN);
    }
}
