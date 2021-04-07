package io.cloudevents.sql;

import java.util.Collection;

/**
 * Result of an expression evaluation.
 */
public interface Result {

    /**
     * @return the result of the expression evaluation, which could be a {@link String}, a {@link Integer} or a {@link Boolean}.
     */
    Object value();

    /**
     * @return true if the causes collection is not empty.
     */
    boolean isFailed();

    /**
     * @return the list of evaluation exceptions happened while evaluating the expression.
     */
    Collection<EvaluationException> causes();
}
