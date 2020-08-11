package io.cloudevents.core.codec.impl;

import io.cloudevents.core.codec.DataSerializationException;
import io.cloudevents.core.codec.EventDataCodec;

public class TextEventDataCodec implements EventDataCodec {
    @Override
    public byte[] serialize(String dataContentType, Object data) throws DataSerializationException, IllegalArgumentException {
        try {
            return data.toString().getBytes();
        } catch (Throwable e) {
            throw new DataSerializationException(e);
        }
    }

    @Override
    public boolean canSerialize(String dataContentType) {
        return "text/plain".equals(dataContentType);
    }
}
