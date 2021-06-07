package io.cloudevents.sql.impl.runtime;

import io.cloudevents.sql.EvaluationContext;
import io.cloudevents.sql.EvaluationException;
import io.cloudevents.sql.impl.ExceptionThrower;
import org.antlr.v4.runtime.misc.Interval;

public class FailFastExceptionThrower implements ExceptionThrower, EvaluationContext {

    private static class SingletonContainer {
        private final static FailFastExceptionThrower INSTANCE = new FailFastExceptionThrower();
    }

    public static FailFastExceptionThrower getInstance() {
        return FailFastExceptionThrower.SingletonContainer.INSTANCE;
    }

    @Override
    public void throwException(EvaluationException exception) {
        throw exception;
    }

    @Override
    public Interval expressionInterval() {
        return Interval.INVALID;
    }

    @Override
    public String expressionText() {
        return "";
    }

    @Override
    public void appendException(EvaluationException exception) {
        throwException(exception);
    }

    @Override
    public void appendException(EvaluationException.EvaluationExceptionFactory exceptionFactory) {
        throwException(exceptionFactory.create(expressionInterval(), expressionText()));
    }
}
