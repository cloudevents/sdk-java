package io.cloudevents.sql.impl;

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
}
