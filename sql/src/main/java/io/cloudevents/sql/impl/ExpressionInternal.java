package io.cloudevents.sql.impl;

import io.cloudevents.CloudEvent;
import org.antlr.v4.runtime.misc.Interval;

public interface ExpressionInternal {

    Interval expressionInterval();

    String expressionText();

    Object evaluate(EvaluationContextImpl ctx, CloudEvent event);

}
