package io.cloudevents.message.impl;

import io.cloudevents.message.Encoding;
import io.cloudevents.message.Message;
import io.cloudevents.message.MessageVisitException;
import io.cloudevents.message.StructuredMessageVisitor;

public abstract class BaseBinaryMessage implements Message {

    @Override
    public Encoding getEncoding() {
        return Encoding.BINARY;
    }

    @Override
    public <T> T visit(StructuredMessageVisitor<T> visitor) throws MessageVisitException, IllegalStateException {
        throw Encoding.UNKNOWN_ENCODING_EXCEPTION;
    }
}
