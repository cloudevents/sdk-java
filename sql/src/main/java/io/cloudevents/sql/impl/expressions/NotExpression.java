package io.cloudevents.sql.impl.expressions;

import io.cloudevents.sql.EvaluationRuntime;
import io.cloudevents.sql.ExceptionFactory;
import io.cloudevents.sql.Type;
import io.cloudevents.sql.impl.ExpressionInternal;
import io.cloudevents.sql.impl.runtime.EvaluationResult;
import org.antlr.v4.runtime.misc.Interval;

public class NotExpression extends BaseUnaryExpression {

    public NotExpression(Interval expressionInterval, String expressionText, ExpressionInternal internal) {
        super(expressionInterval, expressionText, internal);
    }

    @Override
    public Type returnType() {
        return Type.BOOLEAN;
    }

    @Override
    public EvaluationResult evaluate(EvaluationRuntime runtime, EvaluationResult value, ExceptionFactory exceptions) {
        EvaluationResult x = castToBoolean(exceptions, value);
        return x.copyWithValue(!(Boolean)x.value());
    }
}
