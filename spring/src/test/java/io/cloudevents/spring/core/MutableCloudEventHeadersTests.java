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
package io.cloudevents.spring.core;

import java.net.URI;
import java.util.Collections;
import java.util.Map;

import io.cloudevents.CloudEvent;
import io.cloudevents.SpecVersion;
import io.cloudevents.core.builder.CloudEventBuilder;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * @author Dave Syer
 *
 */
public class MutableCloudEventHeadersTests {

	@Test
	void testEmpty() throws Exception {
		MutableCloudEventHeaders attributes = new MutableCloudEventHeaders(Collections.emptyMap());
		assertThat(attributes.getAttribute(CloudEventHeaderUtils.SPECVERSION)).isEqualTo(SpecVersion.V1);
		assertThat(attributes.getAttribute(CloudEventHeaderUtils.ID)).isNull();
	}

	@Test
	void testSetAttribute() throws Exception {
		CloudEventBuilder builder = new MutableCloudEventHeaders(Collections.emptyMap()).getBuilder();
		builder.withAttribute(CloudEventHeaderUtils.ID, "A1234-1234");
		builder.withSource(URI.create("https://spring.io/"));
		builder.withType("org.springframework");
		CloudEvent attributes = builder.build();
		assertThat(attributes.getSpecVersion()).isEqualTo(SpecVersion.V1);
		assertThat(attributes.getId()).isEqualTo("A1234-1234");
	}

	@Test
	void testV03() throws Exception {
		CloudEventBuilder builder = new MutableCloudEventHeaders(
				Collections.singletonMap(CloudEventHeaderUtils.SPECVERSION, SpecVersion.V03)).getBuilder();
		builder.withAttribute(CloudEventHeaderUtils.ID, "A1234-1234");
		builder.withAttribute(CloudEventHeaderUtils.SCHEMAURL, "https://schema.spring.io/ce-0.3");
		builder.withSource(URI.create("https://spring.io/"));
		builder.withType("org.springframework");
		CloudEvent attributes = builder.build();
		assertThat(attributes.getSpecVersion()).isEqualTo(SpecVersion.V03);
		assertThat(attributes.getId()).isEqualTo("A1234-1234");
		assertThat(attributes.getDataSchema().toString()).isEqualTo("https://schema.spring.io/ce-0.3");
	}

	@Test
	void testV03MapWithExplicitSchema() throws Exception {
		CloudEventBuilder attributes = new MutableCloudEventHeaders(
				Collections.singletonMap(CloudEventHeaderUtils.SPECVERSION, SpecVersion.V03)).getBuilder();
		attributes.withId("A1234-1234");
		attributes.withSource(URI.create("https://spring.io/"));
		attributes.withType("org.springframework");
		attributes.withDataSchema(URI.create("https://schema.spring.io/ce-0.3"));
		Map<String, Object> headers = CloudEventHeaderUtils.toMap(attributes.build(), "ce-");
		assertThat(headers.get("ce-specversion")).isEqualTo("0.3");
		assertThat(headers.get("ce-source")).isEqualTo("https://spring.io/");
		assertThat(headers.get("ce-type")).isEqualTo("org.springframework");
		assertThat(headers.get("ce-schemaurl")).isEqualTo("https://schema.spring.io/ce-0.3");
	}

	@Test
	void testV03MapWithAttributeSchema() throws Exception {
		CloudEventBuilder attributes = new MutableCloudEventHeaders(
				Collections.singletonMap(CloudEventHeaderUtils.SPECVERSION, SpecVersion.V03)).getBuilder();
		attributes.withId("A1234-1234");
		attributes.withSource(URI.create("https://spring.io/"));
		attributes.withType("org.springframework");
		attributes.withAttribute(CloudEventHeaderUtils.SCHEMAURL, "https://schema.spring.io/ce-0.3");
		Map<String, Object> headers = CloudEventHeaderUtils.toMap(attributes.build(), "ce-");
		assertThat(headers.get("ce-specversion")).isEqualTo("0.3");
		assertThat(headers.get("ce-source")).isEqualTo("https://spring.io/");
		assertThat(headers.get("ce-type")).isEqualTo("org.springframework");
		assertThat(headers.get("ce-schemaurl")).isEqualTo("https://schema.spring.io/ce-0.3");
	}

}
