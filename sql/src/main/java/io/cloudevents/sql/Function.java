package io.cloudevents.sql;

import io.cloudevents.CloudEvent;

public interface Function {

    FunctionSignature getSignature();

    Object invoke(CloudEvent event) throws EvaluationException;

}
