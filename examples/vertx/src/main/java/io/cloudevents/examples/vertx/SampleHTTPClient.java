package io.cloudevents.examples.vertx;

import io.cloudevents.CloudEvent;
import io.cloudevents.core.builder.CloudEventBuilder;
import io.cloudevents.core.message.MessageReader;
import io.cloudevents.http.vertx.VertxMessageFactory;
import io.cloudevents.jackson.JsonFormat;
import io.vertx.core.Future;
import io.vertx.core.Vertx;
import io.vertx.core.buffer.Buffer;
import io.vertx.ext.web.client.HttpResponse;
import io.vertx.ext.web.client.WebClient;

import java.net.URI;
import java.nio.charset.StandardCharsets;
import java.util.UUID;

public class SampleHTTPClient {

    private static final int NUM_EVENTS = 20;

    public static void main(String[] args) {

        if (args.length != 1) {
            System.err.println("Usage: SampleHTTPClient <event_sink>");
            return;
        }
        final String eventSink = args[0];

        final Vertx vertx = Vertx.vertx();
        final WebClient webClient = WebClient.create(vertx);

        // Create an event template to set basic CloudEvent attributes.
        CloudEventBuilder eventTemplate = CloudEventBuilder.v1()
            .withSource(URI.create("https://github.com/cloudevents/sdk-java/tree/master/examples/vertx"))
            .withType("vertx.example");

        // Send NUM_EVENTS events.
        for (int i = 0; i < NUM_EVENTS; i++) {
            String data = "Event number " + i;

            // Create the event starting from the template
            final CloudEvent event = eventTemplate.newBuilder()
                .withId(UUID.randomUUID().toString())
                .withData("text/plain", data.getBytes(StandardCharsets.UTF_8))
                .build();

            Future<HttpResponse<Buffer>> responseFuture;
            // We need to write the event to the request, so create a MessageWriter.
            if (i % 2 == 0) {
                responseFuture = VertxMessageFactory.createWriter(webClient.postAbs(eventSink))
                    .writeBinary(event); // Use binary mode.
            } else {
                responseFuture = VertxMessageFactory.createWriter(webClient.postAbs(eventSink))
                    .writeStructured(event, JsonFormat.CONTENT_TYPE); // Use structured mode.
            }
            responseFuture
                .map(VertxMessageFactory::createReader) // Let's convert the response to message reader...
                .map(MessageReader::toEvent) // ...then to event
                .onSuccess(System.out::println) // Print the received message
                .onFailure(System.err::println); // Print the eventual failure
        }
    }
}
