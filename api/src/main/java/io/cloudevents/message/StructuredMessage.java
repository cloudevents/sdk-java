package io.cloudevents.message;

import io.cloudevents.CloudEvent;
import io.cloudevents.format.EventFormat;

@FunctionalInterface
public interface StructuredMessage {

    /**
     *
     * @param visitor
     * @throws MessageVisitException
     * @throws IllegalStateException If the message is not a valid structured message
     */
    <T> T visit(StructuredMessageVisitor<T> visitor) throws MessageVisitException, IllegalStateException;

    default CloudEvent toEvent() throws MessageVisitException, IllegalStateException {
        return this.visit(EventFormat::deserialize);
    };

}
