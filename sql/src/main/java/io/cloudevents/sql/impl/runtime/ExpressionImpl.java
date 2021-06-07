package io.cloudevents.sql.impl.runtime;

import io.cloudevents.CloudEvent;
import io.cloudevents.sql.EvaluationException;
import io.cloudevents.sql.EvaluationRuntime;
import io.cloudevents.sql.Expression;
import io.cloudevents.sql.Result;
import io.cloudevents.sql.impl.ExpressionInternal;

public class ExpressionImpl implements Expression {

    private final ExpressionInternal expressionInternal;

    public ExpressionImpl(ExpressionInternal expressionInternal) {
        this.expressionInternal = expressionInternal;
    }

    @Override
    public Result evaluate(EvaluationRuntime evaluationRuntime, CloudEvent event) {
        ExceptionStore exceptions = new ExceptionStore();
        Object value = this.expressionInternal.evaluate(evaluationRuntime, event, exceptions);
        return new EvaluationResult(value, exceptions.getExceptions());
    }

    @Override
    public Object tryEvaluate(EvaluationRuntime evaluationRuntime, CloudEvent event) throws EvaluationException {
        return this.expressionInternal.evaluate(evaluationRuntime, event, FailFastExceptionThrower.getInstance());
    }

    public ExpressionInternal getExpressionInternal() {
        return expressionInternal;
    }
}
