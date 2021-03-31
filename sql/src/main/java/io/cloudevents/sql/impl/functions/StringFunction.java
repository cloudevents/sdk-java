package io.cloudevents.sql.impl.functions;

import io.cloudevents.CloudEvent;
import io.cloudevents.sql.EvaluationContext;
import io.cloudevents.sql.EvaluationRuntime;
import io.cloudevents.sql.Type;

public class StringFunction extends BaseOneArgumentFunction<Object> {

    public StringFunction() {
        super("STRING", Object.class);
    }

    @Override
    public Object invoke(EvaluationContext ctx, EvaluationRuntime evaluationRuntime, CloudEvent event, Object argument) {
        return evaluationRuntime.cast(ctx, argument, Type.STRING);
    }
}
