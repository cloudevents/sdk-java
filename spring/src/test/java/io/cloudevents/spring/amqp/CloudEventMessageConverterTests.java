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
package io.cloudevents.spring.amqp;

import static org.assertj.core.api.Assertions.*;

import java.net.URI;
import java.nio.charset.StandardCharsets;
import java.util.Map;

import io.cloudevents.CloudEvent;
import io.cloudevents.SpecVersion;
import io.cloudevents.core.builder.CloudEventBuilder;
import io.cloudevents.rw.CloudEventRWException;
import org.junit.jupiter.api.Test;
import org.springframework.amqp.core.Message;
import org.springframework.amqp.core.MessageBuilder;
import org.springframework.amqp.core.MessageProperties;

/**
 * @author Lars Michele
 * @see io.cloudevents.spring.messaging.CloudEventMessageConverterTests used as stencil for the implementation
 */
class CloudEventMessageConverterTests {

    private static final String JSON = "{\"specversion\":\"1.0\"," //
        + "\"id\":\"12345\"," //
        + "\"source\":\"https://spring.io/events\"," //
        + "\"type\":\"io.spring.event\"," //
        + "\"datacontenttype\":\"application/json\"," //
        + "\"data\":{\"value\":\"Dave\"}" //
        + "}";

    private final CloudEventMessageConverter converter = new CloudEventMessageConverter();

    @Test
    void noSpecVersion() {
        Message message = MessageBuilder.withBody(new byte[0]).build();
        assertThatExceptionOfType(CloudEventRWException.class).isThrownBy(() -> {
            assertThat(converter.fromMessage(message)).isNull();
        });
    }

    @Test
    void notValidCloudEvent() {
        Message message = MessageBuilder.withBody(new byte[0]).setHeader("cloudEvents_specversion", "1.0").build();
        assertThatExceptionOfType(IllegalStateException.class).isThrownBy(() -> {
            assertThat(converter.fromMessage(message)).isNull();
        });
    }

    @Test
    void validCloudEvent() {
        Message message = MessageBuilder.withBody(new byte[0]).setHeader("cloudEvents_specversion", "1.0")
            .setHeader("cloudEvents_id", "12345").setHeader("cloudEvents_source", "https://spring.io/events")
            .setHeader("cloudEvents_type", "io.spring.event").build();
        CloudEvent event = converter.fromMessage(message);
        assertThat(event).isNotNull();
        assertThat(event.getSpecVersion()).isEqualTo(SpecVersion.V1);
        assertThat(event.getId()).isEqualTo("12345");
        assertThat(event.getSource()).isEqualTo(URI.create("https://spring.io/events"));
        assertThat(event.getType()).isEqualTo("io.spring.event");
    }

    @Test
    void structuredCloudEvent() {
        byte[] payload = JSON.getBytes(StandardCharsets.UTF_8);
        Message message = MessageBuilder.withBody(payload)
            .setContentType("application/cloudevents+json").build();
        CloudEvent event = converter.fromMessage(message);
        assertThat(event).isNotNull();
        assertThat(event.getSpecVersion()).isEqualTo(SpecVersion.V1);
        assertThat(event.getId()).isEqualTo("12345");
        assertThat(event.getSource()).isEqualTo(URI.create("https://spring.io/events"));
        assertThat(event.getType()).isEqualTo("io.spring.event");
    }

    @Test
    void fromCloudEvent() {
        CloudEvent attributes = CloudEventBuilder.v1().withId("A234-1234-1234")
            .withSource(URI.create("https://spring.io/")).withType("org.springframework")
            .withData("hello".getBytes(StandardCharsets.UTF_8)).build();
        Message message = converter.toMessage(attributes, new MessageProperties());
        Map<String, ?> headers = message.getMessageProperties().getHeaders();
        assertThat(headers.get("cloudEvents_id")).isEqualTo("A234-1234-1234");
        assertThat(headers.get("cloudEvents_specversion")).isEqualTo("1.0");
        assertThat(headers.get("cloudEvents_source")).isEqualTo("https://spring.io/");
        assertThat(headers.get("cloudEvents_type")).isEqualTo("org.springframework");
        assertThat("hello".getBytes(StandardCharsets.UTF_8)).isEqualTo(message.getBody());
    }

    @Test
    void fromNonCloudEvent() {
        assertThat(converter.toMessage(new byte[0], new MessageProperties())).isNull();
    }
}
