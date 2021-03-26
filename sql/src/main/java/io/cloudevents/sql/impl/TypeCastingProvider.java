package io.cloudevents.sql.impl;

import io.cloudevents.sql.EvaluationException;
import io.cloudevents.sql.Type;

public class TypeCastingProvider {

    public Object cast(Object value, Type target) {
        if (target.valueClass().equals(value.getClass())) {
            return value;
        }
        throw new EvaluationException("Casting not implemented");
    }

}
