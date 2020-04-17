package io.cloudevents.message;

import io.cloudevents.CloudEvent;

@FunctionalInterface
public interface StructuredMessage {

    /**
     *
     * @param visitor
     * @throws MessageVisitException
     * @throws IllegalStateException If the message is not a valid structured message
     */
    void visit(StructuredMessageVisitor visitor) throws MessageVisitException, IllegalStateException;

    default CloudEvent toEvent() throws MessageVisitException, IllegalStateException {
        //TODO
    };

}
