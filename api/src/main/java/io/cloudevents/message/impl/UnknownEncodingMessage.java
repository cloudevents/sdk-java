package io.cloudevents.message.impl;

import io.cloudevents.message.*;

public class UnknownEncodingMessage implements Message {
    @Override
    public Encoding getEncoding() {
        return Encoding.UNKNOWN;
    }

    @Override
    public <T extends BinaryMessageVisitor<V>, V> V visit(BinaryMessageVisitorFactory<T, V> visitorFactory) throws MessageVisitException, IllegalStateException {
        throw Encoding.WRONG_ENCODING_EXCEPTION;
    }

    @Override
    public <T> T visit(StructuredMessageVisitor<T> visitor) throws MessageVisitException, IllegalStateException {
        throw Encoding.WRONG_ENCODING_EXCEPTION;
    }
}
