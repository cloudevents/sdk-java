package io.cloudevents.message;

import io.cloudevents.types.Time;

import java.net.URI;
import java.time.ZonedDateTime;

@FunctionalInterface
public interface BinaryMessageAttributesVisitor {

    void setAttribute(String name, String value) throws MessageVisitException;

    default void setAttribute(String name, URI value) throws MessageVisitException {
        setAttribute(name, value.toString());
    }

    default void setAttribute(String name, ZonedDateTime value) throws MessageVisitException {
        setAttribute(name, value.format(Time.RFC3339_DATE_FORMAT));
    }

}
