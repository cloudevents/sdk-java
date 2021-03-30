package io.cloudevents.sql.impl;

import io.cloudevents.CloudEvent;
import org.antlr.v4.runtime.misc.Interval;

public abstract class BaseBinaryExpression extends BaseExpression {

    protected final ExpressionInternal leftOperand;
    protected final ExpressionInternal rightOperand;

    protected BaseBinaryExpression(Interval expressionInterval, String expressionText, ExpressionInternal leftOperand, ExpressionInternal rightOperand) {
        super(expressionInterval, expressionText);
        this.leftOperand = leftOperand;
        this.rightOperand = rightOperand;
    }

    abstract Object evaluate(EvaluationContextImpl ctx, Object left, Object right);

    @Override
    public Object evaluate(EvaluationContextImpl ctx, CloudEvent event) {
        Object left = leftOperand.evaluate(ctx, event);
        Object right = rightOperand.evaluate(ctx, event);
        return evaluate(ctx, left, right);
    }
}
