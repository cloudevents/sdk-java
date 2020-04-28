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

import io.cloudevents.kafka.impl.KafkaBinaryMessageImpl;
import io.cloudevents.kafka.impl.KafkaHeaders;
import io.cloudevents.message.Message;
import io.cloudevents.message.impl.GenericStructuredMessage;
import io.cloudevents.message.impl.MessageUtils;
import io.cloudevents.message.impl.UnknownEncodingMessage;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.apache.kafka.common.header.Headers;

public final class KafkaMessageFactory {

    private KafkaMessageFactory() {
    }

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
