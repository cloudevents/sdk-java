package io.cloudevents.kafka;

import io.cloudevents.message.Message;
import org.apache.kafka.common.header.Headers;
import org.apache.kafka.common.serialization.Deserializer;

/**
 * Deserializer for {@link Message}
 */
public class CloudEventMessageDeserializer implements Deserializer<Message> {

    @Override
    public Message deserialize(String topic, byte[] data) {
        throw new UnsupportedOperationException("CloudEventDeserializer supports only the signature deserialize(String, Headers, byte[])");
    }

    @Override
    public Message deserialize(String topic, Headers headers, byte[] data) {
        return KafkaMessageFactory.create(headers, data);
    }
}
