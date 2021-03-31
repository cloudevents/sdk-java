package io.cloudevents.sql;

import org.antlr.v4.runtime.misc.Interval;

public class EvaluationException extends RuntimeException {

    @FunctionalInterface
    public interface EvaluationExceptionFactory {
        EvaluationException create(Interval interval, String expression);
    }

    public enum ErrorKind {
        INVALID_CAST,
        MISSING_ATTRIBUTE,
        FUNCTION_DISPATCH,
        FUNCTION_EXECUTION,
        DIVISION_BY_ZERO
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

    public Interval getInterval() {
        return interval;
    }

    public String getExpression() {
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
            ErrorKind.DIVISION_BY_ZERO,
            interval,
            expression,
            "Division by zero: " + dividend + " / 0",
            null
        );
    }
}
