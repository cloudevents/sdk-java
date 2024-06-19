package io.cloudevents.sql.impl.functions;

import io.cloudevents.CloudEvent;
import io.cloudevents.sql.EvaluationContext;
import io.cloudevents.sql.EvaluationRuntime;
import io.cloudevents.sql.impl.runtime.EvaluationResult;

public class AbsFunction extends BaseOneArgumentFunction<Integer, Integer> {
    public AbsFunction() {
        super("ABS", Integer.class, Integer.class);
    }

    @Override
    public EvaluationResult invoke(EvaluationContext ctx, EvaluationRuntime evaluationRuntime, CloudEvent event, Integer argument) {
        if (argument == Integer.MIN_VALUE) {
            return new EvaluationResult(Integer.MAX_VALUE, ctx.exceptionFactory().mathError(ctx.expressionInterval(), ctx.expressionText(), "integer overflow while computing absolute value of " + Integer.MIN_VALUE));
        }
        return new EvaluationResult(Math.abs(argument));
    }
}
