package io.cloudevents.message;

import io.cloudevents.CloudEvent;

public interface Message extends StructuredMessage, BinaryMessage {

    Encoding getEncoding();

    default CloudEvent toEvent() throws MessageVisitException, IllegalStateException {
        switch (getEncoding()) {
            case BINARY: return ((BinaryMessage)this).toEvent();
            case STRUCTURED: return ((StructuredMessage)this).toEvent();
            default: throw Encoding.UNKNOWN_ENCODING_EXCEPTION;
        }
    };

}
