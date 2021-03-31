package io.cloudevents.sql.impl.expressions;

import io.cloudevents.CloudEvent;
import io.cloudevents.sql.EvaluationRuntime;
import io.cloudevents.sql.impl.CloudEventUtils;
import io.cloudevents.sql.impl.EvaluationExceptions;
import org.antlr.v4.runtime.misc.Interval;

public class AccessAttributeExpression extends BaseExpression {

    private final String key;

    public AccessAttributeExpression(Interval expressionInterval, String expressionText, String key) {
        super(expressionInterval, expressionText);
        this.key = key;
    }

    @Override
    public Object evaluate(EvaluationRuntime runtime, CloudEvent event, EvaluationExceptions exceptions) {
        return CloudEventUtils.accessContextAttribute(exceptions, expressionInterval(), expressionText(), event, key);
    }

}
