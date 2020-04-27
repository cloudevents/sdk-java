package io.cloudevents.kafka;

import io.cloudevents.kafka.impl.KafkaBinaryMessageImpl;
import io.cloudevents.kafka.impl.KafkaHeaders;
import io.cloudevents.message.Message;
import io.cloudevents.message.impl.GenericStructuredMessage;
import io.cloudevents.message.impl.MessageUtils;
import io.cloudevents.message.impl.UnknownEncodingMessage;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.apache.kafka.common.header.Headers;

public interface KafkaMessageFactory {

    static <K> Message create(ConsumerRecord<K, byte[]> record) throws IllegalArgumentException {
        return create(record.headers(), record.value());
    }

    static Message create(Headers headers, byte[] payload) throws IllegalArgumentException {
        return MessageUtils.parseStructuredOrBinaryMessage(
            () -> KafkaHeaders.getParsedKafkaHeader(headers, KafkaHeaders.CONTENT_TYPE),
            format -> new GenericStructuredMessage(format, payload),
            () -> KafkaHeaders.getParsedKafkaHeader(headers, KafkaHeaders.SPEC_VERSION),
            sv -> new KafkaBinaryMessageImpl(sv, headers, payload),
            UnknownEncodingMessage::new
        );
    }

}
