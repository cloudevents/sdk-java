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
package io.cloudevents.spring.messaging;

import java.net.URI;
import java.util.Collections;
import java.util.Map;

import io.cloudevents.CloudEvent;
import io.cloudevents.SpecVersion;
import io.cloudevents.core.builder.CloudEventBuilder;
import io.cloudevents.rw.CloudEventRWException;
import org.junit.jupiter.api.Test;

import org.springframework.messaging.Message;
import org.springframework.messaging.MessageHeaders;
import org.springframework.messaging.support.MessageBuilder;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatExceptionOfType;

/**
 * @author Dave Syer
 *
 */
class CloudEventMessageConverterTests {

	private static final String JSON = "{\"specversion\":\"1.0\"," //
			+ "\"id\":\"12345\"," //
			+ "\"source\":\"https://spring.io/events\"," //
			+ "\"type\":\"io.spring.event\"," //
			+ "\"datacontenttype\":\"application/json\"," //
			+ "\"data\":{\"value\":\"Dave\"}" //
			+ "}";

	private CloudEventMessageConverter converter = new CloudEventMessageConverter();

	@Test
	void noSpecVersion() {
		Message<?> message = MessageBuilder.withPayload(new byte[0]).build();
		assertThatExceptionOfType(CloudEventRWException.class).isThrownBy(() -> {
			assertThat(converter.fromMessage(message, CloudEvent.class)).isNull();
		});
	}

	@Test
	void notValidCloudEvent() {
		Message<?> message = MessageBuilder.withPayload(new byte[0]).setHeader("ce-specversion", "1.0").build();
		assertThatExceptionOfType(IllegalStateException.class).isThrownBy(() -> {
			assertThat(converter.fromMessage(message, CloudEvent.class)).isNull();
		});
	}

	@Test
	void targetNotCloudEvent() {
		Message<?> message = MessageBuilder.withPayload(new byte[0]).build();
		assertThat(converter.fromMessage(message, String.class)).isNull();
	}

	@Test
	void validCloudEvent() {
		Message<?> message = MessageBuilder.withPayload(new byte[0]).setHeader("ce-specversion", "1.0")
				.setHeader("ce-id", "12345").setHeader("ce-source", "https://spring.io/events")
				.setHeader("ce-type", "io.spring.event").build();
		CloudEvent event = (CloudEvent) converter.fromMessage(message, CloudEvent.class);
		assertThat(event).isNotNull();
		assertThat(event.getSpecVersion()).isEqualTo(SpecVersion.V1);
		assertThat(event.getId()).isEqualTo("12345");
		assertThat(event.getSource()).isEqualTo(URI.create("https://spring.io/events"));
		assertThat(event.getType()).isEqualTo("io.spring.event");
	}

	@Test
	void structuredCloudEvent() {
		byte[] payload = JSON.getBytes();
		Message<?> message = MessageBuilder.withPayload(payload)
				.setHeader(MessageHeaders.CONTENT_TYPE, "application/cloudevents+json").build();
		CloudEvent event = (CloudEvent) converter.fromMessage(message, CloudEvent.class);
		assertThat(event).isNotNull();
		assertThat(event.getSpecVersion()).isEqualTo(SpecVersion.V1);
		assertThat(event.getId()).isEqualTo("12345");
		assertThat(event.getSource()).isEqualTo(URI.create("https://spring.io/events"));
		assertThat(event.getType()).isEqualTo("io.spring.event");
	}

	@Test
	void structuredCloudEventStringPayload() {
		Message<?> message = MessageBuilder.withPayload(JSON)
				.setHeader(MessageHeaders.CONTENT_TYPE, "application/cloudevents+json").build();
		CloudEvent event = (CloudEvent) converter.fromMessage(message, CloudEvent.class);
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
				.withData("hello".getBytes()).build();
		Message<?> message = converter.toMessage(attributes, new MessageHeaders(Collections.emptyMap()));
		Map<String, ?> headers = message.getHeaders();
		assertThat(headers.get("ce-id")).isEqualTo("A234-1234-1234");
		assertThat(headers.get("ce-specversion")).isEqualTo("1.0");
		assertThat(headers.get("ce-source")).isEqualTo("https://spring.io/");
		assertThat(headers.get("ce-type")).isEqualTo("org.springframework");
		assertThat("hello".getBytes().equals(message.getPayload()));
	}

	@Test
	void fromNonCloudEvent() {
		assertThat(converter.toMessage(new byte[0], new MessageHeaders(Collections.emptyMap()))).isNull();
	}

}
