package io.cloudevents.sql.impl.functions;

import io.cloudevents.CloudEvent;
import io.cloudevents.sql.EvaluationContext;
import io.cloudevents.sql.EvaluationException;
import io.cloudevents.sql.EvaluationRuntime;

public class SubstringFunction extends BaseTwoArgumentFunction<String, Integer> {
    public SubstringFunction() {
        super("SUBSTRING", String.class, Integer.class);
    }

    @Override
    Object invoke(EvaluationContext ctx, EvaluationRuntime evaluationRuntime, CloudEvent event, String x, Integer pos) {
        try {
            return SubstringWithLengthFunction.substring(x, pos, null);
        } catch (Exception e) {
            ctx.appendException(EvaluationException.functionExecutionError(
                name(),
                e
            ));
            return "";
        }
    }
}
