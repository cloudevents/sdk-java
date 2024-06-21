package io.cloudevents.sql.impl.expressions;

import io.cloudevents.CloudEvent;
import io.cloudevents.sql.EvaluationRuntime;
import io.cloudevents.sql.ExceptionFactory;
import io.cloudevents.sql.Type;
import io.cloudevents.sql.impl.ExpressionInternal;
import io.cloudevents.sql.impl.ExpressionInternalVisitor;
import io.cloudevents.sql.impl.runtime.EvaluationContextImpl;
import io.cloudevents.sql.impl.runtime.EvaluationResult;
import io.cloudevents.sql.impl.runtime.TypeCastingProvider;
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
    public EvaluationResult evaluate(EvaluationRuntime runtime, CloudEvent event, ExceptionFactory exceptionFactory) {
        EvaluationResult leftValue = leftExpression.evaluate(runtime, event, exceptionFactory);
        for (ExpressionInternal setExpression : this.setExpressions) {
            EvaluationResult rightValue = TypeCastingProvider.cast(
                    new EvaluationContextImpl(expressionInterval(), expressionText(), exceptionFactory),
                    setExpression.evaluate(runtime, event, exceptionFactory),
                    Type.fromValue(leftValue.value())
            );

            if (Objects.equals(leftValue.value(), rightValue.value())) {
                return new EvaluationResult(true, null, leftValue, rightValue);
            } else {
                leftValue.wrapExceptions(rightValue);
            }
        }
        return leftValue.copyWithValue(false);
    }

    @Override
    public <T> T visit(ExpressionInternalVisitor<T> visitor) {
        return visitor.visitInExpression(this);
    }
}
