package io.cloudevents.message;

public class MessageVisitException extends RuntimeException {

    public enum MessageVisitExceptionKind {
        INVALID_ATTRIBUTE_TYPE
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

    public static MessageVisitException newInvalidAttributeType(String attributeName, Class<?> clazz) {
        return new MessageVisitException(
            MessageVisitExceptionKind.INVALID_ATTRIBUTE_TYPE,
            "Invalid attribute type for " + attributeName + ": " + clazz.getCanonicalName()
        );
    }

}
