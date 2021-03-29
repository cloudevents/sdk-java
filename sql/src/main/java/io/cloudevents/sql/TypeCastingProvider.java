package io.cloudevents.sql;

import org.antlr.v4.runtime.misc.Interval;

public interface TypeCastingProvider {

    Object cast(EvaluationContext ctx, Interval interval, String expression, Object value, Type target);

}
