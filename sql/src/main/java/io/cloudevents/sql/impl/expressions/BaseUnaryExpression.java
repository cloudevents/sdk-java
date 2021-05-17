package io.cloudevents.sql.impl.expressions;

import io.cloudevents.CloudEvent;
import io.cloudevents.sql.EvaluationRuntime;
import io.cloudevents.sql.impl.ExceptionThrower;
import org.antlr.v4.runtime.misc.Interval;

public abstract class BaseUnaryExpression extends BaseExpression {

    protected ExpressionInternal internal;

    public BaseUnaryExpression(Interval expressionInterval, String expressionText, ExpressionInternal internal) {
        super(expressionInterval, expressionText);
        this.internal = internal;
    }

    public abstract Object evaluate(EvaluationRuntime runtime, Object value, ExceptionThrower exceptions);

    @Override
    public Object evaluate(EvaluationRuntime runtime, CloudEvent event, ExceptionThrower thrower) {
        return evaluate(runtime, internal.evaluate(runtime, event, thrower), thrower);
    }

    @Override
    public <T> T visit(ExpressionInternalVisitor<T> visitor) {
        return visitor.visitBaseUnaryExpression(this);
    }

    public ExpressionInternal getOperand() {
        return internal;
    }

    public BaseUnaryExpression setOperand(ExpressionInternal internal) {
        this.internal = internal;
        return this;
    }
}
