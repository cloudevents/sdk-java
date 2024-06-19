package io.cloudevents.sql.impl.functions;

import io.cloudevents.CloudEvent;
import io.cloudevents.sql.EvaluationContext;
import io.cloudevents.sql.EvaluationRuntime;
import io.cloudevents.sql.Type;
import io.cloudevents.sql.impl.runtime.EvaluationResult;
import io.cloudevents.sql.impl.runtime.TypeCastingProvider;

public class StringFunction extends BaseOneArgumentFunction<Object, String> {

    public StringFunction() {
        super("STRING", Object.class, String.class);
    }

    @Override
    public EvaluationResult invoke(EvaluationContext ctx, EvaluationRuntime evaluationRuntime, CloudEvent event, Object argument) {
        return TypeCastingProvider.cast(ctx, new EvaluationResult(argument), Type.STRING);
    }
}
