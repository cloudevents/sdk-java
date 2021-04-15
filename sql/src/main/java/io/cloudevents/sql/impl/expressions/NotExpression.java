package io.cloudevents.sql.impl.expressions;

import io.cloudevents.CloudEvent;
import io.cloudevents.sql.EvaluationRuntime;
import io.cloudevents.sql.impl.ExceptionThrower;
import io.cloudevents.sql.impl.ExpressionInternal;
import org.antlr.v4.runtime.misc.Interval;

public class NotExpression extends BaseExpression {

    private final ExpressionInternal internal;

    public NotExpression(Interval expressionInterval, String expressionText, ExpressionInternal internal) {
        super(expressionInterval, expressionText);
        this.internal = internal;
    }

    @Override
    public Object evaluate(EvaluationRuntime runtime, CloudEvent event, ExceptionThrower thrower) {
        return !castToBoolean(runtime, thrower, internal.evaluate(runtime, event, thrower));
    }
}
