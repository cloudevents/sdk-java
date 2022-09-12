package io.cloudevents.nats.impl;

import io.cloudevents.CloudEvent;
import io.cloudevents.core.v1.CloudEventBuilder;
import io.cloudevents.jackson.JsonFormat;
import io.cloudevents.nats.NatsMessageFactory;
import io.cloudevents.types.Time;
import io.nats.client.Message;
import io.nats.client.impl.Headers;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.net.URI;
import java.nio.charset.StandardCharsets;

import static org.assertj.core.api.Assertions.assertThat;

public class NatsMessageWriterTest {
    CloudEventBuilder builder;

    @BeforeEach
    public void setupBuilder() {
        builder = new CloudEventBuilder()
            .withData("application/json", "test-data".getBytes(StandardCharsets.UTF_8))
            .withSubject("subject-v1")
            .withSource(URI.create(NatsHeadersTest.fullUrl))
            .withTime(Time.parseTime(NatsHeadersTest.rawTime))
            .withId("event-id")
            .withType(NatsHeadersTest.emojiRaw)
            .withExtension("testdata", "contents-of-test-data");
        builder.withContextAttribute("sausage", "cumberlands");
    }

    @Test
    public void structuredTest() {
        final CloudEvent event = builder.build();
        final Message natsMessage = NatsMessageFactory
            .createWriter("natsMessage")
            .writeStructured(event, new JsonFormat());

        final Headers headers = natsMessage.getHeaders();
        assertThat(headers.size()).isEqualTo(1);
        assertThat(headers.getFirst(NatsHeaders.CONTENT_TYPE)).isEqualTo("application/cloudevents+json");
        // we don't care what is in it, simply that it is the same
        assertThat(natsMessage.getData()).isEqualTo(new JsonFormat().serialize(event));
    }

    @Test
    public void binaryTest() {
        final Message natsMessage = NatsMessageFactory
            .createWriter("natsMessage")
            .writeBinary(builder.build());

        final Headers headers = natsMessage.getHeaders();

        assertThat(headers.getFirst("ce-subject")).isEqualTo("subject-v1");
        assertThat(headers.getFirst("ce-source")).isEqualTo(NatsHeadersTest.fullUrl);
        assertThat(headers.getFirst("ce-time")).isEqualTo(NatsHeadersTest.rawTime);
        assertThat(headers.getFirst("ce-type")).isEqualTo(NatsHeadersTest.emojiHeader);
        assertThat(headers.getFirst("ce-id")).isEqualTo("event-id");
        assertThat(headers.getFirst("content-type")).isEqualTo("application/json");
        assertThat(headers.getFirst("ce-testdata")).isEqualTo("contents-of-test-data");

        assertThat(new String(natsMessage.getData(), StandardCharsets.UTF_8)).isEqualTo("test-data");
    }
}
