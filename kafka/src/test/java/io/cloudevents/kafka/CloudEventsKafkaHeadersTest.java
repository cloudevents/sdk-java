package io.cloudevents.kafka;

import io.cloudevents.CloudEvent;
import io.cloudevents.v1.AttributesImpl;
import io.cloudevents.v1.CloudEventBuilder;
import io.cloudevents.v1.kafka.Marshallers;
import org.apache.kafka.common.header.Header;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import java.net.URI;
import java.nio.charset.StandardCharsets;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.stream.Collectors;
import java.util.stream.StreamSupport;

public class CloudEventsKafkaHeadersTest {

    private final CloudEvent<AttributesImpl, String> ce =
        CloudEventBuilder.<String>builder()
            .withId("x10")
            .withSource(URI.create("/source"))
            .withType("event-type")
            .withDataContentType("application/json")
            .build();

    @Test
    public void should_create_headers_given_binary_marshaller() {
        Iterable<Header> headers = CloudEventsKafkaHeaders.buildHeaders(ce, Marshallers.binary());
        assertHeaders(headers, new HashMap<String, String>(){{
            put("ce_id", "x10");
            put("ce_type", "event-type");
            put("ce_specversion", "1.0");
            put("ce_source", "/source");
            put("content-type", "application/json");
        }});
    }

    @Test
    public void should_create_headers_given_structured_marshaller() {
        Iterable<Header> headers = CloudEventsKafkaHeaders.buildHeaders(ce, Marshallers.structured());
        assertHeaders(headers, Collections.singletonMap("content-type", "application/cloudevents+json"));
    }

    private void assertHeaders(final Iterable<Header> headers, final Map<String, String> expected) {
        Map<String, String> values = StreamSupport.stream(headers.spliterator(), false)
                .collect(Collectors.toMap(Header::key, h -> new String(h.value(), StandardCharsets.UTF_8)));

        for (Map.Entry<String, String> expectedHeader : expected.entrySet()) {
            Assertions.assertEquals(expectedHeader.getValue(), values.get(expectedHeader.getKey()));
        }
    }
}