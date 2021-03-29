package io.cloudevents.sql.impl;

import io.cloudevents.sql.EvaluationContext;
import io.cloudevents.sql.EvaluationException;
import io.cloudevents.sql.Type;
import io.cloudevents.sql.TypeCastingProvider;
import org.antlr.v4.runtime.misc.Interval;

import java.util.Objects;

public class TypeCastingProviderImpl implements TypeCastingProvider {

    @Override
    public Object cast(EvaluationContext ctx, Interval interval, String expression, Object value, Type target) {
        Objects.requireNonNull(value);
        if (target.valueClass().equals(value.getClass())) {
            return value;
        }
        switch (target) {
            case ANY:
                return value;
            case STRING:
                return Objects.toString(value);
            case INTEGER:
                if (value instanceof String) {
                    try {
                        return Integer.parseInt((String) value);
                    } catch (NumberFormatException e) {
                        ctx.appendException(
                            EvaluationException.castException(interval, expression, String.class, Integer.class, e)
                        );
                    }
                } else {
                    ctx.appendException(
                        EvaluationException.invalidCastTarget(interval, expression, value.getClass(), target.valueClass())
                    );
                }
                return 0;
            case BOOLEAN:
                if (value instanceof String) {
                    return Boolean.parseBoolean((String) value);
                } else {
                    ctx.appendException(
                        EvaluationException.invalidCastTarget(interval, expression, value.getClass(), target.valueClass())
                    );
                }
        }
        // This should never happen
        throw new IllegalArgumentException("target type doesn't correspond to a known type");
    }

}
