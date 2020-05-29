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

import io.cloudevents.CloudEvent;
import io.cloudevents.core.message.Message;
import io.cloudevents.core.test.Data;
import org.apache.kafka.clients.producer.ProducerRecord;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

public class CloudEventDeserializerTest {

    @Test
    public void deserializerShouldWork() {
        String topic = "test";
        CloudEvent inEvent = Data.V1_WITH_JSON_DATA;

        CloudEventDeserializer deserializer = new CloudEventDeserializer();

        // Serialize the event first
        ProducerRecord<Void, byte[]> inRecord = Message.writeBinaryEvent(inEvent, KafkaProducerMessageVisitor.create(topic));
        CloudEvent outEvent = deserializer.deserialize(topic, inRecord.headers(), inRecord.value());

        assertThat(outEvent)
            .isEqualTo(inEvent);
    }

}
