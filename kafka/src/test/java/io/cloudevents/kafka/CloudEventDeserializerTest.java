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
import io.cloudevents.core.builder.CloudEventBuilder;
import io.cloudevents.core.mock.MyCloudEventData;
import io.cloudevents.core.test.Data;
import io.cloudevents.rw.CloudEventDataMapper;
import org.apache.kafka.clients.producer.ProducerRecord;
import org.apache.kafka.common.header.Headers;
import org.junit.jupiter.api.Test;

import java.util.HashMap;

import static org.assertj.core.api.Assertions.assertThat;

public class CloudEventDeserializerTest {

    @Test
    public void deserializerShouldWork() {
        testDeserialize(
            new CloudEventDeserializer(),
            Data.V1_WITH_JSON_DATA,
            Data.V1_WITH_JSON_DATA
        );
    }

    @Test
    public void deserializerWithMapper() {
        CloudEventDataMapper<MyCloudEventData> mapper = data -> MyCloudEventData.fromStringBytes(data.toBytes());

        CloudEventDeserializer deserializer = new CloudEventDeserializer();
        HashMap<String, Object> config = new HashMap<>();
        config.put(CloudEventDeserializer.MAPPER_CONFIG, mapper);
        deserializer.configure(config, false);

        testDeserialize(
            deserializer,
            CloudEventBuilder.v1(Data.V1_MIN)
                .withData("application/json", "10".getBytes())
                .build(),
            CloudEventBuilder.v1(Data.V1_MIN)
                .withData("application/json", new MyCloudEventData(10))
                .build()
        );
    }

    @Test
    public void deserializerShouldWorkWithNullableManuallyDefinedHeaders() {
        String topic = "test";
        CloudEvent testCloudEvent = Data.V1_MIN;
        CloudEventDeserializer cloudEventDeserializer = new CloudEventDeserializer();

        // Serialize the event first
        ProducerRecord<Void, byte[]> inRecord = KafkaMessageFactory
            .createWriter(topic)
            .writeBinary(testCloudEvent);

        // add optional subject header with null value
        Headers headers = inRecord.headers();
        headers.add("ce_subject", null);
        CloudEvent outEvent = cloudEventDeserializer.deserialize(topic, headers, inRecord.value());

        assertThat(outEvent)
            .isEqualTo(testCloudEvent);
    }

    private void testDeserialize(CloudEventDeserializer deserializer, CloudEvent input, CloudEvent expected) {
        String topic = "test";

        // Serialize the event first
        ProducerRecord<Void, byte[]> inRecord = KafkaMessageFactory
            .createWriter(topic)
            .writeBinary(input);
        CloudEvent outEvent = deserializer.deserialize(topic, inRecord.headers(), inRecord.value());

        assertThat(outEvent)
            .isEqualTo(expected);
    }

}
