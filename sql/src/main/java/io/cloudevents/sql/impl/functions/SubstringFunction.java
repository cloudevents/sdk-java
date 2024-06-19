package io.cloudevents.sql.impl.functions;

import io.cloudevents.CloudEvent;
import io.cloudevents.sql.EvaluationContext;
import io.cloudevents.sql.EvaluationRuntime;
import io.cloudevents.sql.impl.runtime.EvaluationResult;

public class SubstringFunction extends BaseTwoArgumentFunction<String, Integer, String> {
    public SubstringFunction() {
        super("SUBSTRING", String.class, Integer.class, String.class);
    }

    @Override
    EvaluationResult invoke(EvaluationContext ctx, EvaluationRuntime evaluationRuntime, CloudEvent event, String x, Integer pos) {
        try {
            return new EvaluationResult(SubstringWithLengthFunction.substring(x, pos, null));
        } catch (Exception e) {
            return new EvaluationResult("", ctx.exceptionFactory().functionExecutionError(name(), e).create(ctx.expressionInterval(), ctx.expressionText()));
        }
    }
}
