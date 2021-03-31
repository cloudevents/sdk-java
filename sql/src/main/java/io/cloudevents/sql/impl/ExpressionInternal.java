package io.cloudevents.sql.impl;

import io.cloudevents.CloudEvent;
import io.cloudevents.sql.EvaluationRuntime;
import org.antlr.v4.runtime.misc.Interval;

public interface ExpressionInternal {

    Interval expressionInterval();

    String expressionText();

    Object evaluate(EvaluationRuntime runtime, CloudEvent event, EvaluationExceptions exceptions);

}
