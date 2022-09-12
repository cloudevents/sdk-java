package io.cloudevents.examples.nats;

import io.cloudevents.CloudEvent;
import io.cloudevents.core.builder.CloudEventBuilder;
import io.cloudevents.jackson.JsonFormat;
import io.cloudevents.nats.NatsMessageFactory;

import java.io.IOException;
import java.net.URI;
import java.util.UUID;

public class SampleNATSSource {
    private static final int NUM_EVENTS = 20;

    public static void main(String[] args) throws IOException, InterruptedException {
        final SampleNATSConnector conn = new SampleNATSConnector(args);

        // Create an event template to set basic CloudEvent attributes.
        CloudEventBuilder eventTemplate = CloudEventBuilder.v1()
            .withSource(URI.create("https://github.com/cloudevents/sdk-java/tree/master/examples/nats"))
            .withType("nats.example");

        for (int i = 0; i < NUM_EVENTS; i++) {
            String data = "Event number " + i;

            // Create the event starting from the template
            final CloudEvent event = eventTemplate.newBuilder()
                .withId(UUID.randomUUID().toString())
                .withData("text/plain", data.getBytes())
                .build();

            if (i % 2 == 0) {
                conn.nats.publish(NatsMessageFactory.createWriter(conn.subject).writeBinary(event));
            } else {
                conn.nats.publish(NatsMessageFactory.createWriter(conn.subject).writeStructured(event, JsonFormat.CONTENT_TYPE));
            }
        }
    }
}
