package io.cloudevents.sql.impl.expressions;

import io.cloudevents.CloudEvent;
import io.cloudevents.sql.EvaluationRuntime;
import io.cloudevents.sql.ExceptionFactory;
import io.cloudevents.sql.Type;
import io.cloudevents.sql.impl.ExpressionInternal;
import io.cloudevents.sql.impl.runtime.EvaluationContextImpl;
import io.cloudevents.sql.impl.runtime.EvaluationResult;
import io.cloudevents.sql.impl.runtime.TypeCastingProvider;
import org.antlr.v4.runtime.misc.Interval;

import java.util.Objects;

public class ComparisonExpression extends BaseBinaryExpression {

    public ComparisonExpression(Interval expressionInterval, String expressionText, ExpressionInternal leftOperand, ExpressionInternal rightOperand) {
        super(expressionInterval, expressionText, leftOperand, rightOperand);
    }

    // x = y: Boolean x Boolean -> Boolean
    // x = y: Integer x Integer -> Boolean
    // x = y: String x String -> Boolean
    @Override
    public EvaluationResult evaluate(EvaluationRuntime runtime, CloudEvent event, ExceptionFactory exceptionFactory) {
        EvaluationResult left = this.getLeftOperand().evaluate(runtime, event, exceptionFactory);
        EvaluationResult right = this.getRightOperand().evaluate(runtime, event, exceptionFactory);

        if (left.isMissingAttributeException() || right.isMissingAttributeException()) {
            return left.wrap(right).copyWithDefaultValueForType(Type.BOOLEAN);
        }

        left = TypeCastingProvider.cast(
            new EvaluationContextImpl(expressionInterval(), expressionText(), exceptionFactory),
            left,
            Type.fromValue(right.value())
        );

        return new EvaluationResult(Objects.equals(left.value(), right.value()), null, left, right);
    }
}
