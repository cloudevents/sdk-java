package io.cloudevents.sql;

import org.antlr.v4.runtime.RecognitionException;
import org.antlr.v4.runtime.misc.Interval;
import org.antlr.v4.runtime.tree.ParseTree;

public class ParseException extends RuntimeException {

    public enum ErrorKind {
        RECOGNITION_ERROR,
        PARSE_VALUE
    }

    private final ErrorKind errorKind;
    private final Interval interval;
    private final String expression;

    protected ParseException(ErrorKind errorKind, Interval interval, String expression, String message, Throwable cause) {
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

    public static ParseException cannotParseValue(ParseTree node, Type target, Throwable cause) {
        return new ParseException(
            ErrorKind.PARSE_VALUE,
            node.getSourceInterval(),
            node.getText(),
            "Cannot parse to " + target.name() + ": " + cause.getMessage(),
            cause
        );
    }

    public static ParseException recognitionError(RecognitionException e, String msg) {
        return new ParseException(
            ErrorKind.RECOGNITION_ERROR,
            new Interval(e.getOffendingToken().getStartIndex(), e.getOffendingToken().getStopIndex()),
            e.getOffendingToken().getText(),
            "Cannot parse: " + msg,
            e
        );
    }

}
