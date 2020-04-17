package io.cloudevents.message;

import io.cloudevents.CloudEvent;

@FunctionalInterface
public interface BinaryMessage {

    /**
     *
     * @param visitor
     * @throws MessageVisitException
     * @throws IllegalStateException If the message is not a valid binary message
     */
    void visit(BinaryMessageVisitor visitor) throws MessageVisitException, IllegalStateException;

    default CloudEvent toEvent() throws MessageVisitException, IllegalStateException {
        //TODO
    };

}
