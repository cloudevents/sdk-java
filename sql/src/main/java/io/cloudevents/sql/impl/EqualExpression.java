package io.cloudevents.sql.impl;

import io.cloudevents.sql.Type;
import org.antlr.v4.runtime.misc.Interval;

import java.util.Objects;

public class EqualExpression extends BaseBinaryExpression {

    protected EqualExpression(Interval expressionInterval, String expressionText, ExpressionInternal leftOperand, ExpressionInternal rightOperand) {
        super(expressionInterval, expressionText, leftOperand, rightOperand);
    }

    // x = y: Boolean x Boolean -> Boolean
    // x = y: Integer x Integer -> Boolean
    // x = y: String x String -> Boolean
    @Override
    Object evaluate(EvaluationContextImpl ctx, Object left, Object right) {
        left = ctx.getRuntime().getTypeCastingProvider().cast(
            ctx,
            ctx.expressionInterval(),
            ctx.expressionText(),
            left,
            Type.fromValue(right)
        );

        return Objects.equals(left, right);
    }
}
