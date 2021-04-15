package io.cloudevents.sql.impl;

import io.cloudevents.sql.EvaluationException;

class FailFastExceptionThrower implements ExceptionThrower {

    private static class SingletonContainer {
        private final static FailFastExceptionThrower INSTANCE = new FailFastExceptionThrower();
    }

    static FailFastExceptionThrower getInstance() {
        return FailFastExceptionThrower.SingletonContainer.INSTANCE;
    }

    @Override
    public void throwException(EvaluationException exception) {
        throw exception;
    }
}
