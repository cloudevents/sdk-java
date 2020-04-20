package io.cloudevents.message;

import io.cloudevents.SpecVersion;

import java.util.function.Function;

@FunctionalInterface
public interface BinaryMessageVisitorFactory<T extends BinaryMessageVisitor<V>, V> extends Function<SpecVersion, T> { }
