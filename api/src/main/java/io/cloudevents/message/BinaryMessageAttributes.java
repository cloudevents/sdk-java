package io.cloudevents.message;

public interface BinaryMessageAttributes {

    /**
     * @param visitor
     * @throws MessageVisitException
     */
    void visitAttributes(BinaryMessageAttributesVisitor visitor) throws MessageVisitException;

}
