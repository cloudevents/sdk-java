package io.cloudevents.sql.impl.expressions;

import io.cloudevents.sql.EvaluationRuntime;
import io.cloudevents.sql.impl.ExceptionThrower;
import org.antlr.v4.runtime.misc.Interval;

public class NegateExpression extends BaseUnaryExpression {

    public NegateExpression(Interval expressionInterval, String expressionText, ExpressionInternal internal) {
        super(expressionInterval, expressionText, internal);
    }

    @Override
    Object evaluate(EvaluationRuntime runtime, Object value, ExceptionThrower exceptions) {
        return -castToInteger(runtime, exceptions, value);
    }
}
