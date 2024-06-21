package io.cloudevents.sql.impl.functions;

import io.cloudevents.CloudEvent;
import io.cloudevents.sql.EvaluationContext;
import io.cloudevents.sql.EvaluationRuntime;
import io.cloudevents.sql.Type;
import io.cloudevents.sql.impl.runtime.EvaluationResult;

import java.util.List;

public abstract class BaseTwoArgumentFunction<X, Y, Z> extends BaseFunction {

    private final Type firstArg;
    private final Type secondArg;
    private final Type returnType;

    public BaseTwoArgumentFunction(String name, Class<X> firstArg, Class<Y> secondArg, Class<Z> returnClass) {
        super(name);
        this.firstArg = Type.fromClass(firstArg);
        this.secondArg = Type.fromClass(secondArg);
        this.returnType = Type.fromClass(returnClass);
    }

    abstract EvaluationResult invoke(EvaluationContext ctx, EvaluationRuntime evaluationRuntime, CloudEvent event, X x, Y y);

    @SuppressWarnings("unchecked")
    @Override
    public EvaluationResult invoke(EvaluationContext ctx, EvaluationRuntime evaluationRuntime, CloudEvent event, List<Object> arguments) {
        return this.invoke(ctx, evaluationRuntime, event, (X) arguments.get(0), (Y) arguments.get(1));
    }

    @Override
    public Type typeOfParameter(int i) {
        requireValidParameterIndex(i);
        switch (i) {
            case 0:
                return firstArg;
            case 1:
                return secondArg;
        }
        throw new IllegalArgumentException(); // This should be already checked by requireValidParameterIndex
    }

    @Override
    public Type returnType() {
        return this.returnType;
    }

    @Override
    public int arity() {
        return 2;
    }

    @Override
    public boolean isVariadic() {
        return false;
    }
}
