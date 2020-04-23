package io.cloudevents.message;

import io.cloudevents.types.Time;

import java.net.URI;
import java.time.ZonedDateTime;

@FunctionalInterface
public interface BinaryMessageAttributesVisitor {

    /**
     * Set attribute with type {@link String}. This setter should not be invoked for specversion, because the built Visitor already
     * has the information through the {@link BinaryMessageVisitorFactory}
     *
     * @param name
     * @param value
     * @throws MessageVisitException
     */
    void setAttribute(String name, String value) throws MessageVisitException;

    /**
     * Set attribute with type {@link URI}.
     *
     * @param name
     * @param value
     * @throws MessageVisitException
     */
    default void setAttribute(String name, URI value) throws MessageVisitException {
        setAttribute(name, value.toString());
    }

    /**
     * Set attribute with type {@link ZonedDateTime} attribute.
     *
     * @param name
     * @param value
     * @throws MessageVisitException
     */
    default void setAttribute(String name, ZonedDateTime value) throws MessageVisitException {
        setAttribute(name, value.format(Time.RFC3339_DATE_FORMAT));
    }

}
