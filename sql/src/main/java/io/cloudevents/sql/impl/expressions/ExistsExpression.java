package io.cloudevents.sql.impl.expressions;

import io.cloudevents.CloudEvent;
import io.cloudevents.sql.EvaluationRuntime;
import io.cloudevents.sql.ExceptionFactory;
import io.cloudevents.sql.impl.ExceptionFactoryImpl;
import io.cloudevents.sql.impl.ExpressionInternalVisitor;
import io.cloudevents.sql.impl.runtime.EvaluationResult;
import org.antlr.v4.runtime.misc.Interval;

public class ExistsExpression extends BaseExpression {

    private final String key;

    public ExistsExpression(Interval expressionInterval, String expressionText, String key) {
        super(expressionInterval, expressionText);
        this.key = key.toLowerCase();
    }

    @Override
    public EvaluationResult evaluate(EvaluationRuntime runtime, CloudEvent event, ExceptionFactory exceptionFactory) {
        return new EvaluationResult(hasContextAttribute(event, key));
    }

    @Override
    public <T> T visit(ExpressionInternalVisitor<T> visitor) {
        return visitor.visitExistsExpression(this);
    }

    public String getKey() {
        return key;
    }

    private static boolean hasContextAttribute(CloudEvent event, String key) {
        return event.getAttributeNames().contains(key) || event.getExtensionNames().contains(key);
    }

}
