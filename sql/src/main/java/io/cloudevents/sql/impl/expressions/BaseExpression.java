package io.cloudevents.sql.impl.expressions;

import io.cloudevents.sql.EvaluationRuntime;
import io.cloudevents.sql.Type;
import io.cloudevents.sql.impl.EvaluationContextImpl;
import io.cloudevents.sql.impl.ExceptionThrower;
import io.cloudevents.sql.impl.ExpressionInternal;
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

    public Boolean castToBoolean(EvaluationRuntime runtime, ExceptionThrower exceptions, Object value) {
        return (Boolean) runtime.cast(
            new EvaluationContextImpl(expressionInterval(), expressionText(), exceptions),
            value,
            Type.BOOLEAN
        );
    }

    public Integer castToInteger(EvaluationRuntime runtime, ExceptionThrower exceptions, Object value) {
        return (Integer) runtime.cast(
            new EvaluationContextImpl(expressionInterval(), expressionText(), exceptions),
            value,
            Type.INTEGER
        );
    }

    public String castToString(EvaluationRuntime runtime, ExceptionThrower exceptions, Object value) {
        return (String) runtime.cast(
            new EvaluationContextImpl(expressionInterval(), expressionText(), exceptions),
            value,
            Type.STRING
        );
    }

}
