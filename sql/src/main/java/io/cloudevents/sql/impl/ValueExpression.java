package io.cloudevents.sql.impl;

import io.cloudevents.CloudEvent;

public class ValueExpression implements ExpressionInternal {

    private final Object value;

    public ValueExpression(Object value) {
        this.value = value;
    }

    @Override
    public Object evaluate(EvaluationContext ctx, CloudEvent event) {
        return value;
    }
}
