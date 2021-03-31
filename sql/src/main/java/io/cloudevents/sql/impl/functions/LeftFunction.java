package io.cloudevents.sql.impl.functions;

import io.cloudevents.CloudEvent;
import io.cloudevents.sql.EvaluationContext;
import io.cloudevents.sql.EvaluationException;
import io.cloudevents.sql.EvaluationRuntime;

public class LeftFunction extends BaseTwoArgumentFunction<String, Integer> {
    public LeftFunction() {
        super("LEFT", String.class, Integer.class);
    }

    @Override
    Object invoke(EvaluationContext ctx, EvaluationRuntime evaluationRuntime, CloudEvent event, String s, Integer length) {
        if (length > s.length()) {
            return s;
        }
        if (length < 0) {
            ctx.appendException(
                EvaluationException.functionExecutionError("LEFT", new IllegalArgumentException("The length of the LEFT substring is lower than 0: " + length))
            );
            return s;
        }
        return s.substring(0, length);
    }
}
