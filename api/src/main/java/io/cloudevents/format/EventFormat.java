package io.cloudevents.format;

import io.cloudevents.CloudEvent;

import java.util.Set;

public interface EventFormat {

    byte[] serialize(CloudEvent event) throws EventSerializationException;

    CloudEvent deserialize(byte[] event) throws EventDeserializationException;

    Set<String> supportedContentTypes();

}
