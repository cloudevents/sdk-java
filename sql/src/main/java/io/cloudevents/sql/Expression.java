package io.cloudevents.sql;

import io.cloudevents.CloudEvent;
import io.cloudevents.sql.impl.RuntimeImpl;

public interface Expression {

    /**
     * Evaluate the expression
     *
     * @param event the input event
     * @return the evaluation result
     */
    Result evaluate(Runtime runtime, CloudEvent event);

    default Result evaluate(CloudEvent event) {
        return evaluate(RuntimeImpl.getInstance(), event);
    }

}
