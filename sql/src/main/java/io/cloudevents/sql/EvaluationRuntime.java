package io.cloudevents.sql;

import io.cloudevents.sql.impl.runtime.EvaluationRuntimeBuilder;
import io.cloudevents.sql.impl.runtime.EvaluationRuntimeImpl;

/**
 * The evaluation runtime takes care of the function resolution, casting and other core functionalities to execute an expression.
 */
public interface EvaluationRuntime {
    /**
     * Resolve a {@link Function} starting from its name and the concrete number of arguments.
     *
     * @param name the name of the function
     * @param args the number of arguments passed to the function
     * @return the resolved function
     * @throws IllegalStateException if the function cannot be resolved
     */
    Function resolveFunction(String name, int args) throws IllegalStateException;

    /**
     * @return a new builder to create a custom evaluation runtime
     */
    static EvaluationRuntimeBuilder builder() {
        return new EvaluationRuntimeBuilder();
    }

    /**
     * @return the default evaluation runtime
     */
    static EvaluationRuntime getDefault() {
        return EvaluationRuntimeImpl.getInstance();
    }

}
