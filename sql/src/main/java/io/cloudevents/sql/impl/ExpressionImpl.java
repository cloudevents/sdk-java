package io.cloudevents.sql.impl;

import io.cloudevents.CloudEvent;
import io.cloudevents.sql.Expression;
import io.cloudevents.sql.Result;
import io.cloudevents.sql.Runtime;

public class ExpressionImpl implements Expression {

    private final ExpressionInternal expressionInternal;

    public ExpressionImpl(ExpressionInternal expressionInternal) {
        this.expressionInternal = expressionInternal;
    }

    @Override
    public Result evaluate(Runtime runtime, CloudEvent event) {
        EvaluationContextImpl ctx = new EvaluationContextImpl(runtime);
        Object value = this.expressionInternal.evaluate(ctx, event);
        return new EvaluationResult(value, ctx.getEvaluationExceptions());
    }
}
