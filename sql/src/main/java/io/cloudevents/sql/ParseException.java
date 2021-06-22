package io.cloudevents.sql;

import org.antlr.v4.runtime.misc.Interval;

/**
 * This exception represents an error occurred during parsing.
 */
public class ParseException extends RuntimeException {

    public enum ErrorKind {
        /**
         * Error when parsing the expression string
         */
        RECOGNITION,
        /**
         * Error when parsing a literal
         */
        PARSE_VALUE,
        /**
         * Error when executing the constant parts of the expression
         */
        CONSTANT_EXPRESSION_EVALUATION,
    }

    private final ErrorKind errorKind;
    private final Interval interval;
    private final String expression;

    public ParseException(ErrorKind errorKind, Interval interval, String expression, String message, Throwable cause) {
        super(String.format("[%s at %d:%d `%s`] %s", errorKind.name(), interval.a, interval.b, expression, message), cause);
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

}
