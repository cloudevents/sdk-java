package io.cloudevents.sql.impl.expressions;

import io.cloudevents.CloudEvent;
import io.cloudevents.sql.EvaluationRuntime;
import io.cloudevents.sql.impl.ExceptionThrower;
import io.cloudevents.sql.impl.ExpressionInternal;
import io.cloudevents.sql.impl.ExpressionInternalVisitor;
import org.antlr.v4.runtime.misc.Interval;

import java.util.regex.Pattern;

public class LikeExpression extends BaseExpression {

    private final ExpressionInternal internal;
    private final Pattern pattern;

    public LikeExpression(Interval expressionInterval, String expressionText, ExpressionInternal internal, String pattern) {
        super(expressionInterval, expressionText);
        this.internal = internal;
        // Converting to regex is not the most performant impl, but it works
        this.pattern = convertLikePatternToRegex(pattern);
    }

    @Override
    public Object evaluate(EvaluationRuntime runtime, CloudEvent event, ExceptionThrower thrower) {
        String value = castToString(
            runtime,
            thrower,
            internal.evaluate(runtime, event, thrower)
        );

        return pattern.matcher(value).matches();
    }

    private Pattern convertLikePatternToRegex(String pattern) {
        StringBuilder builder = new StringBuilder();
        builder.append("^\\Q");

        for (int i = 0; i < pattern.length(); i++) {
            if (pattern.charAt(i) == '\\' && i < pattern.length() - 1) {
                if (pattern.charAt(i + 1) == '%') {
                    // \% case
                    builder.append('%');
                    i++;
                    continue;
                } else if (pattern.charAt(i + 1) == '_') {
                    // \_ case
                    builder.append('_');
                    i++;
                    continue;
                }
            }
            if (pattern.charAt(i) == '_') {
                // replace with .
                builder.append("\\E.\\Q");
            } else if (pattern.charAt(i) == '%') {
                // replace with .*
                builder.append("\\E.*\\Q");
            } else {
                builder.append(pattern.charAt(i));
            }
        }

        builder.append("\\E$");

        return Pattern.compile(builder.toString());
    }

    @Override
    public <T> T visit(ExpressionInternalVisitor<T> visitor) {
        return visitor.visitLikeExpression(this);
    }
}
