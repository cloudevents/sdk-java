package io.cloudevents.sql.impl;

import io.cloudevents.sql.EvaluationException;
import io.cloudevents.sql.Result;

import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.Objects;

public class EvaluationResult implements Result {

    private final Object value;
    private final List<EvaluationException> exceptions;

    public EvaluationResult(Object value, List<EvaluationException> exceptions) {
        this.value = value;
        this.exceptions = exceptions == null ? Collections.emptyList() : Collections.unmodifiableList(exceptions);
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
        return !exceptions.isEmpty();
    }

    /**
     * @return the causes of the failure, if {@link #isFailed()} returns {@code true}
     */
    @Override
    public Collection<EvaluationException> causes() {
        return exceptions != null ? exceptions : Collections.emptyList();
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        EvaluationResult that = (EvaluationResult) o;
        return Objects.equals(value, that.value) && Objects.equals(exceptions, that.exceptions);
    }

    @Override
    public int hashCode() {
        return Objects.hash(value, exceptions);
    }

    @Override
    public String toString() {
        return "EvaluationResult{" +
            "  value=" + value +
            "\n" +
            "  , exceptions=" + exceptions +
            "\n" +
            '}';
    }
}
