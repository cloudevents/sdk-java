package io.cloudevents.message;

public enum Encoding {
    STRUCTURED,
    BINARY,
    UNKNOWN;

    public static IllegalStateException UNKNOWN_ENCODING_EXCEPTION = new IllegalStateException("Unknown encoding");

    public static IllegalStateException WRONG_ENCODING_EXCEPTION = new IllegalStateException("Wrong encoding");
}
