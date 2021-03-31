package io.cloudevents.sql.impl;

import io.cloudevents.sql.EvaluationContext;
import io.cloudevents.sql.EvaluationException;
import org.antlr.v4.runtime.misc.Interval;

public class EvaluationContextImpl implements EvaluationContext {

    private final Interval expressionInterval;
    private final String expressionText;
    private final EvaluationExceptions evaluationExceptions;

    public EvaluationContextImpl(Interval expressionInterval, String expressionText, EvaluationExceptions evaluationExceptions) {
        this.expressionInterval = expressionInterval;
        this.expressionText = expressionText;
        this.evaluationExceptions = evaluationExceptions;
    }

    @Override
    public Interval expressionInterval() {
        return this.expressionInterval;
    }

    @Override
    public String expressionText() {
        return this.expressionText;
    }

    @Override
    public void appendException(EvaluationException exception) {
        this.evaluationExceptions.appendException(exception);
    }

    @Override
    public void appendException(EvaluationException.EvaluationExceptionFactory exceptionFactory) {
        this.evaluationExceptions.appendException(exceptionFactory.create(expressionInterval(), expressionText()));
    }
}
