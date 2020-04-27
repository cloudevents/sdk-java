package io.cloudevents.kafka;

import io.cloudevents.CloudEvent;
import org.apache.kafka.common.header.Headers;
import org.apache.kafka.common.serialization.Deserializer;

/**
 * Deserializer for {@link CloudEvent}
 */
public class CloudEventDeserializer implements Deserializer<CloudEvent> {

    @Override
    public CloudEvent deserialize(String topic, byte[] data) {
        throw new UnsupportedOperationException("CloudEventDeserializer supports only the signature deserialize(String, Headers, byte[])");
    }

    @Override
    public CloudEvent deserialize(String topic, Headers headers, byte[] data) {
        return KafkaMessageFactory.create(headers, data).toEvent();
    }
}
