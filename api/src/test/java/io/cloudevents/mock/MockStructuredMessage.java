package io.cloudevents.mock;

import io.cloudevents.format.EventFormat;
import io.cloudevents.message.*;

public class MockStructuredMessage implements Message, StructuredMessageVisitor<MockStructuredMessage> {

    private EventFormat format;
    private byte[] payload;

    @Override
    public Encoding getEncoding() {
        return Encoding.STRUCTURED;
    }

    @Override
    public <T extends BinaryMessageVisitor<V>, V> V visit(BinaryMessageVisitorFactory<T, V> visitor) throws MessageVisitException, IllegalStateException {
        throw Encoding.WRONG_ENCODING_EXCEPTION;
    }

    @Override
    public <T> T visit(StructuredMessageVisitor<T> visitor) throws MessageVisitException, IllegalStateException {
        if (this.format == null) {
            throw new IllegalStateException("MockStructuredMessage is empty");
        }

        return visitor.setEvent(this.format, this.payload);
    }

    @Override
    public MockStructuredMessage setEvent(EventFormat format, byte[] value) throws MessageVisitException {
        this.format = format;
        this.payload = value;

        return this;
    }
}
