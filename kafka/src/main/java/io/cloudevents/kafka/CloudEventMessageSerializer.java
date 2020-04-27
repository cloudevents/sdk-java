package io.cloudevents.kafka;

import io.cloudevents.kafka.impl.KafkaSerializerMessageVisitorImpl;
import io.cloudevents.message.Message;
import org.apache.kafka.common.header.Headers;
import org.apache.kafka.common.serialization.Serializer;

import java.util.Map;

/**
 * Serializer for {@link Message}. This {@link Serializer} can't be used as a key serializer.
 */
public class CloudEventMessageSerializer implements Serializer<Message> {

    @Override
    public void configure(Map<String, ?> configs, boolean isKey) {
        if (isKey) {
            throw new IllegalStateException("Cannot use CloudEventMessageSerializer as key serializer");
        }
    }

    @Override
    public byte[] serialize(String topic, Message data) {
        throw new UnsupportedOperationException("CloudEventMessageSerializer supports only the signature serialize(String, Headers, Message)");
    }

    @Override
    public byte[] serialize(String topic, Headers headers, Message data) {
        return data.visit(new KafkaSerializerMessageVisitorImpl(headers));
    }
}
