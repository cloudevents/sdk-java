package io.cloudevents.format;

import io.cloudevents.CloudEvent;

import java.util.Set;

public interface EventFormat {

    byte[] serializeToBytes(CloudEvent event);

    String serializeToString(CloudEvent event);

    CloudEvent deserialize(byte[] event);

    CloudEvent deserialize(String event);

    Set<String> supportedContentTypes();

}
