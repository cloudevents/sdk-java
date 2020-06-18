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

package io.cloudevents.kafka.impl;

import io.cloudevents.SpecVersion;
import org.apache.kafka.clients.producer.ProducerRecord;
import org.apache.kafka.common.header.internals.RecordHeaders;

public final class KafkaProducerMessageWriterImpl<K>
    extends BaseKafkaMessageWriterImpl<ProducerRecord<K, byte[]>> {

    private final String topic;
    private final K key;
    private final Integer partition;
    private final Long timestamp;

    public KafkaProducerMessageWriterImpl(String topic, Integer partition, Long timestamp, K key) {
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
    public KafkaProducerMessageWriterImpl<K> create(SpecVersion version) {
        this.setAttribute("specversion", version.toString());
        return this;
    }
}
