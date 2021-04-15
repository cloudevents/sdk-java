package io.cloudevents.sql.impl.expressions;

import io.cloudevents.CloudEvent;
import io.cloudevents.sql.EvaluationRuntime;
import io.cloudevents.sql.impl.ExceptionThrower;
import io.cloudevents.sql.impl.ExpressionInternal;
import org.antlr.v4.runtime.misc.Interval;

public abstract class BaseBinaryExpression extends BaseExpression {

    protected final ExpressionInternal leftOperand;
    protected final ExpressionInternal rightOperand;

    protected BaseBinaryExpression(Interval expressionInterval, String expressionText, ExpressionInternal leftOperand, ExpressionInternal rightOperand) {
        super(expressionInterval, expressionText);
        this.leftOperand = leftOperand;
        this.rightOperand = rightOperand;
    }

    abstract Object evaluate(EvaluationRuntime runtime, Object left, Object right, ExceptionThrower exceptions);

    @Override
    public Object evaluate(EvaluationRuntime runtime, CloudEvent event, ExceptionThrower exceptions) {
        Object left = leftOperand.evaluate(runtime, event, exceptions);
        Object right = rightOperand.evaluate(runtime, event, exceptions);
        return evaluate(runtime, left, right, exceptions);
    }
}
