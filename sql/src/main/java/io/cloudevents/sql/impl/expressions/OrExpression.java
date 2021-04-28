package io.cloudevents.sql.impl.expressions;

import io.cloudevents.sql.EvaluationRuntime;
import io.cloudevents.sql.impl.ExceptionThrower;
import io.cloudevents.sql.impl.ExpressionInternal;
import org.antlr.v4.runtime.misc.Interval;

public class OrExpression extends BaseBinaryExpression {

    public OrExpression(Interval expressionInterval, String expressionText, ExpressionInternal leftOperand, ExpressionInternal rightOperand) {
        super(expressionInterval, expressionText, leftOperand, rightOperand);
    }

    @Override
    Object evaluate(EvaluationRuntime runtime, Object left, Object right, ExceptionThrower exceptions) {
        boolean x = castToBoolean(runtime, exceptions, left);
        if (x) {
            // Short circuit
            return true;
        }
        return castToBoolean(runtime, exceptions, right);
    }
}
