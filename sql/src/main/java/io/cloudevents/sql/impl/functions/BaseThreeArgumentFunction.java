package io.cloudevents.sql.impl.functions;

import io.cloudevents.CloudEvent;
import io.cloudevents.sql.EvaluationContext;
import io.cloudevents.sql.EvaluationRuntime;
import io.cloudevents.sql.Type;
import io.cloudevents.sql.impl.runtime.EvaluationResult;

import java.util.List;

public abstract class BaseThreeArgumentFunction<W, X, Y, Z> extends BaseFunction {

    private final Type firstArg;
    private final Type secondArg;
    private final Type thirdArg;
    private final Type returnType;

    public BaseThreeArgumentFunction(String name, Class<W> firstArg, Class<X> secondArg, Class<Y> thirdArg, Class<Z> returnClass) {
        super(name);
        this.firstArg = Type.fromClass(firstArg);
        this.secondArg = Type.fromClass(secondArg);
        this.thirdArg = Type.fromClass(thirdArg);
        this.returnType = Type.fromClass(returnClass);
    }

    abstract EvaluationResult invoke(EvaluationContext ctx, EvaluationRuntime evaluationRuntime, CloudEvent event, W w, X x, Y y);

    @SuppressWarnings("unchecked")
    @Override
    public EvaluationResult invoke(EvaluationContext ctx, EvaluationRuntime evaluationRuntime, CloudEvent event, List<Object> arguments) {
        return this.invoke(ctx, evaluationRuntime, event, (W) arguments.get(0), (X) arguments.get(1), (Y) arguments.get(2));
    }

    @Override
    public Type typeOfParameter(int i) {
        requireValidParameterIndex(i);
        switch (i) {
            case 0:
                return firstArg;
            case 1:
                return secondArg;
            case 2:
                return thirdArg;
        }
        throw new IllegalArgumentException(); // This should be already checked by requireValidParameterIndex
    }

    @Override
    public int arity() {
        return 3;
    }

    @Override
    public Type returnType() {
        return this.returnType;
    }

    @Override
    public boolean isVariadic() {
        return false;
    }
}
