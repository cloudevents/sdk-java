package io.cloudevents.message;

public interface BinaryMessageAttributes {

    /**
     *
     * @param visitor
     * @throws MessageVisitException
     */
    void visit(BinaryMessageAttributesVisitor visitor) throws MessageVisitException;

}
