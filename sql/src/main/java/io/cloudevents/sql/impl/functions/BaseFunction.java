package io.cloudevents.sql.impl.functions;

import io.cloudevents.sql.Function;

import java.util.stream.Collectors;
import java.util.stream.IntStream;

public abstract class BaseFunction implements Function {

    private final String name;

    protected BaseFunction(String name) {
        this.name = name.toUpperCase();
    }

    @Override
    public String name() {
        return name;
    }

    protected void requireValidParameterIndex(int i) {
        if (!isVariadic() && i >= arity()) {
            throw new IllegalArgumentException("The provided index must less than the arity of the function: " + i + " < " + arity());
        }
    }

    @Override
    public String toString() {
        StringBuilder builder = new StringBuilder();
        builder.append(name());
        builder.append('(');
        if (arity() > 0) {
            builder.append(
                IntStream.range(0, arity())
                    .mapToObj(i -> typeOfParameter(i).name())
                    .collect(Collectors.joining(","))
            );
            if (isVariadic()) {
                builder.append(", ")
                    .append(typeOfParameter(arity()))
                    .append("...");
            }
        }
        builder.append(')');
        return builder.toString();
    }
}
