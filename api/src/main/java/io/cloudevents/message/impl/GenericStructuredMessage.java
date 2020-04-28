package io.cloudevents.message.impl;

import io.cloudevents.format.EventFormat;
import io.cloudevents.format.EventFormatProvider;
import io.cloudevents.message.MessageVisitException;
import io.cloudevents.message.StructuredMessageVisitor;

public class GenericStructuredMessage extends BaseStructuredMessage {

    private EventFormat format;
    private byte[] payload;

    public GenericStructuredMessage(EventFormat format, byte[] payload) {
        this.format = format;
        this.payload = payload;
    }

    @Override
    public <T> T visit(StructuredMessageVisitor<T> visitor) throws MessageVisitException, IllegalStateException {
        return visitor.setEvent(format, payload);
    }

    /**
     * TODO
     *
     * @param contentType
     * @param payload
     * @return null if format was not found, otherwise returns the built message
     */
    public static GenericStructuredMessage fromContentType(String contentType, byte[] payload) {
        EventFormat format = EventFormatProvider.getInstance().resolveFormat(contentType);
        if (format == null) {
            return null;
        }

        return new GenericStructuredMessage(format, payload);
    }

}
