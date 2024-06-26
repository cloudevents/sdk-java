package io.cloudevents.sql.impl.functions;

import io.cloudevents.CloudEvent;
import io.cloudevents.sql.EvaluationContext;
import io.cloudevents.sql.EvaluationRuntime;
import io.cloudevents.sql.impl.runtime.EvaluationResult;

public class SubstringWithLengthFunction extends BaseThreeArgumentFunction<String, Integer, Integer, String> {
    public SubstringWithLengthFunction() {
        super("SUBSTRING", String.class, Integer.class, Integer.class, String.class);
    }

    @Override
    EvaluationResult invoke(EvaluationContext ctx, EvaluationRuntime evaluationRuntime, CloudEvent event, String x, Integer pos, Integer len) {
        try {
            return new EvaluationResult(substring(x, pos, len));
        } catch (Exception e) {
            return new EvaluationResult("", ctx.exceptionFactory().functionExecutionError(name(), e).create(ctx.expressionInterval(), ctx.expressionText()));
        }
    }

    static String substring(String x, Integer pos, Integer len) throws IllegalArgumentException {
        if (pos == 0) {
            return "";
        }
        if (pos < -x.length() || pos > x.length()) {
            throw new IllegalArgumentException("The pos argument is out of bounds: " + pos);
        }
        int beginning;
        if (pos < 0) {
            beginning = x.length() + pos;
        } else {
            // Indexes are 1-based
            beginning = pos - 1;
        }
        int end;
        if (len == null || beginning + len > x.length()) {
            end = x.length();
        } else {
            end = beginning + len;
        }
        return x.substring(beginning, end);
    }
}
