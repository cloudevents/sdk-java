package io.cloudevents;

public class DataConversionException extends RuntimeException {

    public DataConversionException(String from, String to, Throwable cause) {
        super("Cannot convert " + from + " data to " + to, cause);
    }
}
