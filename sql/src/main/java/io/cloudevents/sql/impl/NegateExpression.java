package io.cloudevents.sql.impl;

import io.cloudevents.CloudEvent;
import io.cloudevents.sql.EvaluationException;
import org.antlr.v4.runtime.misc.Interval;

public class NegateExpression extends BaseExpression {

    private final ExpressionInternal internal;

    public NegateExpression(Interval expressionInterval, String expressionText, ExpressionInternal internal) {
        super(expressionInterval, expressionText);
        this.internal = internal;
    }

    @Override
    public Object evaluate(EvaluationContextImpl ctx, CloudEvent event) throws EvaluationException {
        return -castToInteger(ctx, internal.evaluate(ctx, event));
    }

}
