package io.cloudevents.sql.impl.expressions;

import io.cloudevents.CloudEvent;
import io.cloudevents.sql.EvaluationRuntime;
import io.cloudevents.sql.impl.ExceptionThrower;
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
    public Object evaluate(EvaluationRuntime runtime, CloudEvent event, ExceptionThrower thrower) {
        Object left = leftOperand.evaluate(runtime, event, thrower);
        Object right = rightOperand.evaluate(runtime, event, thrower);
        return evaluate(runtime, left, right, thrower);
    }

    @Override
    public <T> T visit(ExpressionInternalVisitor<T> visitor) {
        return visitor.visitBaseBinaryExpression(this);
    }
}
