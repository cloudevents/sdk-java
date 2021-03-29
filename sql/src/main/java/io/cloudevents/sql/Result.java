package io.cloudevents.sql;

import java.util.Collection;

public interface Result {

    Object value();

    boolean isFailed();

    Collection<EvaluationException> causes();
}
