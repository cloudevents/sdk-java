package io.cloudevents.message;

public interface BinaryMessageVisitor<V> extends BinaryMessageAttributesVisitor, BinaryMessageExtensionsVisitor {

    // TODO one day we'll convert this to some byte stream
    void setBody(byte[] value) throws MessageVisitException;

    // Returns an eventual output value
    V end();

}
