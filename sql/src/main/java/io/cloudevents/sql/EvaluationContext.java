package io.cloudevents.sql;

import org.antlr.v4.runtime.misc.Interval;

/**
 * This class wraps some elements of the evaluation context,
 * required to throw {@link EvaluationException} when an error occurs while evaluating a function.
 */
public interface EvaluationContext {

    /**
     * @return the interval of the original expression string.
     */
    Interval expressionInterval();

    /**
     * @return the text of the original expression string.
     */
    String expressionText();

    /**
     * Append a new exception to the evaluation context.
     * This exception will be propagated back in the evaluation result.
     *
     * @param exception exception to append
     */
    void appendException(EvaluationException exception);

    /**
     * Append a new exception to the evaluation context.
     * This exception will be propagated back in the evaluation result.
     *
     * @param exceptionFactory exception factory, which will automatically incude expression interval and text
     */
    void appendException(EvaluationException.EvaluationExceptionFactory exceptionFactory);
}
