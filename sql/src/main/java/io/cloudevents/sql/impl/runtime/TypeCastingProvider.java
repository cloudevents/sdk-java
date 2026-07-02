package io.cloudevents.sql.impl.runtime;

import io.cloudevents.sql.EvaluationContext;
import io.cloudevents.sql.Type;

import java.util.Objects;

public class TypeCastingProvider {

    public static boolean canCast(Object value, Type target) {
        if (target.valueClass().equals(value.getClass())) {
            return true;
        }
        switch (target) {
            case INTEGER:
                if (value instanceof String string) {
                    try {
                        Integer.parseInt(string);
                        return true;
                    } catch (NumberFormatException e) {
                        return false;
                    }
                }
                return value instanceof Boolean;
            case BOOLEAN:
                if (value instanceof String string) {
                    try {
                        parseBool(string);
                        return true;
                    } catch (IllegalArgumentException e) {
                        return false;
                    }
                } else return value instanceof Integer;
        }
        return true;
    }

    public static EvaluationResult cast(EvaluationContext ctx, EvaluationResult result, Type target) {
        Objects.requireNonNull(result);
        Objects.requireNonNull(result.value());
        if (target.valueClass().equals(result.value().getClass())) {
            return result;
        }
        switch (target) {
            case ANY:
                return result;
            case STRING:
                return result.copyWithValue(Objects.toString(result.value()));
            case INTEGER:
                if (result.value() instanceof String) {
                    try {
                        return result.copyWithValue(Integer.parseInt((String) result.value()));
                    } catch (NumberFormatException e) {
                        return new EvaluationResult(0, ctx.exceptionFactory().castError(String.class, Integer.class, e).create(ctx.expressionInterval(), ctx.expressionText()));
                    }
                } else if (result.value() instanceof Boolean) {
                    if ((Boolean) result.value()) {
                        return result.copyWithValue(1);
                    }
                    return result.copyWithValue(0);
                } else {
                    return new EvaluationResult(0, ctx.exceptionFactory().invalidCastTarget(result.getClass(), target.valueClass()).create(ctx.expressionInterval(), ctx.expressionText()));
                }
            case BOOLEAN:
                if (result.value() instanceof String) {
                    try {
                        return result.copyWithValue(parseBool((String) result.value()));
                    } catch (IllegalArgumentException e) {
                        return new EvaluationResult(false, ctx.exceptionFactory().castError(String.class, Boolean.class, e).create(ctx.expressionInterval(), ctx.expressionText()));
                    }
                } else if (result.value() instanceof Integer) {
                    return result.copyWithValue(((Integer) result.value()) != 0);
                } else {
                    return new EvaluationResult(false, ctx.exceptionFactory().invalidCastTarget(result.getClass(), target.getClass()).create(ctx.expressionInterval(), ctx.expressionText()));
                }
        }
        // This should never happen
        throw new IllegalArgumentException("target type doesn't correspond to a known type");
    }

    private static boolean parseBool(String val) {
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
