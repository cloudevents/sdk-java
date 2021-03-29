package io.cloudevents.sql.impl;

import io.cloudevents.sql.EvaluationContext;
import io.cloudevents.sql.Type;
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

    public Boolean castToBoolean(EvaluationContext ctx, Object value) {
        return (Boolean) ctx.getRuntime().getTypeCastingProvider().cast(
            ctx,
            this.expressionInterval(),
            this.expressionText(),
            value,
            Type.BOOLEAN
        );
    }

    public Integer castToInteger(EvaluationContext ctx, Object value) {
        return (Integer) ctx.getRuntime().getTypeCastingProvider().cast(
            ctx,
            this.expressionInterval(),
            this.expressionText(),
            value,
            Type.INTEGER
        );
    }

    public String castToString(EvaluationContext ctx, Object value) {
        return (String) ctx.getRuntime().getTypeCastingProvider().cast(
            ctx,
            this.expressionInterval(),
            this.expressionText(),
            value,
            Type.STRING
        );
    }

}
