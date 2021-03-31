package io.cloudevents.sql.impl.functions;

import io.cloudevents.CloudEvent;
import io.cloudevents.sql.EvaluationContext;
import io.cloudevents.sql.EvaluationRuntime;

public class LeftFunction extends BaseTwoArgumentFunction<String, Integer> {
    public LeftFunction() {
        super("LEFT", String.class, Integer.class);
    }

    @Override
    Object invoke(EvaluationContext ctx, EvaluationRuntime evaluationRuntime, CloudEvent event, String s, Integer index) {
        //TODO
        return null;
    }
}
