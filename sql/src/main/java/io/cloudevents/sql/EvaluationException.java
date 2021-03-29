package io.cloudevents.sql;

import org.antlr.v4.runtime.misc.Interval;

public class EvaluationException extends RuntimeException {

    public enum Kind {
        INVALID_CAST
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

    public static EvaluationException invalidCastTarget(Interval interval, String expression, Class<?> from, Class<?> to) {
        return new EvaluationException(
            Kind.INVALID_CAST,
            interval,
            expression,
            "Cannot cast " + from + " to " + to + ": no cast defined.",
            null
        );
    }

    public static EvaluationException castException(Interval interval, String expression, Class<?> from, Class<?> to, Throwable cause) {
        return new EvaluationException(
            Kind.INVALID_CAST,
            interval,
            expression,
            "Cannot cast " + from + " to " + to + ": " + cause.getMessage(),
            cause
        );
    }
}
