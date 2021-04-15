package io.cloudevents.sql;

import io.cloudevents.CloudEvent;
import io.cloudevents.sql.impl.EvaluationRuntimeImpl;

/**
 * This class represents a parsed expression, ready to be executed.
 *
 * <p>
 * <p>
 * You can execute the Expression in one of the two modes, depending on your use case:
 *
 * <ul>
 *    <li>Use {@link #evaluate(EvaluationRuntime, CloudEvent)} to evaluate the expression without interrupting on the first error. The returned {@link Result} will contain a non-null evaluation result and, eventually, one or more failures</li>
 *    <li>Use {@link #tryEvaluate(EvaluationRuntime, CloudEvent)} to evaluate the expression, failing as soon as an error happens. This function either returns the evaluation result, or throws an exception with the first evaluation error.</li>
 * </ul>
 * <p>
 * The former approach gives more flexibility and allows to implement proper error handling,
 * while the latter is generally faster, because as soon as a failure happens the execution is interrupted.
 *
 * <p>
 * <p>
 * The execution of an {@link Expression} is thread safe, in the sense that no state is shared with other {@link Expression} and it doesn't mutate the state of {@link EvaluationRuntime}.
 */
public interface Expression {

    /**
     * Evaluate the expression
     *
     * @param evaluationRuntime the runtime instance to use to run the evaluation
     * @param event             the input event
     * @return the evaluation result, encapsulated in a {@link Result} type
     */
    Result evaluate(EvaluationRuntime evaluationRuntime, CloudEvent event);

    /**
     * Evaluate the expression, but throw an {@link EvaluationException} as soon as the evaluation fails
     *
     * @param evaluationRuntime the runtime instance to use to run the evaluation
     * @param event             the input event
     * @return the evaluation result
     * @throws EvaluationException the first evaluation failure that happened
     */
    Object tryEvaluate(EvaluationRuntime evaluationRuntime, CloudEvent event) throws EvaluationException;

    /**
     * Like {@link #evaluate(EvaluationRuntime, CloudEvent)}, but using the default runtime instance
     */
    default Result evaluate(CloudEvent event) {
        return evaluate(EvaluationRuntimeImpl.getInstance(), event);
    }

    /**
     * Like {@link #tryEvaluate(EvaluationRuntime, CloudEvent)}, but using the default runtime instance
     */
    default Object tryEvaluate(CloudEvent event) throws EvaluationException {
        return tryEvaluate(EvaluationRuntimeImpl.getInstance(), event);
    }

}
