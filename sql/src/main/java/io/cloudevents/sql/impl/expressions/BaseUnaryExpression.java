package io.cloudevents.sql.impl.expressions;

import io.cloudevents.CloudEvent;
import io.cloudevents.sql.EvaluationRuntime;
import io.cloudevents.sql.ExceptionFactory;
import io.cloudevents.sql.Type;
import io.cloudevents.sql.impl.ExceptionFactoryImpl;
import io.cloudevents.sql.impl.ExpressionInternal;
import io.cloudevents.sql.impl.ExpressionInternalVisitor;
import io.cloudevents.sql.impl.runtime.EvaluationResult;
import org.antlr.v4.runtime.misc.Interval;

public abstract class BaseUnaryExpression extends BaseExpression {

    protected ExpressionInternal internal;

    public BaseUnaryExpression(Interval expressionInterval, String expressionText, ExpressionInternal internal) {
        super(expressionInterval, expressionText);
        this.internal = internal;
    }

    public abstract EvaluationResult evaluate(EvaluationRuntime runtime, EvaluationResult result, ExceptionFactory exceptionFactory);

    public abstract Type returnType();

    @Override
    public EvaluationResult evaluate(EvaluationRuntime runtime, CloudEvent event, ExceptionFactory exceptionFactory) {
        EvaluationResult value = internal.evaluate(runtime, event, exceptionFactory);
        if (value.isMissingAttributeException()) {
            return value.copyWithDefaultValueForType(this.returnType());
        }
        return evaluate(runtime, value, exceptionFactory);
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
