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
import io.cloudevents.core.message.Encoding;
import io.cloudevents.core.message.Message;
import io.cloudevents.core.mock.CSVFormat;
import io.cloudevents.core.provider.EventFormatProvider;
import io.cloudevents.core.test.Data;
import org.apache.kafka.common.header.Headers;
import org.apache.kafka.common.header.internals.RecordHeaders;
import org.junit.jupiter.api.Test;

import java.util.HashMap;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatCode;

public class CloudEventSerializerTest {

    @Test
    public void serializerWithDefaultsShouldWork() {
        CloudEventSerializer serializer = new CloudEventSerializer();

        assertProducesMessagesWithEncoding(serializer, Encoding.BINARY);
    }

    @Test
    public void serializerNotKey() {
        CloudEventSerializer serializer = new CloudEventSerializer();

        assertThatCode(() -> serializer.configure(new HashMap<>(), true))
            .isInstanceOf(IllegalArgumentException.class);
    }

    @Test
    public void serializerWithEncodingBinary() {
        CloudEventSerializer serializer = new CloudEventSerializer();

        HashMap<String, Object> config = new HashMap<>();
        config.put(CloudEventSerializer.ENCODING_CONFIG, "BINARY");

        assertThatCode(() -> serializer.configure(config, false))
            .doesNotThrowAnyException();

        assertProducesMessagesWithEncoding(serializer, Encoding.BINARY);
    }

    @Test
    public void serializerWithEncodingStructuredWithoutEventFormat() {
        CloudEventSerializer serializer = new CloudEventSerializer();

        HashMap<String, Object> config = new HashMap<>();
        config.put(CloudEventSerializer.ENCODING_CONFIG, "STRUCTURED");

        assertThatCode(() -> serializer.configure(config, false))
            .isInstanceOf(IllegalArgumentException.class);
    }

    @Test
    public void serializerWithEncodingStructuredAndFormat() {
        CloudEventSerializer serializer = new CloudEventSerializer();

        EventFormatProvider.getInstance().registerFormat(CSVFormat.INSTANCE);

        HashMap<String, Object> config = new HashMap<>();
        config.put(CloudEventSerializer.ENCODING_CONFIG, "STRUCTURED");
        config.put(CloudEventSerializer.EVENT_FORMAT_CONFIG, CSVFormat.INSTANCE.serializedContentType());

        assertThatCode(() -> serializer.configure(config, false))
            .doesNotThrowAnyException();

        assertProducesMessagesWithEncoding(serializer, Encoding.STRUCTURED);
    }

    private void assertProducesMessagesWithEncoding(CloudEventSerializer serializer, Encoding expectedEncoding) {
        String topic = "test";
        CloudEvent event = Data.V1_MIN;

        Headers headers = new RecordHeaders();
        byte[] payload = serializer.serialize(topic, headers, event);

        Message message = KafkaMessageFactory.create(headers, payload);

        assertThat(message.getEncoding())
            .isEqualTo(expectedEncoding);
        assertThat(message.toEvent())
            .isEqualTo(event);
    }

}
