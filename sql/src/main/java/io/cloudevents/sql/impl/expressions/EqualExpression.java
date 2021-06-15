package io.cloudevents.sql.impl.expressions;

import io.cloudevents.sql.EvaluationRuntime;
import io.cloudevents.sql.Type;
import io.cloudevents.sql.impl.ExceptionThrower;
import io.cloudevents.sql.impl.ExpressionInternal;
import io.cloudevents.sql.impl.runtime.EvaluationContextImpl;
import org.antlr.v4.runtime.misc.Interval;

import java.util.Objects;

public class EqualExpression extends BaseBinaryExpression {

    public EqualExpression(Interval expressionInterval, String expressionText, ExpressionInternal leftOperand, ExpressionInternal rightOperand) {
        super(expressionInterval, expressionText, leftOperand, rightOperand);
    }

    // x = y: Boolean x Boolean -> Boolean
    // x = y: Integer x Integer -> Boolean
    // x = y: String x String -> Boolean
    @Override
    public Object evaluate(EvaluationRuntime runtime, Object left, Object right, ExceptionThrower exceptions) {
        left = runtime.cast(
            new EvaluationContextImpl(expressionInterval(), expressionText(), exceptions),
            left,
            Type.fromValue(right)
        );
        return Objects.equals(left, right);
    }
}
