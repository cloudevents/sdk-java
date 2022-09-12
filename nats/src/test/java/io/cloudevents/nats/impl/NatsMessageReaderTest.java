package io.cloudevents.nats.impl;

import io.cloudevents.CloudEvent;
import io.cloudevents.SpecVersion;
import io.cloudevents.nats.NatsMessageFactory;
import io.nats.client.impl.Headers;
import org.junit.jupiter.api.Test;

import java.nio.charset.StandardCharsets;
import java.util.HashMap;
import java.util.Map;

import static org.assertj.core.api.Assertions.assertThat;

public class NatsMessageReaderTest {
    @Test
    public void basicReaderTest() {
        byte[] body = "hello-world".getBytes(StandardCharsets.UTF_8);
        Headers headers = new Headers();
        headers.put(NatsHeaders.SPEC_VERSION, SpecVersion.V1.toString());
        headers.put(NatsHeaders.CONTENT_TYPE, "application/json");
        headers.put(NatsHeaders.headerMapper("subject"), "subject-test");
        headers.put(NatsHeaders.headerMapper("id"), "hello-id");
        headers.put(NatsHeaders.headerMapper("type"), "hello/v1");
        headers.put(NatsHeaders.headerMapper("test"), "test-value");
        headers.put(NatsHeaders.headerMapper("source"), "plum");

        NatsMessageReaderImpl reader = new NatsMessageReaderImpl(SpecVersion.V1, headers, body);
        assertThat(reader.isCloudEventsHeader(NatsHeaders.CE_EVENT_FORMAT + "x")).isTrue();
        assertThat(reader.isCloudEventsHeader("burp")).isFalse();
        assertThat(reader.isContentTypeHeader("burp")).isFalse();
        assertThat(reader.isContentTypeHeader(NatsHeaders.CONTENT_TYPE)).isTrue();
        assertThat(reader.toCloudEventsKey(NatsHeaders.CE_PREFIX + "k")).isEqualTo("k");

        Map<String, String> data = new HashMap<>();
        reader.forEachHeader((key, value) -> data.put(key, new String(value)));

        assertThat(data.size()).isEqualTo(7);

        assertThat(data.get(NatsHeaders.headerMapper("test"))).isEqualTo("test-value");
        assertThat(data.get(NatsHeaders.CONTENT_TYPE)).isEqualTo("application/json");

        CloudEvent cloudEvent = NatsMessageFactory.createReader(headers, body).toEvent();

        assertThat(cloudEvent.getSubject()).isEqualTo("subject-test");
        assertThat(cloudEvent.getId()).isEqualTo("hello-id");
        assertThat(cloudEvent.getType()).isEqualTo("hello/v1");
        assertThat(cloudEvent.getExtension("test")).isEqualTo("test-value");

    }
}
