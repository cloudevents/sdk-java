package io.cloudevents.kafka.impl;

import io.cloudevents.SpecVersion;
import io.cloudevents.kafka.KafkaProducerMessageVisitor;
import org.apache.kafka.clients.producer.ProducerRecord;
import org.apache.kafka.common.header.internals.RecordHeaders;

public final class KafkaProducerMessageVisitorImpl<K> extends
    BaseKafkaMessageVisitorImpl<KafkaProducerMessageVisitor<K>, ProducerRecord<K, byte[]>>
    implements KafkaProducerMessageVisitor<K> {

    private final String topic;
    private final K key;
    private final Integer partition;
    private final Long timestamp;

    public KafkaProducerMessageVisitorImpl(String topic, Integer partition, Long timestamp, K key) {
        super(new RecordHeaders());
        this.topic = topic;
        this.key = key;
        this.partition = partition;
        this.timestamp = timestamp;
    }

    @Override
    public ProducerRecord<K, byte[]> end() {
        return new ProducerRecord<>(this.topic, this.partition, this.timestamp, this.key, this.value, this.headers);
    }

    @Override
    public KafkaProducerMessageVisitor<K> createBinaryMessageVisitor(SpecVersion version) {
        this.setAttribute("specversion", version.toString());
        return this;
    }
}
