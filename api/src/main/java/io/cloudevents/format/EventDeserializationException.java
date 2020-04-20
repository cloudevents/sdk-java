package io.cloudevents.format;

public class EventDeserializationException extends RuntimeException {
    public EventDeserializationException(Throwable e) {
        super(e);
    }
}
