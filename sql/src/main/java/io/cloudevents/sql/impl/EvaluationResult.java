package io.cloudevents.sql.impl;

import io.cloudevents.sql.EvaluationException;
import io.cloudevents.sql.Result;

import java.util.Objects;

public class EvaluationResult implements Result {

    private final Object value;
    private final EvaluationException exception;

    public EvaluationResult(Object value) {
        this(value, null);
    }

    public EvaluationResult(Object value, EvaluationException exception) {
        this.value = value;
        this.exception = exception;
    }

    /**
     * @return the raw result of the evaluation
     */
    @Override
    public Object value() {
        return value;
    }

    /**
     * @return true if the evaluation failed, false otherwise
     */
    @Override
    public boolean isFailed() {
        return exception != null;
    }

    /**
     * @return the cause of the failure, if {@link #isFailed()} returns {@code true}
     */
    @Override
    public EvaluationException cause() {
        return exception;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        EvaluationResult that = (EvaluationResult) o;
        return Objects.equals(value, that.value) && Objects.equals(exception, that.exception);
    }

    @Override
    public int hashCode() {
        return Objects.hash(value, exception);
    }

    @Override
    public String toString() {
        return "EvaluationResult{" +
            "  value=" + value +
            "\n" +
            "  , exception=" + exception +
            "\n" +
            '}';
    }
}
