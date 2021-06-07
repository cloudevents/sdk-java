package io.cloudevents.sql.impl.expressions;

import io.cloudevents.CloudEvent;
import io.cloudevents.SpecVersion;
import io.cloudevents.sql.EvaluationException;
import io.cloudevents.sql.EvaluationRuntime;
import io.cloudevents.sql.impl.ExceptionThrower;
import org.antlr.v4.runtime.misc.Interval;

import java.util.Base64;
import java.util.Objects;
import java.util.function.Function;

public class AccessAttributeExpression extends BaseExpression {

    private final String key;
    private final Function<CloudEvent, Object> getter;

    public AccessAttributeExpression(Interval expressionInterval, String expressionText, String key) {
        super(expressionInterval, expressionText);
        this.key = key;
        this.getter = generateGetter(key);
    }

    @Override
    public Object evaluate(EvaluationRuntime runtime, CloudEvent event, ExceptionThrower thrower) {
        Object value = this.getter.apply(event);
        if (value == null) {
            thrower.throwException(
                EvaluationException.missingAttribute(this.expressionInterval(), this.expressionText(), key)
            );
            return "";
        }

        // Because the CESQL type system is smaller than the CE type system,
        // we need to coherce some values to string
        return coherceTypes(value);
    }

    @Override
    public <T> T visit(ExpressionInternalVisitor<T> visitor) {
        return visitor.visitAccessAttributeExpression(this);
    }

    private static Function<CloudEvent, Object> generateGetter(String key) {
        return SpecVersion.V1.getAllAttributes().contains(key) ? ce -> ce.getAttribute(key) : ce -> ce.getExtension(key);
    }

    private static Object coherceTypes(Object value) {
        if (value instanceof Boolean || value instanceof String || value instanceof Integer) {
            // No casting required
            return value;
        }
        if (value instanceof byte[]) {
            return Base64.getEncoder().encodeToString((byte[]) value);
        }
        return Objects.toString(value);
    }

}
