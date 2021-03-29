package io.cloudevents.sql.impl;

import io.cloudevents.CloudEvent;
import io.cloudevents.sql.EvaluationException;
import org.antlr.v4.runtime.misc.Interval;

public interface ExpressionInternal {

    Interval expressionInterval();

    String expressionText();

    Object evaluate(EvaluationContextImpl ctx, CloudEvent event) throws EvaluationException;

}
