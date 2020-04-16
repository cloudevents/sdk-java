package io.cloudevents.message;

@FunctionalInterface
public interface StructuredMessage {

    /**
     *
     * @param visitor
     * @throws MessageVisitException
     * @throws IllegalStateException If the message is not a valid structured message
     */
    void visit(StructuredMessageVisitor visitor) throws MessageVisitException, IllegalStateException;

}
