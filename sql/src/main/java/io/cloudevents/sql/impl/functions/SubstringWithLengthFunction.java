package io.cloudevents.sql.impl.functions;

import io.cloudevents.CloudEvent;
import io.cloudevents.sql.EvaluationContext;
import io.cloudevents.sql.EvaluationRuntime;
import io.cloudevents.sql.impl.ExceptionFactory;

public class SubstringWithLengthFunction extends BaseThreeArgumentFunction<String, Integer, Integer> {
    public SubstringWithLengthFunction() {
        super("SUBSTRING", String.class, Integer.class, Integer.class);
    }

    @Override
    Object invoke(EvaluationContext ctx, EvaluationRuntime evaluationRuntime, CloudEvent event, String x, Integer pos, Integer len) {
        try {
            return substring(x, pos, len);
        } catch (Exception e) {
            ctx.appendException(ExceptionFactory.functionExecutionError(
                name(),
                e
            ));
            return "";
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
