package io.cloudevents.format;

public class EventSerializationException extends RuntimeException {
    public EventSerializationException(Throwable e) {
        super(e);
    }
}
