package io.cloudevents.sql;

import org.antlr.v4.runtime.misc.Interval;

public interface EvaluationContext {

    Runtime getRuntime();

    Interval expressionInterval();

    String expressionText();

    void appendException(EvaluationException exception);
}
