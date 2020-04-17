package io.cloudevents.message;

import io.cloudevents.format.EventFormat;

public interface StructuredMessageVisitor {

    // TODO one day we'll convert this to some byte stream
    void setEvent(EventFormat format, byte[] value) throws MessageVisitException;

}
