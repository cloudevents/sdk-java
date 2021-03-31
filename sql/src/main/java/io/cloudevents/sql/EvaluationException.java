package io.cloudevents.sql;

import org.antlr.v4.runtime.misc.Interval;

public class EvaluationException extends RuntimeException {

    @FunctionalInterface
    public interface EvaluationExceptionFactory {
        EvaluationException create(Interval interval, String expression);
    }

    public enum Kind {
        INVALID_CAST,
        MISSING_ATTRIBUTE,
        FUNCTION_DISPATCH,
    }

    private final Kind kind;
    private final Interval interval;
    private final String expression;

    protected EvaluationException(Kind kind, Interval interval, String expression, String message, Throwable cause) {
        super(String.format("%s at %s `%s`: %s", kind.name(), interval.toString(), expression, message), cause);
        this.kind = kind;
        this.interval = interval;
        this.expression = expression;
    }

    public Kind getKind() {
        return kind;
    }

    public Interval getInterval() {
        return interval;
    }

    public String getExpression() {
        return expression;
    }

    public static EvaluationExceptionFactory invalidCastTarget(Class<?> from, Class<?> to) {
        return (interval, expression) -> new EvaluationException(
            Kind.INVALID_CAST,
            interval,
            expression,
            "Cannot cast " + from + " to " + to + ": no cast defined.",
            null
        );
    }

    public static EvaluationExceptionFactory castError(Class<?> from, Class<?> to, Throwable cause) {
        return (interval, expression) -> new EvaluationException(
            Kind.INVALID_CAST,
            interval,
            expression,
            "Cannot cast " + from + " to " + to + ": " + cause.getMessage(),
            cause
        );
    }

    public static EvaluationException missingAttribute(Interval interval, String expression, String key) {
        return new EvaluationException(
            Kind.MISSING_ATTRIBUTE,
            interval,
            expression,
            "Missing attribute " + key + " in the input event. Perhaps you should check with 'EXISTS " + key + "' if the input contains the provided key?",
            null
        );
    }

    public static EvaluationException cannotDispatchFunction(Interval interval, String expression, String functionName, Throwable cause) {
        return new EvaluationException(
            Kind.FUNCTION_DISPATCH,
            interval,
            expression,
            "Cannot dispatch function invocation to function " + functionName + ": " + cause.getMessage(),
            cause
        );
    }
}
