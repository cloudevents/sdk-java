package io.cloudevents.format;

import io.cloudevents.CloudEvent;

import java.util.Collections;
import java.util.Set;

public interface EventFormat {

    byte[] serialize(CloudEvent event) throws EventSerializationException;

    CloudEvent deserialize(byte[] event) throws EventDeserializationException;

    default Set<String> deserializableContentTypes() {
        return Collections.singleton(serializedContentType());
    }

    ;

    String serializedContentType();

}
