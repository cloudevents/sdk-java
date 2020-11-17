package io.cloudevents.spring.messaging;

import java.net.URI;
import java.util.Map;

import io.cloudevents.CloudEvent;
import io.cloudevents.core.v1.CloudEventBuilder;
import io.cloudevents.spring.core.CloudEventHeaderUtils;
import org.junit.jupiter.api.Test;

import org.springframework.messaging.Message;
import org.springframework.messaging.MessageHeaders;
import org.springframework.messaging.converter.MappingJackson2MessageConverter;
import org.springframework.messaging.support.MessageBuilder;

import static org.assertj.core.api.Assertions.assertThat;

public class CloudEventMessageUtilsTests {

	String payloadWithHttpPrefix = "{\n" + "    \"ce-specversion\" : \"1.0\",\n"
			+ "    \"ce-type\" : \"org.springframework\",\n" + "    \"ce-source\" : \"https://spring.io/\",\n"
			+ "    \"ce-id\" : \"A234-1234-1234\",\n" + "    \"ce-datacontenttype\" : \"application/json\",\n"
			+ "    \"ce-data\" : {\n" + "        \"version\" : \"1.0\",\n"
			+ "        \"releaseName\" : \"Spring Framework\",\n" + "        \"releaseDate\" : \"24-03-2004\"\n"
			+ "    }\n" + "}";

	String payloadNoPrefix = "{\n" + "    \"specversion\" : \"1.0\",\n" + "    \"type\" : \"org.springframework\",\n"
			+ "    \"source\" : \"https://spring.io/\",\n" + "    \"id\" : \"A234-1234-1234\",\n"
			+ "    \"datacontenttype\" : \"application/json\",\n" + "    \"data\" : {\n"
			+ "        \"version\" : \"1.0\",\n" + "        \"releaseName\" : \"Spring Framework\",\n"
			+ "        \"releaseDate\" : \"24-03-2004\"\n" + "    }\n" + "}";

	String payloadNoDataContentType = "{\n" + "    \"ce_specversion\" : \"1.0\",\n"
			+ "    \"ce_type\" : \"org.springframework\",\n" + "    \"ce_source\" : \"https://spring.io/\",\n"
			+ "    \"ce_id\" : \"A234-1234-1234\",\n" + "    \"ce_datacontenttype\" : \"application/json\",\n"
			+ "    \"data\" : {\n" + "        \"version\" : \"1.0\",\n"
			+ "        \"releaseName\" : \"Spring Framework\",\n" + "        \"releaseDate\" : \"24-03-2004\"\n"
			+ "    }\n" + "}";

	@Test
	public void testGenerateAttributes() {
		Message<String> message = MessageBuilder.withPayload("Hello")
				.copyHeaders(CloudEventHeaderUtils.toMap(new CloudEventBuilder().withId("A234-1234-1234")
						.withSource(URI.create("https://spring.io/")).withType("org.springframework").build(), "ce_"))
				.build();
		CloudEvent attributes = CloudEventMessageUtils.getOutputAttributes(message, attrs -> attrs).build();
		assertThat(attributes.getId()).isNotEqualTo("A234-1234-1234");
		assertThat(attributes.getSource()).isEqualTo(URI.create("https://spring.io/"));
		assertThat(attributes.getType()).isEqualTo(String.class.getName());
	}

	@SuppressWarnings("unchecked")
	@Test
	public void testStructuredToBinaryWithPrefix() {
		Message<String> structuredMessage = MessageBuilder.withPayload(payloadWithHttpPrefix)
				.setHeader(MessageHeaders.CONTENT_TYPE,
						CloudEventHeaderUtils.APPLICATION_CLOUDEVENTS_VALUE + "+json")
				.setHeader("foo", "bar").build();

		MappingJackson2MessageConverter converter = new MappingJackson2MessageConverter();
		Message<Map<String, Object>> binaryMessage = (Message<Map<String, Object>>) CloudEventMessageUtils
				.toBinary(structuredMessage, converter);
		CloudEvent attributes = CloudEventHeaderUtils.fromMap(binaryMessage.getHeaders()).build();
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
						CloudEventHeaderUtils.APPLICATION_CLOUDEVENTS_VALUE + "+json")
				.setHeader("user-agent", "oleg").build();

		MappingJackson2MessageConverter converter = new MappingJackson2MessageConverter();
		Message<Map<String, Object>> binaryMessage = (Message<Map<String, Object>>) CloudEventMessageUtils
				.toBinary(structuredMessage, converter);
		CloudEvent attributes = CloudEventHeaderUtils.fromMap(binaryMessage.getHeaders()).build();
		assertThat(attributes.getId()).isEqualTo("A234-1234-1234");
		assertThat(attributes.getSource()).isEqualTo(URI.create("https://spring.io/"));
		assertThat(attributes.getType()).isEqualTo("org.springframework");
		assertThat(attributes.getDataContentType()).isEqualTo("application/json");
	}

	@SuppressWarnings("unchecked")
	@Test
	public void testStructuredToBinaryNoPrefix() {
		Message<String> structuredMessage = MessageBuilder.withPayload(payloadNoPrefix).setHeader(
				MessageHeaders.CONTENT_TYPE, CloudEventHeaderUtils.APPLICATION_CLOUDEVENTS_VALUE + "+json").build();

		MappingJackson2MessageConverter converter = new MappingJackson2MessageConverter();
		Message<Map<String, Object>> binaryMessage = (Message<Map<String, Object>>) CloudEventMessageUtils
				.toBinary(structuredMessage, converter);
		CloudEvent attributes = CloudEventHeaderUtils.fromMap(binaryMessage.getHeaders()).build();
		assertThat(attributes.getId()).isEqualTo("A234-1234-1234");
		assertThat(attributes.getSource()).isEqualTo(URI.create("https://spring.io/"));
		assertThat(attributes.getType()).isEqualTo("org.springframework");
		assertThat(attributes.getDataContentType()).isEqualTo("application/json");
	}

	@SuppressWarnings("unchecked")
	@Test
	public void testStructuredToBinaryNoDataContentType() {
		Message<String> structuredMessage = MessageBuilder.withPayload(payloadNoPrefix).setHeader(
				MessageHeaders.CONTENT_TYPE, CloudEventHeaderUtils.APPLICATION_CLOUDEVENTS_VALUE + "+json").build();

		MappingJackson2MessageConverter converter = new MappingJackson2MessageConverter();
		Message<Map<String, Object>> binaryMessage = (Message<Map<String, Object>>) CloudEventMessageUtils
				.toBinary(structuredMessage, converter);
		CloudEvent attributes = CloudEventHeaderUtils.fromMap(binaryMessage.getHeaders()).build();
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
				MessageHeaders.CONTENT_TYPE, CloudEventHeaderUtils.APPLICATION_CLOUDEVENTS_VALUE + "+json").build();

		Message<Map<String, Object>> binaryMessage = (Message<Map<String, Object>>) CloudEventMessageUtils
				.toBinary(structuredMessage, converter);
		assertThat(binaryMessage.getHeaders().containsKey("ce-data")).isFalse();
		CloudEvent attributes = CloudEventHeaderUtils.fromMap(binaryMessage.getHeaders()).build();

		Map headers = CloudEventHeaderUtils.toMap(attributes, CloudEventHeaderUtils.DEFAULT_ATTR_PREFIX);
		assertThat(headers.get(CloudEventHeaderUtils.DEFAULT_ATTR_PREFIX + CloudEventHeaderUtils.ID))
				.isEqualTo("A234-1234-1234");
		assertThat(headers.get(CloudEventHeaderUtils.DEFAULT_ATTR_PREFIX + CloudEventHeaderUtils.SOURCE))
				.isEqualTo("https://spring.io/");
		assertThat(headers.get(CloudEventHeaderUtils.DEFAULT_ATTR_PREFIX + CloudEventHeaderUtils.TYPE))
				.isEqualTo("org.springframework");
		assertThat(headers.get(CloudEventHeaderUtils.DEFAULT_ATTR_PREFIX + CloudEventHeaderUtils.DATACONTENTTYPE))
				.isEqualTo("application/json");

		structuredMessage = MessageBuilder.withPayload(payloadNoPrefix).setHeader(MessageHeaders.CONTENT_TYPE,
				CloudEventHeaderUtils.APPLICATION_CLOUDEVENTS_VALUE + "+json").build();

		binaryMessage = (Message<Map<String, Object>>) CloudEventMessageUtils.toBinary(structuredMessage, converter);
		assertThat(binaryMessage.getHeaders().containsKey("data")).isFalse();
		attributes = CloudEventHeaderUtils.fromMap(binaryMessage.getHeaders()).build();

		headers = CloudEventHeaderUtils.toMap(attributes, CloudEventHeaderUtils.HTTP_ATTR_PREFIX);
		assertThat(headers.get(CloudEventHeaderUtils.HTTP_ATTR_PREFIX + CloudEventHeaderUtils.ID))
				.isEqualTo("A234-1234-1234");
		assertThat(headers.get(CloudEventHeaderUtils.HTTP_ATTR_PREFIX + CloudEventHeaderUtils.SOURCE))
				.isEqualTo("https://spring.io/");
		assertThat(headers.get(CloudEventHeaderUtils.HTTP_ATTR_PREFIX + CloudEventHeaderUtils.TYPE))
				.isEqualTo("org.springframework");
		assertThat(headers.get(CloudEventHeaderUtils.HTTP_ATTR_PREFIX + CloudEventHeaderUtils.DATACONTENTTYPE))
				.isEqualTo("application/json");
	}

}
