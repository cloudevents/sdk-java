package io.cloudevents.spring.messaging;

import io.cloudevents.CloudEvent;
import io.cloudevents.CloudEventContext;
import io.cloudevents.SpecVersion;
import io.cloudevents.core.builder.CloudEventBuilder;
import io.cloudevents.rw.CloudEventRWException;
import org.junit.jupiter.api.Test;
import org.springframework.messaging.MessageHeaders;

import java.net.URI;
import java.util.HashMap;
import java.util.Map;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatExceptionOfType;

public class CloudEventContextUtilsTests {

    @Test
    public void testWithEmpty() {
        Map<String, Object> headers = new HashMap<>();
        assertThatExceptionOfType(CloudEventRWException.class).isThrownBy(() -> {
            CloudEventContext attributes = CloudEventContextUtils.fromMap(new MessageHeaders(headers));
            assertThat(attributes.getSpecVersion()).isEqualTo(SpecVersion.V1);
            assertThat(attributes.getId()).isNull();
            assertThat(attributes.getSource()).isNull();
            assertThat(attributes.getType()).isNull();
        });
	}

	@Test
	public void testWithPrefix() {
        Map<String, Object> headers = new HashMap<>();
        headers.put("ce-specversion", "1.0");
        headers.put("ce-id", "A234-1234-1234");
        headers.put("ce-source", "https://spring.io/");
        headers.put("ce-type", "org.springframework");
        headers.put("ce-datacontenttype", "application/json");
        CloudEventContext attributes = CloudEventContextUtils.fromMap(new MessageHeaders(headers));
        assertThat(attributes.getSpecVersion()).isEqualTo(SpecVersion.V1);
        assertThat(attributes.getId()).isEqualTo("A234-1234-1234");
        assertThat(attributes.getSource()).isEqualTo(URI.create("https://spring.io/"));
        assertThat(attributes.getType()).isEqualTo("org.springframework");
        assertThat(attributes.getDataContentType()).isEqualTo("application/json");
    }

	@Test
	public void testExtensionsWithPrefix() {
        Map<String, Object> headers = new HashMap<>();
        headers.put("ce-specversion", "1.0");
        headers.put("ce-id", "A234-1234-1234");
        headers.put("ce-source", "https://spring.io/");
        headers.put("ce-type", "org.springframework");
        headers.put("ce-foo", "bar");
        CloudEventContext attributes = CloudEventContextUtils.fromMap(new MessageHeaders(headers));
        assertThat(attributes.getSpecVersion()).isEqualTo(SpecVersion.V1);
        assertThat(attributes.getId()).isEqualTo("A234-1234-1234");
        assertThat(attributes.getSource()).isEqualTo(URI.create("https://spring.io/"));
        assertThat(attributes.getType()).isEqualTo("org.springframework");
        assertThat(attributes.getExtension("foo")).isEqualTo("bar");
    }

	@Test
	public void testToHeaders() {
        CloudEvent attributes = CloudEventBuilder.v1().withId("A234-1234-1234")
            .withSource(URI.create("https://spring.io/")).withType("org.springframework").build();
        Map<String, ?> headers = CloudEventContextUtils.toMap(attributes);
        assertThat(headers.get("ce-id")).isEqualTo("A234-1234-1234");
        assertThat(headers.get("ce-specversion")).isEqualTo("1.0");
        assertThat(headers.get("ce-source")).isEqualTo("https://spring.io/");
        assertThat(headers.get("ce-type")).isEqualTo("org.springframework");
    }

}
