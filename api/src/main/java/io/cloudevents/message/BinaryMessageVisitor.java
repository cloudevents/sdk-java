package io.cloudevents.message;

import io.cloudevents.SpecVersion;

public interface BinaryMessageVisitor extends BinaryMessageAttributesVisitor, BinaryMessageExtensionsVisitor {

    void setSpecVersion(SpecVersion specVersion) throws MessageVisitException;

    // TODO one day we'll convert this to some byte stream
    void setBody(byte[] value) throws MessageVisitException;

}
