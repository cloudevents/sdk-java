package io.cloudevents.sql.impl.expressions;

import io.cloudevents.CloudEvent;
import io.cloudevents.sql.EvaluationRuntime;
import io.cloudevents.sql.ExceptionFactory;
import io.cloudevents.sql.Type;
import io.cloudevents.sql.impl.ExceptionFactoryImpl;
import io.cloudevents.sql.impl.ExpressionInternal;
import io.cloudevents.sql.impl.runtime.EvaluationResult;
import org.antlr.v4.runtime.misc.Interval;

public abstract class BaseIntegerBinaryExpression extends BaseBinaryExpression {

    public BaseIntegerBinaryExpression(Interval expressionInterval, String expressionText, ExpressionInternal leftOperand, ExpressionInternal rightOperand) {
        super(expressionInterval, expressionText, leftOperand, rightOperand);
    }

    abstract EvaluationResult evaluate(EvaluationRuntime runtime, int left, int right, ExceptionFactory exceptionFactory);

    @Override
    public EvaluationResult evaluate(EvaluationRuntime runtime, CloudEvent event, ExceptionFactory exceptionFactory) {
        EvaluationResult left = this.getLeftOperand().evaluate(runtime, event, exceptionFactory);
        EvaluationResult right = this.getRightOperand().evaluate(runtime, event, exceptionFactory);

        if (left.isMissingAttributeException() || right.isMissingAttributeException()) {
            return left.wrap(right).copyWithDefaultValueForType(Type.INTEGER);
        }

        EvaluationResult x = castToInteger(exceptionFactory, left);
        EvaluationResult y = castToInteger(exceptionFactory, right);

        return this.evaluate(
            runtime,
            (Integer)x.value(),
            (Integer)y.value(),
            exceptionFactory
        ).wrap(x).wrap(y);
    }

}
