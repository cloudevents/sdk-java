package io.cloudevents.message;

import io.cloudevents.SpecVersion;

@FunctionalInterface
public interface BinaryMessageVisitorFactory<T extends BinaryMessageVisitor<V>, V> {
    T createBinaryMessageVisitor(SpecVersion version);
}
