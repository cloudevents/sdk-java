package io.cloudevents.sql.impl.runtime;

import io.cloudevents.sql.EvaluationException;
import io.cloudevents.sql.Result;
import io.cloudevents.sql.Type;

import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class EvaluationResult implements Result {

    private final Object value;
    private final List<EvaluationException> exceptions;
    private final EvaluationException latestException;

    public EvaluationResult(Object value, List<EvaluationException> exceptions) {
        this.value = value;
        this.exceptions = exceptions == null ? new ArrayList<>() : exceptions;
        this.latestException = null;
    }

    public EvaluationResult(Object value, EvaluationException exception, EvaluationResult left, EvaluationResult right) {
        this.exceptions = Stream.concat(Stream.of(left, right).filter(Objects::nonNull).map(r -> r.exceptions).flatMap(Collection::stream), Stream.of(exception))
            .filter(Objects::nonNull)
            .collect(Collectors.toList());
        this.latestException = exception;
        this.value = value;
    }

    public EvaluationResult(Object value, EvaluationException exception) {
        this(value, exception, null, null);
    }

    public EvaluationResult(Object value) {
        this.value = value;
        this.exceptions = new ArrayList<>();
        this.latestException = null;
    }

    public EvaluationResult wrapExceptions(EvaluationResult other) {
        if (other != null && other.exceptions != null) {
            return this.wrapExceptions(other.exceptions);
        }
        return this;
    }

    public EvaluationResult wrapExceptions(List<EvaluationException> exceptions) {
        if (!exceptions.isEmpty()) {
            this.exceptions.addAll(exceptions);
        }
        return this;
    }

    public EvaluationResult copyWithValue(Object value) {
        return new EvaluationResult(value, this.exceptions);
    }

    public EvaluationResult copyWithDefaultValueForType(Type type) {
        Object value;
        switch (type) {
            case STRING:
                value = "";
                break;
            case INTEGER:
                value = 0;
                break;
            default:
                value = false;
                break;
        }
        return new EvaluationResult(value, this.exceptions);
    }

    // returns true is the most recent exception was a MISSING attribute exception
    public boolean isMissingAttributeException() {
        return (this.latestException != null && this.latestException.getKind() == EvaluationException.ErrorKind.MISSING_ATTRIBUTE);
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
