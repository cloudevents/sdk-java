package io.cloudevents.message;

@FunctionalInterface
public interface BinaryMessageExtensionsVisitor {

    void setExtension(String name, String value) throws MessageVisitException;

    default void setExtension(String name, Number value) throws MessageVisitException {
        setExtension(name, value.toString());
    }

    default void setExtension(String name, Boolean value) throws MessageVisitException {
        setExtension(name, value.toString());
    }

}
