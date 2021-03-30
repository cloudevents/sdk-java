package io.cloudevents.sql;

import io.cloudevents.CloudEvent;
import io.cloudevents.sql.impl.BaseExpression;
import io.cloudevents.sql.impl.EvaluationContextImpl;
import io.cloudevents.sql.impl.ExpressionInternal;
import org.antlr.v4.runtime.misc.Interval;

import java.util.regex.Pattern;

public class LikeExpression extends BaseExpression {

    private final ExpressionInternal internal;
    private final Pattern pattern;

    public LikeExpression(Interval expressionInterval, String expressionText, ExpressionInternal internal, String pattern) {
        super(expressionInterval, expressionText);
        this.internal = internal;
        // Converting to regex is not the most performant impl, but it works
        this.pattern = Pattern.compile("^" +
            pattern.replaceAll("(?<!\\\\)\\%", ".*")
                .replaceAll("(?<!\\\\)\\_", ".")
                .replaceAll("\\\\\\%", "%")
                .replaceAll("\\\\_", "_") + "$"
        );
    }

    @Override
    public Object evaluate(EvaluationContextImpl ctx, CloudEvent event) {
        String value = castToString(
            ctx,
            internal.evaluate(ctx, event)
        );

        return pattern.matcher(value).matches();
    }
}
