package io.cloudevents.sql.impl.runtime;

import io.cloudevents.sql.EvaluationContext;
import io.cloudevents.sql.EvaluationException;
import io.cloudevents.sql.ExceptionFactory;
import io.cloudevents.sql.impl.ExceptionFactoryImpl;
import org.antlr.v4.runtime.misc.Interval;

public class EvaluationContextImpl implements EvaluationContext {

    private final Interval expressionInterval;
    private final String expressionText;
    private final ExceptionFactory exceptionFactory;

    public EvaluationContextImpl(Interval expressionInterval, String expressionText, ExceptionFactory exceptionFactory) {
        this.expressionInterval = expressionInterval;
        this.expressionText = expressionText;
        this.exceptionFactory = exceptionFactory;
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
    public ExceptionFactory exceptionFactory() {
        return this.exceptionFactory;
    }
}
