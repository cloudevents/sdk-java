package io.cloudevents.sql.impl.expressions;

import io.cloudevents.CloudEvent;
import io.cloudevents.sql.EvaluationRuntime;
import io.cloudevents.sql.ExceptionFactory;
import io.cloudevents.sql.impl.ExceptionFactoryImpl;
import io.cloudevents.sql.impl.ExpressionInternal;
import io.cloudevents.sql.impl.ExpressionInternalVisitor;
import io.cloudevents.sql.impl.runtime.EvaluationResult;
import org.antlr.v4.runtime.misc.Interval;

public abstract class BaseBinaryExpression extends BaseExpression {

    protected ExpressionInternal leftOperand;
    protected ExpressionInternal rightOperand;

    protected BaseBinaryExpression(Interval expressionInterval, String expressionText, ExpressionInternal leftOperand, ExpressionInternal rightOperand) {
        super(expressionInterval, expressionText);
        this.leftOperand = leftOperand;
        this.rightOperand = rightOperand;
    }

    public abstract EvaluationResult evaluate(EvaluationRuntime runtime, CloudEvent event, ExceptionFactory exceptionFactory);

    @Override
    public <T> T visit(ExpressionInternalVisitor<T> visitor) {
        return visitor.visitBaseBinaryExpression(this);
    }

    public ExpressionInternal getLeftOperand() {
        return leftOperand;
    }

    public ExpressionInternal getRightOperand() {
        return rightOperand;
    }

    public BaseBinaryExpression setLeftOperand(ExpressionInternal leftOperand) {
        this.leftOperand = leftOperand;
        return this;
    }

    public BaseBinaryExpression setRightOperand(ExpressionInternal rightOperand) {
        this.rightOperand = rightOperand;
        return this;
    }
}
