package io.cloudevents.sql.impl;

import io.cloudevents.CloudEvent;

public interface ExpressionInternal {

    Object evaluate(EvaluationContext ctx, CloudEvent event);

}
