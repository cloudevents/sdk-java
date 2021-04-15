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
        INVALID_CAST,
        /**
         * An event attribute was addressed, but missing.
         */
        MISSING_ATTRIBUTE,
        /**
         * Error happened while dispatching a function invocation. Reasons may be invalid function name or invalid arguments number.
         */
        FUNCTION_DISPATCH,
        /**
         * Error happened while executing a function. This usually contains a non null cause.
         */
        FUNCTION_EXECUTION,
        /**
         * Error happened while executing a math operation. Reason may be a division by zero.
         */
        MATH
    }

    private final ErrorKind errorKind;
    private final Interval interval;
    private final String expression;

    protected EvaluationException(ErrorKind errorKind, Interval interval, String expression, String message, Throwable cause) {
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

    public static EvaluationExceptionFactory invalidCastTarget(Class<?> from, Class<?> to) {
        return (interval, expression) -> new EvaluationException(
            ErrorKind.INVALID_CAST,
            interval,
            expression,
            "Cannot cast " + from + " to " + to + ": no cast defined.",
            null
        );
    }

    public static EvaluationExceptionFactory castError(Class<?> from, Class<?> to, Throwable cause) {
        return (interval, expression) -> new EvaluationException(
            ErrorKind.INVALID_CAST,
            interval,
            expression,
            "Cannot cast " + from + " to " + to + ": " + cause.getMessage(),
            cause
        );
    }

    public static EvaluationException missingAttribute(Interval interval, String expression, String key) {
        return new EvaluationException(
            ErrorKind.MISSING_ATTRIBUTE,
            interval,
            expression,
            "Missing attribute " + key + " in the input event. Perhaps you should check with 'EXISTS " + key + "' if the input contains the provided key?",
            null
        );
    }

    public static EvaluationException cannotDispatchFunction(Interval interval, String expression, String functionName, Throwable cause) {
        return new EvaluationException(
            ErrorKind.FUNCTION_DISPATCH,
            interval,
            expression,
            "Cannot dispatch function invocation to function " + functionName + ": " + cause.getMessage(),
            cause
        );
    }

    public static EvaluationExceptionFactory functionExecutionError(String functionName, Throwable cause) {
        return (interval, expression) -> new EvaluationException(
            ErrorKind.FUNCTION_EXECUTION,
            interval,
            expression,
            "Error while executing " + functionName + ": " + cause.getMessage(),
            cause
        );
    }

    public static EvaluationException divisionByZero(Interval interval, String expression, Integer dividend) {
        return new EvaluationException(
            ErrorKind.MATH,
            interval,
            expression,
            "Division by zero: " + dividend + " / 0",
            null
        );
    }
}
