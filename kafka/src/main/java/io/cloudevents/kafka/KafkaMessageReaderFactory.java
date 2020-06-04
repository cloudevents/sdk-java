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
import io.cloudevents.core.message.impl.GenericStructuredMessageReader;
import io.cloudevents.core.message.impl.MessageUtils;
import io.cloudevents.core.message.impl.UnknownEncodingMessageReader;
import io.cloudevents.kafka.impl.KafkaBinaryMessageReaderImpl;
import io.cloudevents.kafka.impl.KafkaHeaders;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.apache.kafka.common.header.Headers;

public final class KafkaMessageReaderFactory {

    private KafkaMessageReaderFactory() {
    }

    static <K> MessageReader create(ConsumerRecord<K, byte[]> record) throws IllegalArgumentException {
        return create(record.headers(), record.value());
    }

    static MessageReader create(Headers headers, byte[] payload) throws IllegalArgumentException {
        return MessageUtils.parseStructuredOrBinaryMessage(
            () -> KafkaHeaders.getParsedKafkaHeader(headers, KafkaHeaders.CONTENT_TYPE),
            format -> new GenericStructuredMessageReader(format, payload),
            () -> KafkaHeaders.getParsedKafkaHeader(headers, KafkaHeaders.SPEC_VERSION),
            sv -> new KafkaBinaryMessageReaderImpl(sv, headers, payload),
            UnknownEncodingMessageReader::new
        );
    }

}
