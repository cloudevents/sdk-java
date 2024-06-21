package io.cloudevents.sql.impl.functions;

import io.cloudevents.CloudEvent;
import io.cloudevents.sql.EvaluationContext;
import io.cloudevents.sql.EvaluationRuntime;
import io.cloudevents.sql.Type;
import io.cloudevents.sql.impl.runtime.EvaluationResult;
import io.cloudevents.sql.impl.runtime.TypeCastingProvider;

public class IntFunction extends BaseOneArgumentFunction<Object, Integer> {

    public IntFunction() {
        super("INT", Object.class, Integer.class);
    }

    @Override
    public EvaluationResult invoke(EvaluationContext ctx, EvaluationRuntime evaluationRuntime, CloudEvent event, Object argument) {
        return TypeCastingProvider.cast(ctx, new EvaluationResult(argument), Type.INTEGER);
    }
}
