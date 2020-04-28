package io.cloudevents.message.impl;

import io.cloudevents.message.*;

public abstract class BaseStructuredMessage implements Message {

    @Override
    public Encoding getEncoding() {
        return Encoding.STRUCTURED;
    }

    @Override
    public <V extends BinaryMessageVisitor<R>, R> R visit(BinaryMessageVisitorFactory<V, R> visitorFactory) throws MessageVisitException, IllegalStateException {
        throw Encoding.UNKNOWN_ENCODING_EXCEPTION;
    }
}
