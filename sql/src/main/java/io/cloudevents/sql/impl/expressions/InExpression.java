package io.cloudevents.sql.impl.expressions;

import io.cloudevents.CloudEvent;
import io.cloudevents.sql.EvaluationRuntime;
import io.cloudevents.sql.Type;
import io.cloudevents.sql.impl.EvaluationContextImpl;
import io.cloudevents.sql.impl.ExceptionThrower;
import org.antlr.v4.runtime.misc.Interval;

import java.util.List;
import java.util.Objects;

public class InExpression extends BaseExpression {

    // leftOperand IN (setExpressions...)
    private final ExpressionInternal leftExpression;
    private final List<ExpressionInternal> setExpressions;

    // TODO this expression can be optimized if the ExpressionInternal are all ValueExpression (aka set is composed by literals)
    public InExpression(Interval expressionInterval, String expressionText, ExpressionInternal leftExpression, List<ExpressionInternal> setExpressions) {
        super(expressionInterval, expressionText);
        this.leftExpression = leftExpression;
        this.setExpressions = setExpressions;
    }

    @Override
    public Object evaluate(EvaluationRuntime runtime, CloudEvent event, ExceptionThrower thrower) {
        Object leftValue = leftExpression.evaluate(runtime, event, thrower);
        return setExpressions.stream()
            .anyMatch(expr -> {
                Object rightValue = runtime.cast(
                    new EvaluationContextImpl(expressionInterval(), expressionText(), thrower),
                    expr.evaluate(runtime, event, thrower),
                    Type.fromValue(leftValue)
                );

                return Objects.equals(leftValue, rightValue);
            });
    }

    @Override
    public <T> T visit(ExpressionInternalVisitor<T> visitor) {
        return visitor.visitInExpression(this);
    }
}
