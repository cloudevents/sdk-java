package io.cloudevents.sql;

import io.cloudevents.sql.impl.EvaluationRuntimeBuilder;
import io.cloudevents.sql.impl.EvaluationRuntimeImpl;

/**
 * The evaluation runtime takes care of the function resolution, casting and other core functionalities to execute an expression.
 */
public interface EvaluationRuntime {

    /**
     * Check if the cast can be executed from {@code value} to the {@code target} type.
     *
     * @param value  the value to cast
     * @param target the type cast target
     * @return false if the cast trigger an error, true otherwise.
     */
    boolean canCast(Object value, Type target);

    /**
     * Return the {@code value} casted to the {@code target} type.
     *
     * @param ctx    the evaluation context
     * @param value  the value to cast
     * @param target the type cast target
     * @return the casted value, if the cast succeeds, otherwise the default value of the target type
     */
    Object cast(EvaluationContext ctx, Object value, Type target);

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
