/*
 * Copyright 2020-Present The CloudEvents Authors
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
import java.util.Map;

import io.cloudevents.core.v1.CloudEventBuilder;
import io.cloudevents.spring.core.CloudEventAttributeUtils;
import io.cloudevents.spring.core.MutableCloudEventAttributes;
import org.junit.jupiter.api.Test;

import org.springframework.messaging.Message;
import org.springframework.messaging.MessageHeaders;
import org.springframework.messaging.converter.MappingJackson2MessageConverter;
import org.springframework.messaging.support.MessageBuilder;

import static org.assertj.core.api.Assertions.assertThat;

/**
 *
 * @author Oleg Zhurakousky
 *
 */
public class CloudEventMessageUtilsTests {

	String payloadWithHttpPrefix = "{\n" +
	        "    \"ce-specversion\" : \"1.0\",\n" +
	        "    \"ce-type\" : \"org.springframework\",\n" +
	        "    \"ce-source\" : \"https://spring.io/\",\n" +
	        "    \"ce-id\" : \"A234-1234-1234\",\n" +
	        "    \"ce-datacontenttype\" : \"application/json\",\n" +
	        "    \"ce-data\" : {\n" +
	        "        \"version\" : \"1.0\",\n" +
	        "        \"releaseName\" : \"Spring Framework\",\n" +
	        "        \"releaseDate\" : \"24-03-2004\"\n" +
	        "    }\n" +
	        "}";

	String payloadNoPrefix = "{\n" +
	        "    \"specversion\" : \"1.0\",\n" +
	        "    \"type\" : \"org.springframework\",\n" +
	        "    \"source\" : \"https://spring.io/\",\n" +
	        "    \"id\" : \"A234-1234-1234\",\n" +
	        "    \"datacontenttype\" : \"application/json\",\n" +
	        "    \"data\" : {\n" +
	        "        \"version\" : \"1.0\",\n" +
	        "        \"releaseName\" : \"Spring Framework\",\n" +
	        "        \"releaseDate\" : \"24-03-2004\"\n" +
	        "    }\n" +
	        "}";

	String payloadNoDataContentType = "{\n" +
	        "    \"ce_specversion\" : \"1.0\",\n" +
	        "    \"ce_type\" : \"org.springframework\",\n" +
	        "    \"ce_source\" : \"https://spring.io/\",\n" +
	        "    \"ce_id\" : \"A234-1234-1234\",\n" +
	        "    \"ce_datacontenttype\" : \"application/json\",\n" +
	        "    \"data\" : {\n" +
	        "        \"version\" : \"1.0\",\n" +
	        "        \"releaseName\" : \"Spring Framework\",\n" +
	        "        \"releaseDate\" : \"24-03-2004\"\n" +
	        "    }\n" +
	        "}";

	@Test
	public void testGenerateAttributes() {
		Message<String> message = MessageBuilder.withPayload("Hello")
				.copyHeaders(CloudEventAttributeUtils
						.toMutable(new CloudEventBuilder().withId("A234-1234-1234")
								.withSource(URI.create("https://spring.io/")).withType("org.springframework").build())
						.toMap("ce_"))
				.build();
		MutableCloudEventAttributes attributes = CloudEventMessageUtils.getOutputAttributes(message, attrs -> attrs);
		assertThat(attributes.getId()).isNotEqualTo("A234-1234-1234");
		assertThat(attributes.getSource()).isEqualTo(URI.create("https://spring.io/"));
		assertThat(attributes.getType()).isEqualTo(String.class.getName());
	}

	@SuppressWarnings("unchecked")
	@Test
	public void testStructuredToBinaryWithPrefix() {
		Message<String> structuredMessage = MessageBuilder.withPayload(payloadWithHttpPrefix)
				.setHeader(MessageHeaders.CONTENT_TYPE,
						CloudEventAttributeUtils.APPLICATION_CLOUDEVENTS_VALUE + "+json")
				.setHeader("foo", "bar").build();

		MappingJackson2MessageConverter converter = new MappingJackson2MessageConverter();
		Message<Map<String, Object>> binaryMessage = (Message<Map<String, Object>>) CloudEventMessageUtils
				.toBinary(structuredMessage, converter);
		MutableCloudEventAttributes attributes = CloudEventAttributeUtils.toAttributes(binaryMessage.getHeaders());
		assertThat(attributes.getId()).isEqualTo("A234-1234-1234");
		assertThat(attributes.getSource()).isEqualTo(URI.create("https://spring.io/"));
		assertThat(attributes.getType()).isEqualTo("org.springframework");
		assertThat(attributes.getDataContentType()).isEqualTo("application/json");
		assertThat(binaryMessage.getHeaders().get("foo")).isEqualTo("bar");
	}

	@SuppressWarnings("unchecked")
	@Test
	public void testStructuredToBinaryWithPrefixAndUserAgent() {
		Message<String> structuredMessage = MessageBuilder.withPayload(payloadWithHttpPrefix)
				.setHeader(MessageHeaders.CONTENT_TYPE,
						CloudEventAttributeUtils.APPLICATION_CLOUDEVENTS_VALUE + "+json")
				.setHeader("user-agent", "oleg").build();

		MappingJackson2MessageConverter converter = new MappingJackson2MessageConverter();
		Message<Map<String, Object>> binaryMessage = (Message<Map<String, Object>>) CloudEventMessageUtils
				.toBinary(structuredMessage, converter);
		MutableCloudEventAttributes attributes = CloudEventAttributeUtils.toAttributes(binaryMessage.getHeaders());
		assertThat(attributes.getId()).isEqualTo("A234-1234-1234");
		assertThat(attributes.getSource()).isEqualTo(URI.create("https://spring.io/"));
		assertThat(attributes.getType()).isEqualTo("org.springframework");
		assertThat(attributes.getDataContentType()).isEqualTo("application/json");
	}

	@SuppressWarnings("unchecked")
	@Test
	public void testStructuredToBinaryNoPrefix() {
		Message<String> structuredMessage = MessageBuilder.withPayload(payloadNoPrefix).setHeader(
				MessageHeaders.CONTENT_TYPE, CloudEventAttributeUtils.APPLICATION_CLOUDEVENTS_VALUE + "+json").build();

		MappingJackson2MessageConverter converter = new MappingJackson2MessageConverter();
		Message<Map<String, Object>> binaryMessage = (Message<Map<String, Object>>) CloudEventMessageUtils
				.toBinary(structuredMessage, converter);
		MutableCloudEventAttributes attributes = CloudEventAttributeUtils.toAttributes(binaryMessage.getHeaders());
		assertThat(attributes.getId()).isEqualTo("A234-1234-1234");
		assertThat(attributes.getSource()).isEqualTo(URI.create("https://spring.io/"));
		assertThat(attributes.getType()).isEqualTo("org.springframework");
		assertThat(attributes.getDataContentType()).isEqualTo("application/json");
	}

	@SuppressWarnings("unchecked")
	@Test
	public void testStructuredToBinaryNoDataContentType() {
		Message<String> structuredMessage = MessageBuilder.withPayload(payloadNoPrefix).setHeader(
				MessageHeaders.CONTENT_TYPE, CloudEventAttributeUtils.APPLICATION_CLOUDEVENTS_VALUE + "+json").build();

		MappingJackson2MessageConverter converter = new MappingJackson2MessageConverter();
		Message<Map<String, Object>> binaryMessage = (Message<Map<String, Object>>) CloudEventMessageUtils
				.toBinary(structuredMessage, converter);
		MutableCloudEventAttributes attributes = CloudEventAttributeUtils.toAttributes(binaryMessage.getHeaders());
		assertThat(attributes.getId()).isEqualTo("A234-1234-1234");
		assertThat(attributes.getSource()).isEqualTo(URI.create("https://spring.io/"));
		assertThat(attributes.getType()).isEqualTo("org.springframework");
		assertThat(attributes.getDataContentType()).isEqualTo("application/json");
	}

	@SuppressWarnings({ "rawtypes", "unchecked" })
	@Test
	public void testStructuredToBinaryBackToMessageHeaders() {
		MappingJackson2MessageConverter converter = new MappingJackson2MessageConverter();
		Message<String> structuredMessage = MessageBuilder.withPayload(payloadWithHttpPrefix).setHeader(
				MessageHeaders.CONTENT_TYPE, CloudEventAttributeUtils.APPLICATION_CLOUDEVENTS_VALUE + "+json").build();

		Message<Map<String, Object>> binaryMessage = (Message<Map<String, Object>>) CloudEventMessageUtils
				.toBinary(structuredMessage, converter);
		assertThat(binaryMessage.getHeaders().containsKey("ce-data")).isFalse();
		MutableCloudEventAttributes attributes = CloudEventAttributeUtils.toAttributes(binaryMessage.getHeaders());

		Map headers = attributes.toMap(CloudEventAttributeUtils.DEFAULT_ATTR_PREFIX);
		assertThat(headers.get(CloudEventAttributeUtils.DEFAULT_ATTR_PREFIX + MutableCloudEventAttributes.ID))
				.isEqualTo("A234-1234-1234");
		assertThat(headers.get(CloudEventAttributeUtils.DEFAULT_ATTR_PREFIX + MutableCloudEventAttributes.SOURCE))
				.isEqualTo("https://spring.io/");
		assertThat(headers.get(CloudEventAttributeUtils.DEFAULT_ATTR_PREFIX + MutableCloudEventAttributes.TYPE))
				.isEqualTo("org.springframework");
		assertThat(
				headers.get(CloudEventAttributeUtils.DEFAULT_ATTR_PREFIX + MutableCloudEventAttributes.DATACONTENTTYPE))
						.isEqualTo("application/json");

		structuredMessage = MessageBuilder.withPayload(payloadNoPrefix).setHeader(MessageHeaders.CONTENT_TYPE,
				CloudEventAttributeUtils.APPLICATION_CLOUDEVENTS_VALUE + "+json").build();

		binaryMessage = (Message<Map<String, Object>>) CloudEventMessageUtils.toBinary(structuredMessage, converter);
		assertThat(binaryMessage.getHeaders().containsKey("data")).isFalse();
		attributes = CloudEventAttributeUtils.toAttributes(binaryMessage.getHeaders());

		headers = attributes.toMap(CloudEventAttributeUtils.HTTP_ATTR_PREFIX);
		assertThat(headers.get(CloudEventAttributeUtils.HTTP_ATTR_PREFIX + MutableCloudEventAttributes.ID))
				.isEqualTo("A234-1234-1234");
		assertThat(headers.get(CloudEventAttributeUtils.HTTP_ATTR_PREFIX + MutableCloudEventAttributes.SOURCE))
				.isEqualTo("https://spring.io/");
		assertThat(headers.get(CloudEventAttributeUtils.HTTP_ATTR_PREFIX + MutableCloudEventAttributes.TYPE))
				.isEqualTo("org.springframework");
		assertThat(headers.get(CloudEventAttributeUtils.HTTP_ATTR_PREFIX + MutableCloudEventAttributes.DATACONTENTTYPE))
				.isEqualTo("application/json");
	}

}
