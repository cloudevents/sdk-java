package io.cloudevents.sql;

import org.antlr.v4.runtime.misc.Interval;

/**
 * This exception represents an evaluation exception when evaluating an {@link Expression}.
 * Using {@link #getKind()} you can inspect what kind of failure happened, implementing proper failure handling.
 */
public class EvaluationException extends RuntimeException {

    /**
     * Interface to simplify the construction of an {@link EvaluationException}.
     */
    @FunctionalInterface
    public interface EvaluationExceptionFactory {
        EvaluationException create(Interval interval, String expression);
    }

    public enum ErrorKind {
        /**
         * An implicit or an explicit casting failed.
         */
        CAST,
        /**
         * An event attribute was addressed, but missing.
         */
        MISSING_ATTRIBUTE,
        /**
         * Error happened while dispatching a function invocation. Reasons may be invalid function name or invalid arguments number.
         */
        MISSING_FUNCTION,
        /**
         * Error happened while executing a function. This usually contains a non null cause.
         */
        FUNCTION_EVALUATION,
        /**
         * Error happened while executing a math operation. Reason may be a division by zero.
         */
        MATH,
        /**
         * Any error that does not fall into the other error kinds
         */
        GENERIC,
    }

    private final ErrorKind errorKind;
    private final Interval interval;
    private final String expression;

    public EvaluationException(ErrorKind errorKind, Interval interval, String expression, String message, Throwable cause) {
        super(String.format("%s at %s `%s`: %s", errorKind.name(), interval.toString(), expression, message), cause);
        this.errorKind = errorKind;
        this.interval = interval;
        this.expression = expression;
    }

    public ErrorKind getKind() {
        return errorKind;
    }

    public Interval getExpressionInterval() {
        return interval;
    }

    public String getExpressionText() {
        return expression;
    }

}
