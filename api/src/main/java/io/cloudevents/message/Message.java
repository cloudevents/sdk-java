package io.cloudevents.message;

import io.cloudevents.CloudEvent;

public interface Message extends StructuredMessage, BinaryMessage {

    Encoding getEncoding();

    default CloudEvent toEvent() throws MessageVisitException, IllegalStateException {
        //TODO
    };

}
