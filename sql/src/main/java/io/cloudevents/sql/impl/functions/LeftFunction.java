package io.cloudevents.sql.impl.functions;

import io.cloudevents.CloudEvent;
import io.cloudevents.sql.EvaluationContext;
import io.cloudevents.sql.EvaluationRuntime;

public class LeftFunction extends BaseTwoArgumentFunction<String, String> {
    public LeftFunction() {
        super("LEFT", String.class, String.class);
    }

    @Override
    Object invoke(EvaluationContext ctx, EvaluationRuntime evaluationRuntime, CloudEvent event, String s, String s2) {
        //TODO
        return null;
    }
}
