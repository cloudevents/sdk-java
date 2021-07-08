/*
 * Copyright 2019-2019 the original author or authors.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      https://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package io.cloudevents.spring.kafka;

import static org.assertj.core.api.Assertions.assertThat;

import java.net.URI;
import java.util.Map;

import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.junit.jupiter.api.Test;
import org.springframework.messaging.Message;
import org.springframework.messaging.MessageHeaders;

import io.cloudevents.CloudEvent;
import io.cloudevents.SpecVersion;

/**
 * @author Dave Syer
 *
 */
class CloudEventRecordMessageConverterTests {

    private static final String JSON = "{\"specversion\":\"1.0\"," //
            + "\"id\":\"12345\"," //
            + "\"source\":\"https://spring.io/events\"," //
            + "\"type\":\"io.spring.event\"," //
            + "\"datacontenttype\":\"application/json\"," //
            + "\"data\":{\"value\":\"Dave\"}" //
            + "}";

    private CloudEventRecordMessageConverter converter = new CloudEventRecordMessageConverter();

    @Test
    void structuredCloudEventMessage() {
        ConsumerRecord<?, ?> record = new ConsumerRecord<byte[], byte[]>("in", 0, 0, null, JSON.getBytes());
        record.headers().add(MessageHeaders.CONTENT_TYPE, "application/cloudevents+json".getBytes());
        Message<?> message = converter.toMessage(record, null, null, CloudEvent.class);
        assertThat(message).isNotNull();
        CloudEvent event = (CloudEvent) message.getPayload();
        assertThat(event.getSpecVersion()).isEqualTo(SpecVersion.V1);
        assertThat(event.getId()).isEqualTo("12345");
        assertThat(event.getDataContentType()).isEqualTo("application/json");
        assertThat(event.getSource()).isEqualTo(URI.create("https://spring.io/events"));
        assertThat(event.getType()).isEqualTo("io.spring.event");
    }

    @Test
    void binaryCloudEventMessage() {
        ConsumerRecord<?, ?> record = new ConsumerRecord<byte[], byte[]>("in", 0, 0, null, "{\"value\":\"Dave\"}".getBytes());
        record.headers() //
        .add(MessageHeaders.CONTENT_TYPE, "application/json".getBytes()) //
        .add("ce_specversion", "1.0".getBytes()) //
        .add("ce_id", "12345".getBytes()) //
        .add("ce_source", "https://spring.io/events".getBytes()) //
        .add("ce_type", "io.spring.event".getBytes());
        Message<?> message = converter.toMessage(record, null, null, CloudEvent.class);
        assertThat(message).isNotNull();
        CloudEvent event = (CloudEvent) message.getPayload();
        assertThat(event.getSpecVersion()).isEqualTo(SpecVersion.V1);
        assertThat(event.getId()).isEqualTo("12345");
        assertThat(event.getDataContentType()).isEqualTo("application/json");
        assertThat(event.getSource()).isEqualTo(URI.create("https://spring.io/events"));
        assertThat(event.getType()).isEqualTo("io.spring.event");
    }
    @Test
    void binaryNonCloudEventMessage() {
        ConsumerRecord<?, ?> record = new ConsumerRecord<byte[], byte[]>("in", 0, 0, null, "{\"value\":\"Dave\"}".getBytes());
        record.headers() //
        .add(MessageHeaders.CONTENT_TYPE, "application/json".getBytes()) //
        .add("ce_specversion", "1.0".getBytes()) //
        .add("ce_id", "12345".getBytes()) //
        .add("ce_source", "https://spring.io/events".getBytes()) //
        .add("ce_type", "io.spring.event".getBytes());
        Message<?> message = converter.toMessage(record, null, null, String.class);
        assertThat(message).isNotNull();
        // TODO: should it be a String?
        assertThat(message.getPayload()).isEqualTo("{\"value\":\"Dave\"}".getBytes());
        Map<String, ?> headers = message.getHeaders();
        assertThat(headers.get("ce-id")).isEqualTo("12345");
        assertThat(headers.get("ce-specversion")).isEqualTo("1.0");
        assertThat(headers.get("ce-source")).isEqualTo("https://spring.io/events");
        assertThat(headers.get("ce-type")).isEqualTo("io.spring.event");
    }
}
