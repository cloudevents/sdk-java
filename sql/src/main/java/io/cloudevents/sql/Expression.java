package io.cloudevents.sql;

import io.cloudevents.CloudEvent;
import io.cloudevents.sql.impl.EvaluationRuntimeImpl;

public interface Expression {

    /**
     * Evaluate the expression
     *
     * @param event the input event
     * @return the evaluation result
     */
    Result evaluate(EvaluationRuntime evaluationRuntime, CloudEvent event);

    /**
     * Evaluate the expression using the default runtime
     *
     * @param event the input event
     * @return the evaluation result
     */
    default Result evaluate(CloudEvent event) {
        return evaluate(EvaluationRuntimeImpl.getInstance(), event);
    }

}
