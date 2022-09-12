package io.cloudevents.nats;

import io.cloudevents.CloudEvent;
import io.cloudevents.core.v1.CloudEventBuilder;
import io.cloudevents.jackson.JsonFormat;
import io.cloudevents.nats.impl.NatsHeadersTest;
import io.nats.client.Message;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.net.URI;
import java.nio.charset.StandardCharsets;

import static org.assertj.core.api.Assertions.assertThat;

public class NatsMessageFactoryTest {
    private CloudEvent event;

    @BeforeEach
    public void before() {
        event = new CloudEventBuilder()
            .withData("application/json", "\"test-data\"".getBytes(StandardCharsets.UTF_8))
            .withSource(URI.create(NatsHeadersTest.fullUrl))
            .withId("event-id")
            .withType(NatsHeadersTest.emojiRaw).build();
    }

    private void compare(CloudEvent decoded) {
        assertThat(decoded.getData().toBytes()).isEqualTo(event.getData().toBytes());
        assertThat(decoded.getSource()).isEqualTo(event.getSource());
        assertThat(decoded.getId()).isEqualTo(event.getId());
        assertThat(decoded.getType()).isEqualTo(event.getType());
    }

    @Test
    public void encodeDecodeStructured() {
        Message m = NatsMessageFactory.createWriter("fred").writeStructured(event, new JsonFormat());

        CloudEvent decoded = NatsMessageFactory.createReader(m).toEvent();

        compare(decoded);
    }

    @Test
    public void encodeDecodeBinary() {
        Message m = NatsMessageFactory.createWriter("fred").writeBinary(event);

        CloudEvent decoded = NatsMessageFactory.createReader(m).toEvent();

        compare(decoded);
    }
}
