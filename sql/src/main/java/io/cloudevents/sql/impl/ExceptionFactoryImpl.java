package io.cloudevents.sql.impl;

import io.cloudevents.sql.EvaluationException;
import org.antlr.v4.runtime.misc.Interval;

/**
 * This class includes a list of static methods to create {@link io.cloudevents.sql.ParseException} and {@link io.cloudevents.sql.EvaluationException}.
 */
public class ExceptionFactoryImpl implements io.cloudevents.sql.ExceptionFactory {
    private final boolean shouldThrow;

    public ExceptionFactoryImpl(boolean shouldThrow) {
        this.shouldThrow = shouldThrow;
    }

    public EvaluationException.EvaluationExceptionFactory invalidCastTarget(Class<?> from, Class<?> to) {
        return (interval, expression) -> {
            final EvaluationException exception = new EvaluationException(
                EvaluationException.ErrorKind.CAST,
                interval,
                expression,
                "Cannot cast " + from + " to " + to + ": no cast defined.",
                null
            );

            if (this.shouldThrow) {
                throw exception;
            }
            return exception;
        };
    }

    public EvaluationException.EvaluationExceptionFactory castError(Class<?> from, Class<?> to, Throwable cause) {
        return (interval, expression) -> {
            final EvaluationException exception = new EvaluationException(
                EvaluationException.ErrorKind.CAST,
                interval,
                expression,
                "Cannot cast " + from + " to " + to + ": " + cause.getMessage(),
                cause
            );

            if (this.shouldThrow) {
                throw exception;
            }
            return exception;
        };
    }

    public EvaluationException missingAttribute(Interval interval, String expression, String key) {
        final EvaluationException exception = new EvaluationException(
            EvaluationException.ErrorKind.MISSING_ATTRIBUTE,
            interval,
            expression,
            "Missing attribute " + key + " in the input event. Perhaps you should check with 'EXISTS " + key + "' if the input contains the provided key?",
            null
        );

        if (this.shouldThrow) {
            throw exception;
        }
        return exception;
    }

    public EvaluationException cannotDispatchFunction(Interval interval, String expression, String functionName, Throwable cause) {
        final EvaluationException exception = new EvaluationException(
            EvaluationException.ErrorKind.MISSING_FUNCTION,
            interval,
            expression,
            "Cannot dispatch function invocation to function " + functionName + ": " + cause.getMessage(),
            cause
        );

        if (this.shouldThrow) {
            throw exception;
        }
        return exception;
    }

    public EvaluationException.EvaluationExceptionFactory functionExecutionError(String functionName, Throwable cause) {
        return (interval, expression) -> {
            final EvaluationException exception = new EvaluationException(
                EvaluationException.ErrorKind.FUNCTION_EVALUATION,
                interval,
                expression,
                "Error while executing " + functionName + ": " + cause.getMessage(),
                cause
            );

            if (this.shouldThrow) {
                throw exception;
            }
            return exception;
        };
    }

    public EvaluationException divisionByZero(Interval interval, String expression, Integer dividend) {
        return mathError(interval, expression, "Division by zero: " + dividend + " / 0");
    }

    @Override
    public EvaluationException mathError(Interval interval, String expression, String errorMessage) {
        final EvaluationException exception = new EvaluationException(
            EvaluationException.ErrorKind.MATH,
            interval,
            expression,
            errorMessage,
            null
        );

        if (this.shouldThrow) {
            throw exception;
        }
        return exception;
    }
}
