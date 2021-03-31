package io.cloudevents.sql.impl;

import io.cloudevents.sql.EvaluationException;

import java.util.ArrayList;
import java.util.List;

public class EvaluationExceptions {

    private List<EvaluationException> exceptions;

    public EvaluationExceptions() {
    }

    public void appendException(EvaluationException exception) {
        if (this.exceptions == null) {
            this.exceptions = new ArrayList<>();
        }
        this.exceptions.add(exception);
    }

    public List<EvaluationException> getExceptions() {
        return exceptions;
    }
}
