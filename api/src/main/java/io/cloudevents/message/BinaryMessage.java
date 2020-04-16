package io.cloudevents.message;

@FunctionalInterface
public interface BinaryMessage {

    /**
     *
     * @param visitor
     * @throws MessageVisitException
     * @throws IllegalStateException If the message is not a valid binary message
     */
    void visit(BinaryMessageVisitor visitor) throws MessageVisitException, IllegalStateException;

}
