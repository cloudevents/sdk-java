package io.cloudevents.sql.impl.runtime;

import io.cloudevents.sql.*;

public class EvaluationRuntimeImpl implements EvaluationRuntime {

    private static class SingletonContainer {
        private final static EvaluationRuntimeImpl INSTANCE = new EvaluationRuntimeImpl(FunctionTable.getDefaultInstance());
    }

    /**
     * @return instance of {@link EvaluationRuntimeImpl}
     */
    public static EvaluationRuntime getInstance() {
        return EvaluationRuntimeImpl.SingletonContainer.INSTANCE;
    }

    private final FunctionTable functionTable;

    public EvaluationRuntimeImpl(FunctionTable functionTable) {
        this.functionTable = functionTable;
    }

    @Override
    public Function resolveFunction(String name, int args) throws IllegalStateException {
        return functionTable.resolve(name, args);
    }
}
