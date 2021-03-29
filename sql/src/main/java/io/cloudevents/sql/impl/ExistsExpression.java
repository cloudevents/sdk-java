package io.cloudevents.sql.impl;

import io.cloudevents.CloudEvent;
import io.cloudevents.sql.EvaluationException;
import org.antlr.v4.runtime.misc.Interval;

public class ExistsExpression extends BaseExpression {

    private final String key;

    public ExistsExpression(Interval expressionInterval, String expressionText, String key) {
        super(expressionInterval, expressionText);
        this.key = key;
    }

    @Override
    public Object evaluate(EvaluationContextImpl ctx, CloudEvent event) throws EvaluationException {
        return event.getAttributeNames().contains(key) || event.getExtensionNames().contains(key);
    }
}
