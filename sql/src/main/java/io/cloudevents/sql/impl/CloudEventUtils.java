package io.cloudevents.sql.impl;

import io.cloudevents.CloudEvent;
import io.cloudevents.sql.EvaluationContext;
import io.cloudevents.sql.EvaluationException;
import org.antlr.v4.runtime.misc.Interval;

import java.util.Base64;
import java.util.Objects;

public final class CloudEventUtils {

    private CloudEventUtils() {
    }

    static boolean hasContextAttribute(CloudEvent event, String key) {
        return event.getAttributeNames().contains(key) || event.getExtensionNames().contains(key);
    }

    static Object accessContextAttribute(EvaluationContext ctx, Interval interval, String expression, CloudEvent event, String key) {
        // TODO do we have a better solution to access attributes here?
        Object value;
        try {
            value = event.getAttribute(key);
        } catch (IllegalArgumentException e) {
            value = event.getExtension(key);
        }
        if (value == null) {
            ctx.appendException(
                EvaluationException.missingAttribute(interval, expression, key)
            );
            value = "";
        } else {
            // Because the CESQL type system is smaller than the CE type system,
            // we need to coherce some values to string
            value = coherceTypes(value);
        }

        return value;
    }

    static Object coherceTypes(Object value) {
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
