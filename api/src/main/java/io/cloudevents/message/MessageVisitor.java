package io.cloudevents.message;

public interface MessageVisitor<BV extends BinaryMessageVisitor<R>, R> extends BinaryMessageVisitorFactory<BV, R>, StructuredMessageVisitor<R> { }
