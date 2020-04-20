package io.cloudevents.format;

import io.cloudevents.CloudEvent;

import java.util.Set;

public interface EventFormat {

    byte[] serialize(CloudEvent event);

    CloudEvent deserialize(byte[] event);

    Set<String> supportedContentTypes();

}
