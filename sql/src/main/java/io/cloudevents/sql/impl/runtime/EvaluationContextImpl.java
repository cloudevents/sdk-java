package io.cloudevents.sql.impl.runtime;

import io.cloudevents.sql.EvaluationContext;
import io.cloudevents.sql.EvaluationException;
import io.cloudevents.sql.impl.ExceptionThrower;
import org.antlr.v4.runtime.misc.Interval;

public class EvaluationContextImpl implements EvaluationContext {

    private final Interval expressionInterval;
    private final String expressionText;
    private final ExceptionThrower exceptionThrower;

    public EvaluationContextImpl(Interval expressionInterval, String expressionText, ExceptionThrower exceptionThrower) {
        this.expressionInterval = expressionInterval;
        this.expressionText = expressionText;
        this.exceptionThrower = exceptionThrower;
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
        this.exceptionThrower.throwException(exception);
    }

    @Override
    public void appendException(EvaluationException.EvaluationExceptionFactory exceptionFactory) {
        this.exceptionThrower.throwException(exceptionFactory.create(expressionInterval(), expressionText()));
    }
}
