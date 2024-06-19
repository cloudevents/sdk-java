package io.cloudevents.sql.impl.expressions;

import io.cloudevents.sql.EvaluationRuntime;
import io.cloudevents.sql.ExceptionFactory;
import io.cloudevents.sql.Type;
import io.cloudevents.sql.impl.ExceptionFactoryImpl;
import io.cloudevents.sql.impl.ExpressionInternal;
import io.cloudevents.sql.impl.runtime.EvaluationResult;
import org.antlr.v4.runtime.misc.Interval;

public class NegateExpression extends BaseUnaryExpression {

    public NegateExpression(Interval expressionInterval, String expressionText, ExpressionInternal internal) {
        super(expressionInterval, expressionText, internal);
    }

    @Override
    public Type returnType() {
        return Type.INTEGER;
    }

    @Override
    public EvaluationResult evaluate(EvaluationRuntime runtime, EvaluationResult result, ExceptionFactory exceptions) {
        EvaluationResult x = castToInteger(exceptions, result);
        return x.copyWithValue(-(Integer)x.value());
    }
}
