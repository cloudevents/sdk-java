package io.cloudevents.sql;

import io.cloudevents.CloudEvent;

import java.util.List;

public interface Function extends FunctionSignature {

    Object invoke(EvaluationContext ctx, EvaluationRuntime evaluationRuntime, CloudEvent event, List<Object> arguments);

}
