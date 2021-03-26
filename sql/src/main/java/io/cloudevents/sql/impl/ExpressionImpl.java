package io.cloudevents.sql.impl;

import io.cloudevents.CloudEvent;
import io.cloudevents.sql.Expression;
import io.cloudevents.sql.Result;

public class ExpressionImpl implements Expression {

    private final ExpressionInternal expressionInternal;

    public ExpressionImpl(ExpressionInternal expressionInternal) {
        this.expressionInternal = expressionInternal;
    }

    @Override
    public Result evaluate(CloudEvent event) {
        return new EvaluationResult(this.expressionInternal.evaluate(null, event)); //TODO
    }
}
