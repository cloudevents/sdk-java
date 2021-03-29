package io.cloudevents.sql.impl;

import io.cloudevents.CloudEvent;
import io.cloudevents.sql.EvaluationException;
import org.antlr.v4.runtime.misc.Interval;

public class AccessAttributeExpression extends BaseExpression {

    private final String key;

    public AccessAttributeExpression(Interval expressionInterval, String expressionText, String key) {
        super(expressionInterval, expressionText);
        this.key = key;
    }

    @Override
    public Object evaluate(EvaluationContextImpl ctx, CloudEvent event) throws EvaluationException {
        return CloudEventUtils.accessContextAttribute(ctx, expressionInterval(), expressionText(), event, key);
    }

}
