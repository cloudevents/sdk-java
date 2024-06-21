package io.cloudevents.sql.impl.runtime;

import io.cloudevents.CloudEvent;
import io.cloudevents.sql.EvaluationException;
import io.cloudevents.sql.EvaluationRuntime;
import io.cloudevents.sql.Expression;
import io.cloudevents.sql.Result;
import io.cloudevents.sql.impl.ExceptionFactoryImpl;
import io.cloudevents.sql.impl.ExpressionInternal;

public class ExpressionImpl implements Expression {

    private final ExpressionInternal expressionInternal;

    public ExpressionImpl(ExpressionInternal expressionInternal) {
        this.expressionInternal = expressionInternal;
    }

    @Override
    public Result evaluate(EvaluationRuntime evaluationRuntime, CloudEvent event) {
        ExceptionFactoryImpl exceptionFactory = new ExceptionFactoryImpl(false);
        return this.expressionInternal.evaluate(evaluationRuntime, event, exceptionFactory);

    }

    @Override
    public Object tryEvaluate(EvaluationRuntime evaluationRuntime, CloudEvent event) throws EvaluationException {
        ExceptionFactoryImpl exceptionFactory = new ExceptionFactoryImpl(true);
        return this.expressionInternal.evaluate(evaluationRuntime, event, exceptionFactory).value();
    }

    public ExpressionInternal getExpressionInternal() {
        return expressionInternal;
    }
}
