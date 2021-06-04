package io.cloudevents.sql.impl;

import io.cloudevents.sql.EvaluationException;

import java.util.ArrayList;
import java.util.List;

class ExceptionStore implements ExceptionThrower {

    private List<EvaluationException> exceptions;

    ExceptionStore() {
    }

    @Override
    public void throwException(EvaluationException exception) {
        if (this.exceptions == null) {
            this.exceptions = new ArrayList<>();
        }
        this.exceptions.add(exception);
    }

    List<EvaluationException> getExceptions() {
        return exceptions;
    }
}
