package io.cloudevents.sql;

import io.cloudevents.CloudEvent;
import io.cloudevents.sql.impl.runtime.EvaluationResult;

import java.util.List;

/**
 * Function is a CloudEvents Expression Language function definition and implementation.
 */
public interface Function extends FunctionSignature {

    /**
     * Invoke the function logic.
     *
     * @param ctx               the evaluation context
     * @param evaluationRuntime the evaluation runtime
     * @param event             the expression input event
     * @param arguments         the arguments passed to this function. Note: the arguments are already cast to the appropriate type declared in the signature
     * @return the return value of the function
     */
    EvaluationResult invoke(EvaluationContext ctx, EvaluationRuntime evaluationRuntime, CloudEvent event, List<Object> arguments);

}
