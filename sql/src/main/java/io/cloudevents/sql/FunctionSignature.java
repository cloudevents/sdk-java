package io.cloudevents.sql;

public interface FunctionSignature {

    String getName();

    Type getParameter(int v);

    int parameterLength();

    boolean isLastParameterVariadic();

}
