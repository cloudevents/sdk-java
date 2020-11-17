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
package io.cloudevents.spring.http;

import java.net.URI;

import io.cloudevents.CloudEvent;
import io.cloudevents.CloudEventContext;
import io.cloudevents.core.builder.CloudEventBuilder;
import org.junit.jupiter.api.Test;

import org.springframework.http.HttpHeaders;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * @author Dave Syer
 *
 */
class CloudEventHttpUtilsTests {

	@Test
	void extractHeaders() {
		CloudEvent attributes = CloudEventBuilder.v1().withId("A234-1234-1234")
				.withSource(URI.create("https://spring.io/")).withType("org.springframework").build();
		HttpHeaders headers = CloudEventHttpUtils.toHttp(attributes);
		assertThat(headers.getFirst("ce-id")).isEqualTo("A234-1234-1234");
		assertThat(headers.getFirst("ce-specversion")).isEqualTo("1.0");
		assertThat(headers.getFirst("ce-source")).isEqualTo("https://spring.io/");
		assertThat(headers.getFirst("ce-type")).isEqualTo("org.springframework");
	}

	@Test
	void extractHeadersWithExtensions() {
		CloudEvent attributes = CloudEventBuilder.v1().withId("A234-1234-1234")
				.withSource(URI.create("https://spring.io/")).withType("org.springframework")
				.withExtension("foo", "bar").build();
		HttpHeaders headers = CloudEventHttpUtils.toHttp(attributes);
		assertThat(headers.getFirst("ce-id")).isEqualTo("A234-1234-1234");
		assertThat(headers.getFirst("ce-foo")).isEqualTo("bar");
	}

	@Test
	void roundTripWithExtensions() {
		CloudEvent attributes = CloudEventBuilder.v1().withId("A234-1234-1234")
				.withSource(URI.create("https://spring.io/")).withType("org.springframework")
				.withExtension("foo", "bar").build();
		HttpHeaders headers = CloudEventHttpUtils.toHttp(attributes);
		CloudEventContext output = CloudEventHttpUtils.fromHttp(headers).build();
		// ID is changed on output
		assertThat(output.getId()).isNotNull();
		assertThat(output.getId()).isNotEqualTo("A234-1234-1234");
		assertThat(output.getExtension("foo")).isEqualTo("bar");
	}

}
