package io.cloudevents.sql.impl.functions;

import io.cloudevents.CloudEvent;
import io.cloudevents.sql.EvaluationContext;
import io.cloudevents.sql.EvaluationRuntime;

public class RightFunction extends BaseTwoArgumentFunction<String, Integer> {
    public RightFunction() {
        super("RIGHT", String.class, Integer.class);
    }

    @Override
    Object invoke(EvaluationContext ctx, EvaluationRuntime evaluationRuntime, CloudEvent event, String s, Integer s2) {
        //TODO
        return null;
    }
}
