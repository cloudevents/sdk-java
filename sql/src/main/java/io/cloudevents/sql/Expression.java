package io.cloudevents.sql;

import io.cloudevents.CloudEvent;

public interface Expression {

    /**
     * Evaluate the expression
     *
     * @param event the input event
     * @return the evaluation result
     */
    Result evaluate(CloudEvent event);

}
