package io.cloudevents.sql.impl.runtime;

import io.cloudevents.sql.EvaluationRuntime;
import io.cloudevents.sql.Function;

public class EvaluationRuntimeBuilder {

    private final FunctionTable functionTable;

    public EvaluationRuntimeBuilder() {
        this.functionTable = new FunctionTable(FunctionTable.getDefaultInstance());
    }

    public EvaluationRuntimeBuilder addFunction(Function function) throws IllegalArgumentException {
        this.functionTable.addFunction(function);
        return this;
    }

    public EvaluationRuntime build() {
        return new EvaluationRuntimeImpl(
            new TypeCastingProvider(),
            functionTable
        );
    }

}
