package io.cloudevents.sql;

/**
 * Function is a CloudEvents Expression Language function definition.
 * This class' methods are used to perform the function dispatch and to cast the arguments to the appropriate values.
 */
public interface FunctionSignature {

    /**
     * @return uppercase name of the function
     */
    String name();

    /**
     * @return the type of the parameter at index {@code i}. If the function is variadic and if {@code i >= arity()}, this function returns the vararg type
     * @throws IllegalArgumentException if {@code i} is greater or equal to the arity and the function is not variadic
     */
    Type typeOfParameter(int i) throws IllegalArgumentException;

    /**
     * @return the arity, excluding the vararg parameter if {@code isVariadic() == true}
     */
    int arity();

    /**
     * @return true is the function is variadic
     */
    boolean isVariadic();

}
