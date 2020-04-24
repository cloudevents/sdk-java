package io.cloudevents.kafka;

import io.cloudevents.message.Message;
import org.apache.kafka.clients.producer.ProducerRecord;
import org.apache.kafka.common.header.Headers;

public interface KafkaMessageFactory {

    static <T> Message create(ProducerRecord<T, byte[]> record) throws IllegalArgumentException {


        record.va

    }

    static <T> Message create(Headers headers, byte[] payload) throws IllegalArgumentException {
        headers.lastHeader()
    }

}
