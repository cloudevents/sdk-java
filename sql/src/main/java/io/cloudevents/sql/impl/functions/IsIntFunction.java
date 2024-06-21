package io.cloudevents.sql.impl.functions;

import io.cloudevents.CloudEvent;
import io.cloudevents.sql.EvaluationContext;
import io.cloudevents.sql.EvaluationRuntime;
import io.cloudevents.sql.Type;
import io.cloudevents.sql.impl.runtime.EvaluationResult;
import io.cloudevents.sql.impl.runtime.TypeCastingProvider;

public class IsIntFunction extends BaseOneArgumentFunction<String, Boolean> {

    public IsIntFunction() {
        super("IS_INT", String.class, Boolean.class);
    }

    @Override
    public EvaluationResult invoke(EvaluationContext ctx, EvaluationRuntime evaluationRuntime, CloudEvent event, String argument) {
        return new EvaluationResult(TypeCastingProvider.canCast(argument, Type.INTEGER));
    }
}
