package io.cloudevents.message;

public interface Message extends StructuredMessage, BinaryMessage {

    Encoding getEncoding();

}
