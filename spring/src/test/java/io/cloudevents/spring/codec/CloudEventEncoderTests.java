/*
 * Copyright 2020-2020 the original author or authors.
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
package io.cloudevents.spring.codec;

import java.net.URI;

import io.cloudevents.CloudEvent;
import io.cloudevents.core.builder.CloudEventBuilder;
import org.junit.jupiter.api.Test;
import reactor.core.publisher.Flux;

import org.springframework.core.ResolvableType;
import org.springframework.core.io.buffer.DataBuffer;
import org.springframework.core.io.buffer.DefaultDataBufferFactory;
import org.springframework.util.MimeTypeUtils;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * @author Dave Syer
 *
 */
class CloudEventEncoderTests {

	@Test
	void canEncodeJson() {
		CloudEventEncoder encoder = new CloudEventEncoder();
		assertThat(encoder.canEncode(ResolvableType.forClass(CloudEvent.class),
				MimeTypeUtils.parseMimeType("application/cloudevents+json"))).isTrue();
	}

	@Test
	void cannotEncodeUnsupported() {
		CloudEventEncoder encoder = new CloudEventEncoder();
		assertThat(encoder.canEncode(ResolvableType.forClass(CloudEvent.class),
				MimeTypeUtils.parseMimeType("application/cloudevents+rubbish")))
						.isFalse();
	}

	@Test
	void doesEncodeJson() {
		CloudEventEncoder encoder = new CloudEventEncoder();
		CloudEvent attributes = CloudEventBuilder.v1().withId("A234-1234-1234")
				.withSource(URI.create("https://spring.io/"))
				.withType("org.springframework").withData("hello".getBytes()).build();
		Flux<DataBuffer> value = encoder.encode(attributes,
				DefaultDataBufferFactory.sharedInstance,
				ResolvableType.forClass(CloudEvent.class),
				MimeTypeUtils.parseMimeType("application/cloudevents+json"), null);
		byte[] array = value.blockLast().asByteBuffer().array();
		String json = new String(array);
		assertThat(json).contains("\"specversion\":\"1.0\"");
	}

}
