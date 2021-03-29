package io.cloudevents.sql;

import org.antlr.v4.runtime.RecognitionException;
import org.antlr.v4.runtime.misc.Interval;
import org.antlr.v4.runtime.tree.ParseTree;

public class ParseException extends RuntimeException {

    public enum Kind {
        RECOGNITION_ERROR,
        PARSE_VALUE
    }

    private final Kind kind;
    private final Interval interval;
    private final String expression;

    protected ParseException(Kind kind, Interval interval, String expression, String message, Throwable cause) {
        super(String.format("[%s at %d:%d `%s`] %s", kind.name(), interval.a, interval.b, expression, message), cause);
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

    public static ParseException cannotParseValue(ParseTree node, Type target, Throwable cause) {
        return new ParseException(
            Kind.PARSE_VALUE,
            node.getSourceInterval(),
            node.getText(),
            "Cannot parse to " + target.name() + ": " + cause.getMessage(),
            cause
        );
    }

    public static ParseException recognitionError(RecognitionException e, String msg) {
        return new ParseException(
            Kind.RECOGNITION_ERROR,
            new Interval(e.getOffendingToken().getStartIndex(), e.getOffendingToken().getStopIndex()),
            e.getOffendingToken().getText(),
            "Cannot parse: " + msg,
            e
        );
    }

}
