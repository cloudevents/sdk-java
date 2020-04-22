package io.cloudevents.message;

import io.cloudevents.format.EventFormat;

@FunctionalInterface
public interface StructuredMessageVisitor<T> {

    // TODO one day we'll convert this to some byte stream
    T setEvent(EventFormat format, byte[] value) throws MessageVisitException;

}
