package io.cloudevents.message;

public interface BinaryMessageExtensions {

    /**
     * @param visitor
     * @throws MessageVisitException
     */
    void visitExtensions(BinaryMessageExtensionsVisitor visitor) throws MessageVisitException;

}
