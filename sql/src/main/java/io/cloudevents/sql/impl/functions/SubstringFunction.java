package io.cloudevents.sql.impl.functions;

import io.cloudevents.CloudEvent;
import io.cloudevents.sql.EvaluationContext;
import io.cloudevents.sql.EvaluationRuntime;

public class SubstringFunction extends BaseThreeArgumentFunction<String, String, Integer> {
    public SubstringFunction() {
        super("SUBSTRING", String.class, String.class, Integer.class);
    }

    @Override
    Object invoke(EvaluationContext ctx, EvaluationRuntime evaluationRuntime, CloudEvent event, String s, String s2, Integer index) {
        //TODO
        return null;
    }
}
