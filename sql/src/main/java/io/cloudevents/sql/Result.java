package io.cloudevents.sql;

public interface Result {

    Object value();

    boolean isFailed();

    // TODO this should be a list
    EvaluationException cause();
}
