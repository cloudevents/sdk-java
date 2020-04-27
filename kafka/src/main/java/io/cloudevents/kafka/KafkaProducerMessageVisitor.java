package io.cloudevents.kafka;

import io.cloudevents.kafka.impl.KafkaProducerMessageVisitorImpl;
import io.cloudevents.message.BinaryMessageVisitor;
import io.cloudevents.message.MessageVisitor;
import org.apache.kafka.clients.producer.ProducerRecord;

public interface KafkaProducerMessageVisitor<K> extends MessageVisitor<KafkaProducerMessageVisitor<K>, ProducerRecord<K, byte[]>>, BinaryMessageVisitor<ProducerRecord<K, byte[]>> {

    static <V> KafkaProducerMessageVisitor<V> create(String topic, Integer partition, Long timestamp, V key) {
        return new KafkaProducerMessageVisitorImpl<V>(topic, partition, timestamp, key);
    }

    static <V> KafkaProducerMessageVisitor<V> create(String topic, Integer partition, V key) {
        return create(topic, partition, null, key);
    }

    static <V> KafkaProducerMessageVisitor<V> create(String topic, V key) {
        return create(topic, null, null, key);
    }

    static KafkaProducerMessageVisitor<Void> create(String topic) {
        return create(topic, null, null, null);
    }

}
