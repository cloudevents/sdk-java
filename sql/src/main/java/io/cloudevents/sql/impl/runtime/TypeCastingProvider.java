package io.cloudevents.sql.impl.runtime;

import io.cloudevents.sql.EvaluationContext;
import io.cloudevents.sql.EvaluationException;
import io.cloudevents.sql.Type;

import java.util.Objects;

public class TypeCastingProvider {

    boolean canCast(Object value, Type target) {
        if (target.valueClass().equals(value.getClass())) {
            return true;
        }
        switch (target) {
            case INTEGER:
                if (value instanceof String) {
                    try {
                        Integer.parseInt((String) value);
                        return true;
                    } catch (NumberFormatException e) {
                        return false;
                    }
                }
                return false;
            case BOOLEAN:
                if (value instanceof String) {
                    try {
                        parseBool((String) value);
                        return true;
                    } catch (IllegalArgumentException e) {
                        return false;
                    }
                }
                return false;
        }
        return true;
    }

    Object cast(EvaluationContext ctx, Object value, Type target) {
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
                            EvaluationException.castError(String.class, Integer.class, e)
                        );
                    }
                } else {
                    ctx.appendException(
                        EvaluationException.invalidCastTarget(value.getClass(), target.valueClass())
                    );
                }
                return 0;
            case BOOLEAN:
                if (value instanceof String) {
                    try {
                        return parseBool((String) value);
                    } catch (IllegalArgumentException e) {
                        ctx.appendException(
                            EvaluationException.castError(String.class, Boolean.class, e)
                        );
                    }
                } else {
                    ctx.appendException(
                        EvaluationException.invalidCastTarget(value.getClass(), target.valueClass())
                    );
                }
                return false;
        }
        // This should never happen
        throw new IllegalArgumentException("target type doesn't correspond to a known type");
    }

    private boolean parseBool(String val) {
        switch (val.toLowerCase()) {
            case "true":
                return true;
            case "false":
                return false;
            default:
                throw new IllegalArgumentException("Cannot cast '" + val + "' to boolean. Allowed values: ['true', 'false']");
        }
    }

}
