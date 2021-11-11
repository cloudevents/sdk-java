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
package io.cloudevents.avro;

import java.util.Map;
import java.util.HashMap;
import java.net.URI;

import io.cloudevents.CloudEvent;
import io.cloudevents.CloudEventData;
import io.cloudevents.SpecVersion;
import io.cloudevents.core.builder.CloudEventBuilder;
import io.cloudevents.core.format.EventFormat;
import org.junit.jupiter.api.Test;
import static org.assertj.core.api.Assertions.assertThat;

public class AvroFormatTest {

    public static Map<String, Object> testData = new HashMap<>();

    static {
        testData.put("name", "Ning");
        testData.put("age", 22.0);
    }

    @Test
    public void testSerde() {
        EventFormat avroFormat = new AvroFormat();
        CloudEventData cloudEventData = new AvroCloudEventDataWrapper(testData);

        assertThat(cloudEventData).isNotNull();
        assertThat(cloudEventData.toBytes()).isNotNull();

        CloudEvent cloudEvent = CloudEventBuilder.v1()
            .withId("1")
            .withType("testdata")
            .withSource(URI.create("http://localhost/test"))
            .withData("application/avro", cloudEventData)
            .build();
        assertThat(cloudEvent).isNotNull();
        assertThat(cloudEvent.getSpecVersion()).isEqualTo(SpecVersion.V1);

        byte[] bytes = avroFormat.serialize(cloudEvent);

        assertThat(bytes).isNotNull();
        assertThat(bytes).hasSizeGreaterThan(0);

        CloudEvent cloudEvent2 = avroFormat.deserialize(bytes);

        assertThat(cloudEvent2).isNotNull();
        assertThat(cloudEvent2.getId()).isEqualTo("1");
        assertThat(cloudEvent2.getType()).isEqualTo("testdata");
    }

    @Test
    public void testV03Serde() {
        EventFormat avroFormat = new AvroFormat();
        CloudEventData cloudEventData = new AvroCloudEventDataWrapper(testData);

        assertThat(cloudEventData).isNotNull();
        assertThat(cloudEventData.toBytes()).isNotNull();

        CloudEvent cloudEvent = CloudEventBuilder.v03()
            .withId("1")
            .withType("testdata")
            .withSource(URI.create("http://localhost/test"))
            .withData("application/avro", cloudEventData)
            .build();
        assertThat(cloudEvent).isNotNull();
        assertThat(cloudEvent.getSpecVersion()).isEqualTo(SpecVersion.V03);

        byte[] bytes = avroFormat.serialize(cloudEvent);

        assertThat(bytes).isNotNull();
        assertThat(bytes).hasSizeGreaterThan(0);

        CloudEvent cloudEvent2 = avroFormat.deserialize(bytes);

        assertThat(cloudEvent2).isNotNull();
        assertThat(cloudEvent2.getId()).isEqualTo("1");
        assertThat(cloudEvent2.getType()).isEqualTo("testdata");
    }

}
