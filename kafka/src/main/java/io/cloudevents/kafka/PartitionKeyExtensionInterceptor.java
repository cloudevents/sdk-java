package io.cloudevents.kafka;

import io.cloudevents.CloudEvent;
import org.apache.kafka.clients.producer.ProducerInterceptor;
import org.apache.kafka.clients.producer.ProducerRecord;
import org.apache.kafka.clients.producer.RecordMetadata;

import java.util.Map;

/**
 * This {@link ProducerInterceptor} implements the partitioning extension,
 * as described in the <a href="https://github.com/cloudevents/spec/blob/main/kafka-protocol-binding.md#31-key-mapping">CloudEvents Kafka specification</a>.
 * <p>
 * When using in your {@link org.apache.kafka.clients.producer.KafkaProducer},
 * it will pick the {@code partitionkey} extension from the event and will set it as record key.
 * If the extension is missing, It won't replace the key from the original record.
 */
public class PartitionKeyExtensionInterceptor implements ProducerInterceptor<Object, CloudEvent> {

    /**
     * The extension key of partition key extension.
     */
    public static final String PARTITION_KEY_EXTENSION = "partitionkey";

    @Override
    public ProducerRecord<Object, CloudEvent> onSend(ProducerRecord<Object, CloudEvent> record) {
        if (record.value() == null) {
            return record;
        }
        Object partitionKey = record.value().getExtension(PARTITION_KEY_EXTENSION);
        if (partitionKey == null) {
            return record;
        }

        return new ProducerRecord<>(record.topic(), record.partition(), record.timestamp(), partitionKey, record.value(), record.headers());
    }

    @Override
    public void onAcknowledgement(RecordMetadata metadata, Exception exception) {
    }

    @Override
    public void close() {
    }

    @Override
    public void configure(Map<String, ?> configs) {
    }
}
