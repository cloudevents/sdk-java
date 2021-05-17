package io.cloudevents.sql.impl.expressions;

import io.cloudevents.sql.EvaluationRuntime;
import io.cloudevents.sql.impl.ExceptionThrower;
import org.antlr.v4.runtime.misc.Interval;

public class XorExpression extends BaseBinaryExpression {

    public XorExpression(Interval expressionInterval, String expressionText, ExpressionInternal leftOperand, ExpressionInternal rightOperand) {
        super(expressionInterval, expressionText, leftOperand, rightOperand);
    }

    @Override
    Object evaluate(EvaluationRuntime runtime, Object left, Object right, ExceptionThrower exceptions) {
        return Boolean.logicalXor(
            castToBoolean(runtime, exceptions, left),
            castToBoolean(runtime, exceptions, right)
        );
    }
}
