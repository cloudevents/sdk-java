package io.cloudevents.message;

public class MessageVisitException extends RuntimeException {

    public enum MessageVisitExceptionKind {
        INVALID_SPEC_VERSION,
        INVALID_ATTRIBUTE_NAME,
        INVALID_ATTRIBUTE_TYPE,
        INVALID_ATTRIBUTE_VALUE,
        INVALID_EXTENSION_TYPE,
    }

    private MessageVisitExceptionKind kind;

    public MessageVisitException(MessageVisitExceptionKind kind, String message) {
        super(message);
        this.kind = kind;
    }

    public MessageVisitException(MessageVisitExceptionKind kind, String message, Throwable cause) {
        super(message, cause);
        this.kind = kind;
    }

    public MessageVisitExceptionKind getKind() {
        return kind;
    }

    public static MessageVisitException newInvalidSpecVersion(String specVersion) {
        return new MessageVisitException(
            MessageVisitExceptionKind.INVALID_ATTRIBUTE_TYPE,
            "Invalid specversion: " + specVersion
        );
    }

    public static MessageVisitException newInvalidAttributeName(String attributeName) {
        return new MessageVisitException(
            MessageVisitExceptionKind.INVALID_ATTRIBUTE_NAME,
            "Invalid attribute: " + attributeName
        );
    }

    public static MessageVisitException newInvalidAttributeType(String attributeName, Class<?> clazz) {
        return new MessageVisitException(
            MessageVisitExceptionKind.INVALID_ATTRIBUTE_TYPE,
            "Invalid attribute type for \"" + attributeName + "\": " + clazz.getCanonicalName()
        );
    }

    public static MessageVisitException newInvalidAttributeValue(String attributeName, Object value, Throwable cause) {
        return new MessageVisitException(
            MessageVisitExceptionKind.INVALID_ATTRIBUTE_VALUE,
            "Invalid attribute value for \"" + attributeName + "\": " + value,
            cause
        );
    }

    public static MessageVisitException newInvalidExtensionType(String extensionName, Class<?> clazz) {
        return new MessageVisitException(
            MessageVisitExceptionKind.INVALID_EXTENSION_TYPE,
            "Invalid extension type for \"" + extensionName + "\": " + clazz.getCanonicalName()
        );
    }
}
