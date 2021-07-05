package io.cloudevents.sql.impl;

import io.cloudevents.sql.EvaluationException;
import io.cloudevents.sql.ParseException;
import io.cloudevents.sql.Type;
import org.antlr.v4.runtime.RecognitionException;
import org.antlr.v4.runtime.misc.Interval;
import org.antlr.v4.runtime.tree.ParseTree;

/**
 * This class includes a list of static methods to create {@link io.cloudevents.sql.ParseException} and {@link io.cloudevents.sql.EvaluationException}.
 */
public class ExceptionFactory {

    private ExceptionFactory() {
    }

    public static EvaluationException.EvaluationExceptionFactory invalidCastTarget(Class<?> from, Class<?> to) {
        return (interval, expression) -> new EvaluationException(
            EvaluationException.ErrorKind.INVALID_CAST,
            interval,
            expression,
            "Cannot cast " + from + " to " + to + ": no cast defined.",
            null
        );
    }

    public static EvaluationException.EvaluationExceptionFactory castError(Class<?> from, Class<?> to, Throwable cause) {
        return (interval, expression) -> new EvaluationException(
            EvaluationException.ErrorKind.INVALID_CAST,
            interval,
            expression,
            "Cannot cast " + from + " to " + to + ": " + cause.getMessage(),
            cause
        );
    }

    public static EvaluationException missingAttribute(Interval interval, String expression, String key) {
        return new EvaluationException(
            EvaluationException.ErrorKind.MISSING_ATTRIBUTE,
            interval,
            expression,
            "Missing attribute " + key + " in the input event. Perhaps you should check with 'EXISTS " + key + "' if the input contains the provided key?",
            null
        );
    }

    public static EvaluationException cannotDispatchFunction(Interval interval, String expression, String functionName, Throwable cause) {
        return new EvaluationException(
            EvaluationException.ErrorKind.FUNCTION_DISPATCH,
            interval,
            expression,
            "Cannot dispatch function invocation to function " + functionName + ": " + cause.getMessage(),
            cause
        );
    }

    public static EvaluationException.EvaluationExceptionFactory functionExecutionError(String functionName, Throwable cause) {
        return (interval, expression) -> new EvaluationException(
            EvaluationException.ErrorKind.FUNCTION_EXECUTION,
            interval,
            expression,
            "Error while executing " + functionName + ": " + cause.getMessage(),
            cause
        );
    }

    public static EvaluationException divisionByZero(Interval interval, String expression, Integer dividend) {
        return new EvaluationException(
            EvaluationException.ErrorKind.MATH,
            interval,
            expression,
            "Division by zero: " + dividend + " / 0",
            null
        );
    }

    public static ParseException cannotParseValue(ParseTree node, Type target, Throwable cause) {
        return new ParseException(
            ParseException.ErrorKind.PARSE_VALUE,
            node.getSourceInterval(),
            node.getText(),
            "Cannot parse to " + target.name() + ": " + cause.getMessage(),
            cause
        );
    }

    public static ParseException recognitionError(RecognitionException e, String msg) {
        return new ParseException(
            ParseException.ErrorKind.RECOGNITION,
            new Interval(e.getOffendingToken().getStartIndex(), e.getOffendingToken().getStopIndex()),
            e.getOffendingToken().getText(),
            "Cannot parse: " + msg,
            e
        );
    }

    public static ParseException cannotEvaluateConstantExpression(EvaluationException exception) {
        return new ParseException(
            ParseException.ErrorKind.CONSTANT_EXPRESSION_EVALUATION,
            exception.getExpressionInterval(),
            exception.getExpressionText(),
            "Cannot evaluate the constant expression: " + exception.getExpressionText(),
            exception
        );
    }
}
