package io.cloudevents.format.json;

import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.deser.std.StdDeserializer;
import io.cloudevents.CloudEvent;

import java.io.IOException;

public class CloudEventDeserializer extends StdDeserializer<CloudEvent> {
    protected CloudEventDeserializer() {
        super(CloudEvent.class);
    }

    @Override
    public CloudEvent deserialize(JsonParser p, DeserializationContext ctxt) throws IOException, JsonProcessingException {
        return null;
    }
}
