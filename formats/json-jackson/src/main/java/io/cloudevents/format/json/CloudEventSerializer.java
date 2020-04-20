package io.cloudevents.format.json;

import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.databind.JsonSerializer;
import com.fasterxml.jackson.databind.SerializerProvider;
import com.fasterxml.jackson.databind.ser.std.StdSerializer;
import io.cloudevents.CloudEvent;

import java.io.IOException;

public class CloudEventSerializer extends StdSerializer<CloudEvent> {
    protected CloudEventSerializer() {
        super(CloudEvent.class);
    }

    @Override
    public void serialize(CloudEvent value, JsonGenerator gen, SerializerProvider provider) throws IOException {
        JsonSerializer<Object> attributesSerializer = provider.findValueSerializer(value.getAttributes().getClass());
        // Serialize attributes
        attributesSerializer.serialize(value.getAttributes(), gen, provider);

    }
}
