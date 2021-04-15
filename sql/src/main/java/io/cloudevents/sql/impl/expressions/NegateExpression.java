package io.cloudevents.sql.impl.expressions;

import io.cloudevents.CloudEvent;
import io.cloudevents.sql.EvaluationRuntime;
import io.cloudevents.sql.impl.ExceptionThrower;
import io.cloudevents.sql.impl.ExpressionInternal;
import org.antlr.v4.runtime.misc.Interval;

public class NegateExpression extends BaseExpression {

    private final ExpressionInternal internal;

    public NegateExpression(Interval expressionInterval, String expressionText, ExpressionInternal internal) {
        super(expressionInterval, expressionText);
        this.internal = internal;
    }

    @Override
    public Object evaluate(EvaluationRuntime runtime, CloudEvent event, ExceptionThrower exceptions) {
        return -castToInteger(runtime, exceptions, internal.evaluate(runtime, event, exceptions));
    }

}
