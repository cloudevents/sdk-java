package io.cloudevents.sql.impl.expressions;

import io.cloudevents.sql.EvaluationRuntime;
import io.cloudevents.sql.impl.ExceptionFactory;
import io.cloudevents.sql.impl.ExceptionThrower;
import io.cloudevents.sql.impl.ExpressionInternal;
import org.antlr.v4.runtime.misc.Interval;

public class DivisionExpression extends BaseIntegerBinaryExpression {

    public DivisionExpression(Interval expressionInterval, String expressionText, ExpressionInternal leftOperand, ExpressionInternal rightOperand) {
        super(expressionInterval, expressionText, leftOperand, rightOperand);
    }

    @Override
    Object evaluate(EvaluationRuntime runtime, int left, int right, ExceptionThrower exceptions) {
        if (right == 0) {
            exceptions.throwException(
                ExceptionFactory.divisionByZero(expressionInterval(), expressionText(), left)
            );
            return 0;
        }
        return left / right;
    }

}
