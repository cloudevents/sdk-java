package io.cloudevents.sql.impl;

import io.cloudevents.CloudEvent;
import io.cloudevents.sql.EvaluationException;
import io.cloudevents.sql.EvaluationRuntime;
import io.cloudevents.sql.Expression;
import io.cloudevents.sql.Result;

public class ExpressionImpl implements Expression {

    private final ExpressionInternal expressionInternal;

    public ExpressionImpl(ExpressionInternal expressionInternal) {
        this.expressionInternal = expressionInternal;
    }

    @Override
    public Result evaluate(EvaluationRuntime evaluationRuntime, CloudEvent event) {
        ExceptionsStore exceptions = new ExceptionsStore();
        Object value = this.expressionInternal.evaluate(evaluationRuntime, event, exceptions);
        return new EvaluationResult(value, exceptions.getExceptions());
    }

    @Override
    public Object tryEvaluate(EvaluationRuntime evaluationRuntime, CloudEvent event) throws EvaluationException {
        return this.expressionInternal.evaluate(evaluationRuntime, event, FailFastExceptionThrower.getInstance());
    }
}
