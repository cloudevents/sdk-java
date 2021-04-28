package io.cloudevents.sql.impl;

import io.cloudevents.sql.EvaluationException;

public interface ExceptionThrower {

    /**
     * This method might block the execution or not, depending on its implementation
     *
     * @param exception the exception to throw
     */
    void throwException(EvaluationException exception);
}
