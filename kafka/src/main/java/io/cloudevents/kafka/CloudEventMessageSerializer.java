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
import io.cloudevents.kafka.impl.KafkaSerializerMessageWriterImpl;
import org.apache.kafka.common.header.Headers;
import org.apache.kafka.common.serialization.Serializer;

import java.util.Map;

/**
 * Kafka {@link Serializer} for {@link MessageReader}.
 * <p>
 * This {@link Serializer} can't be used as a key serializer.
 */
public class CloudEventMessageSerializer implements Serializer<MessageReader> {

    @Override
    public void configure(Map<String, ?> configs, boolean isKey) {
        if (isKey) {
            throw new IllegalStateException("Cannot use CloudEventMessageSerializer as key serializer");
        }
    }

    @Override
    public byte[] serialize(String topic, MessageReader data) {
        throw new UnsupportedOperationException("CloudEventMessageSerializer supports only the signature serialize(String, Headers, Message)");
    }

    @Override
    public byte[] serialize(String topic, Headers headers, MessageReader data) {
        return data.read(new KafkaSerializerMessageWriterImpl(headers));
    }
}
