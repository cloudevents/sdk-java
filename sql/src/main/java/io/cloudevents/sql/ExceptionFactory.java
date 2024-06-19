package io.cloudevents.sql;

import org.antlr.v4.runtime.RecognitionException;
import org.antlr.v4.runtime.misc.Interval;
import org.antlr.v4.runtime.tree.ParseTree;

public interface ExceptionFactory {
    EvaluationException.EvaluationExceptionFactory invalidCastTarget(Class<?> from, Class<?> to);

    EvaluationException.EvaluationExceptionFactory castError(Class<?> from, Class<?> to, Throwable cause);

    EvaluationException missingAttribute(Interval interval, String expression, String key);

    EvaluationException cannotDispatchFunction(Interval interval, String expression, String functionName, Throwable cause);

    EvaluationException.EvaluationExceptionFactory functionExecutionError(String functionName, Throwable cause);

    EvaluationException divisionByZero(Interval interval, String expression, Integer dividend);

    EvaluationException mathError(Interval interval, String expression, String errorMessage);

    static ParseException cannotParseValue(ParseTree node, Type target, Throwable cause) {
        return new ParseException(
            ParseException.ErrorKind.PARSE_VALUE,
            node.getSourceInterval(),
            node.getText(),
            "Cannot parse to " + target.name() + ": " + cause.getMessage(),
            cause
        );
    }

    static ParseException recognitionError(RecognitionException e, String msg) {
        return new ParseException(
            ParseException.ErrorKind.RECOGNITION,
            new Interval(e.getOffendingToken().getStartIndex(), e.getOffendingToken().getStopIndex()),
            e.getOffendingToken().getText(),
            "Cannot parse: " + msg,
            e
        );
    }

    static ParseException cannotEvaluateConstantExpression(EvaluationException exception) {
        return new ParseException(
            ParseException.ErrorKind.CONSTANT_EXPRESSION_EVALUATION,
            exception.getExpressionInterval(),
            exception.getExpressionText(),
            "Cannot evaluate the constant expression: " + exception.getExpressionText(),
            exception
        );
    }
}
