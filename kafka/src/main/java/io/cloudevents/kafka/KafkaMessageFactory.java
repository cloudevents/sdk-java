/*
 * Copyright 2018-Present The CloudEvents Authors
 * <p>
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * <p>
 * http://www.apache.org/licenses/LICENSE-2.0
 * <p>
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 *
 */

package io.cloudevents.kafka;

import io.cloudevents.core.message.MessageReader;
import io.cloudevents.core.message.MessageWriter;
import io.cloudevents.core.message.impl.GenericStructuredMessageReader;
import io.cloudevents.core.message.impl.MessageUtils;
import io.cloudevents.kafka.impl.KafkaBinaryMessageReaderImpl;
import io.cloudevents.kafka.impl.KafkaHeaders;
import io.cloudevents.kafka.impl.KafkaProducerMessageWriterImpl;
import io.cloudevents.rw.CloudEventWriter;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.apache.kafka.clients.producer.ProducerRecord;
import org.apache.kafka.common.header.Headers;

import javax.annotation.ParametersAreNonnullByDefault;

/**
 * This class provides a collection of methods to create {@link io.cloudevents.core.message.MessageReader}
 * and {@link io.cloudevents.core.message.MessageWriter} for Kafka Producer and Consumer.
 */
@ParametersAreNonnullByDefault
public final class KafkaMessageFactory {

    private KafkaMessageFactory() {
    }

    /**
     * Create a {@link io.cloudevents.core.message.MessageReader} to read {@link ConsumerRecord}
     *
     * @param record the record to convert to {@link io.cloudevents.core.message.MessageReader}
     * @param <K>    the type of the record key
     * @return the new {@link io.cloudevents.core.message.MessageReader}
     */
    public static <K> MessageReader createReader(ConsumerRecord<K, byte[]> record) throws IllegalArgumentException {
        return createReader(record.headers(), record.value());
    }

    /**
     * @see #createReader(ConsumerRecord)
     */
    public static MessageReader createReader(Headers headers, byte[] payload) throws IllegalArgumentException {
        return MessageUtils.parseStructuredOrBinaryMessage(
            () -> KafkaHeaders.getParsedKafkaHeader(headers, KafkaHeaders.CONTENT_TYPE),
            format -> new GenericStructuredMessageReader(format, payload),
            () -> KafkaHeaders.getParsedKafkaHeader(headers, KafkaHeaders.SPEC_VERSION),
            sv -> new KafkaBinaryMessageReaderImpl(sv, headers, payload)
        );
    }

    /**
     * Create a {@link io.cloudevents.core.message.MessageWriter} to write a {@link org.apache.kafka.clients.producer.ProducerRecord}
     *
     * @param topic     the topic where to write the record
     * @param partition the partition where to write the record
     * @param timestamp the timestamp of the record
     * @param key       the key of the record
     * @param <K>       the key type
     * @return the new {@link io.cloudevents.core.message.MessageWriter}
     */
    public static <K> MessageWriter<CloudEventWriter<ProducerRecord<K, byte[]>>, ProducerRecord<K, byte[]>> createWriter(String topic, Integer partition, Long timestamp, K key) {
        return new KafkaProducerMessageWriterImpl<>(topic, partition, timestamp, key);
    }

    /**
     * @see #createWriter(String, Integer, Long, Object)
     */
    public static <K> MessageWriter<CloudEventWriter<ProducerRecord<K, byte[]>>, ProducerRecord<K, byte[]>> createWriter(String topic, Integer partition, K key) {
        return createWriter(topic, partition, null, key);
    }

    /**
     * @see #createWriter(String, Integer, Long, Object)
     */
    public static <K> MessageWriter<CloudEventWriter<ProducerRecord<K, byte[]>>, ProducerRecord<K, byte[]>> createWriter(String topic, K key) {
        return createWriter(topic, null, null, key);
    }

    /**
     * @see #createWriter(String, Integer, Long, Object)
     */
    public static MessageWriter<CloudEventWriter<ProducerRecord<Void, byte[]>>, ProducerRecord<Void, byte[]>> createWriter(String topic) {
        return createWriter(topic, null, null, null);
    }
}
