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

    ExceptionFactory exceptionFactory();
}
