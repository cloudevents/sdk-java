package io.cloudevents.message;

import io.cloudevents.SpecVersion;

@FunctionalInterface
public interface BinaryMessageVisitorFactory<V extends BinaryMessageVisitor<R>, R> {
    V createBinaryMessageVisitor(SpecVersion version);
}
