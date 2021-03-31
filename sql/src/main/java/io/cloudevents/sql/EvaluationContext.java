package io.cloudevents.sql;

import org.antlr.v4.runtime.misc.Interval;

public interface EvaluationContext {

    Interval expressionInterval();

    String expressionText();

    void appendException(EvaluationException exception);

    void appendException(EvaluationException.EvaluationExceptionFactory exceptionFactory);
}
