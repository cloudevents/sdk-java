package io.cloudevents.sql.impl.expressions;

import io.cloudevents.sql.EvaluationRuntime;
import io.cloudevents.sql.impl.ExceptionThrower;
import io.cloudevents.sql.impl.ExpressionInternal;
import org.antlr.v4.runtime.misc.Interval;

public abstract class BaseIntegerBinaryExpression extends BaseBinaryExpression {

    public BaseIntegerBinaryExpression(Interval expressionInterval, String expressionText, ExpressionInternal leftOperand, ExpressionInternal rightOperand) {
        super(expressionInterval, expressionText, leftOperand, rightOperand);
    }

    abstract Object evaluate(EvaluationRuntime runtime, int left, int right, ExceptionThrower exceptions);

    @Override
    public Object evaluate(EvaluationRuntime runtime, Object left, Object right, ExceptionThrower exceptions) {
        return this.evaluate(
            runtime,
            castToInteger(runtime, exceptions, left).intValue(),
            castToInteger(runtime, exceptions, right).intValue(),
            exceptions
        );
    }

}
