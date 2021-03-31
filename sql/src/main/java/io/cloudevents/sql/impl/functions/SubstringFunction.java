package io.cloudevents.sql.impl.functions;

import io.cloudevents.CloudEvent;
import io.cloudevents.sql.EvaluationContext;
import io.cloudevents.sql.EvaluationException;
import io.cloudevents.sql.EvaluationRuntime;

public class SubstringFunction extends BaseThreeArgumentFunction<String, Integer, Integer> {
    public SubstringFunction() {
        super("SUBSTRING", String.class, Integer.class, Integer.class);
    }

    @Override
    Object invoke(EvaluationContext ctx, EvaluationRuntime evaluationRuntime, CloudEvent event, String x, Integer b, Integer e) {
        if (e < 0) {
            ctx.appendException(
                EvaluationException.functionExecutionError(
                    name(),
                    new IllegalArgumentException("The e parameter is lower than 0: " + e)
                )
            );
            return x;
        }
        if (e < b) {
            ctx.appendException(
                EvaluationException.functionExecutionError(
                    name(),
                    new IllegalArgumentException("The e parameter is lower than b: " + e + " < " + b)
                )
            );
            return x;
        }
        int beginning = b < 0 ? 0 : b;
        int end = e > x.length() ? x.length() : e;
        return x.substring(beginning, end);
    }
}
