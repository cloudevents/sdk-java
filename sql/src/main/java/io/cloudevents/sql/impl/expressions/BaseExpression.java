package io.cloudevents.sql.impl.expressions;

import io.cloudevents.sql.ExceptionFactory;
import io.cloudevents.sql.Type;
import io.cloudevents.sql.impl.ExpressionInternal;
import io.cloudevents.sql.impl.runtime.EvaluationContextImpl;
import io.cloudevents.sql.impl.runtime.EvaluationResult;
import io.cloudevents.sql.impl.runtime.TypeCastingProvider;
import org.antlr.v4.runtime.misc.Interval;

public abstract class BaseExpression implements ExpressionInternal {

    private final String expressionText;
    private final Interval expressionInterval;

    protected BaseExpression(Interval expressionInterval, String expressionText) {
        this.expressionText = expressionText;
        this.expressionInterval = expressionInterval;
    }

    @Override
    public Interval expressionInterval() {
        return this.expressionInterval;
    }

    @Override
    public String expressionText() {
        return this.expressionText;
    }

    public EvaluationResult castToBoolean(ExceptionFactory exceptionFactory, EvaluationResult value) {
        return TypeCastingProvider.cast(
            new EvaluationContextImpl(expressionInterval(), expressionText(), exceptionFactory),
            value,
            Type.BOOLEAN
        );
    }

    public EvaluationResult castToInteger(ExceptionFactory exceptionFactory, EvaluationResult value) {
        return TypeCastingProvider.cast(
            new EvaluationContextImpl(expressionInterval(), expressionText(), exceptionFactory),
            value,
            Type.INTEGER
        );
    }

    public EvaluationResult castToString(ExceptionFactory exceptionFactory, EvaluationResult value) {
        return TypeCastingProvider.cast(
            new EvaluationContextImpl(expressionInterval(), expressionText(), exceptionFactory),
            value,
            Type.STRING
        );
    }

}
