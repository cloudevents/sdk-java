package io.cloudevents.sql.impl.expressions;

import io.cloudevents.CloudEvent;
import io.cloudevents.sql.EvaluationRuntime;
import io.cloudevents.sql.impl.CloudEventUtils;
import io.cloudevents.sql.impl.EvaluationExceptions;
import org.antlr.v4.runtime.misc.Interval;

public class ExistsExpression extends BaseExpression {

    private final String key;

    public ExistsExpression(Interval expressionInterval, String expressionText, String key) {
        super(expressionInterval, expressionText);
        this.key = key;
    }

    @Override
    public Object evaluate(EvaluationRuntime runtime, CloudEvent event, EvaluationExceptions exceptions) {
        return CloudEventUtils.hasContextAttribute(event, key);
    }
}
