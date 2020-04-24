package io.cloudevents.http.vertx.impl;

import io.cloudevents.format.EventFormat;
import io.cloudevents.http.vertx.VertxMessage;
import io.cloudevents.message.*;
import io.vertx.core.buffer.Buffer;

public class StructuredVertxMessageImpl implements VertxMessage {

    private final EventFormat format;
    private final Buffer buffer;

    public StructuredVertxMessageImpl(EventFormat format, Buffer buffer) {
        this.format = format;
        this.buffer = buffer;
    }

    @Override
    public Encoding getEncoding() {
        return Encoding.STRUCTURED;
    }

    @Override
    public <T extends BinaryMessageVisitor<V>, V> V visit(BinaryMessageVisitorFactory<T, V> visitorFactory) throws MessageVisitException, IllegalStateException {
        throw Encoding.WRONG_ENCODING_EXCEPTION;
    }

    @Override
    public <T> T visit(StructuredMessageVisitor<T> visitor) throws MessageVisitException, IllegalStateException {
        return visitor.setEvent(this.format, this.buffer.getBytes());
    }
}
