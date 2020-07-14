package io.cloudevents.examples.vertx;

import io.cloudevents.CloudEvent;
import io.cloudevents.core.builder.CloudEventBuilder;
import io.cloudevents.core.message.MessageReader;
import io.cloudevents.http.vertx.VertxMessageFactory;
import io.cloudevents.jackson.Json;
import io.vertx.core.Vertx;
import io.vertx.core.http.HttpClient;
import io.vertx.core.http.HttpClientRequest;

import java.net.URI;
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
        final HttpClient httpClient = vertx.createHttpClient();

        // Create an event template to set basic CloudEvent attributes.
        CloudEventBuilder eventTemplate = CloudEventBuilder.v1()
            .withSource(URI.create("https://github.com/cloudevents/sdk-java/tree/master/examples/vertx"))
            .withType("vertx.example");

        // Send NUM_EVENTS events.
        for (int i = 0; i < NUM_EVENTS; i++) {

            // create HTTP request.
            final HttpClientRequest request = httpClient.postAbs(eventSink)
                .handler(response -> {

                    // We need to read the event from the HTTP response we get, so create a MessageReader.
                    VertxMessageFactory.createReader(response)
                        // Covert the MessageReader to a CloudEvent.
                        .map(MessageReader::toEvent)
                        // Print out the event.
                        .onSuccess(System.out::println)
                        .onFailure(System.err::println);

                })
                .exceptionHandler(System.err::println);

            String id = UUID.randomUUID().toString();
            String data = "Event number " + i;

            // Create the event starting from the template
            final CloudEvent event = eventTemplate.newBuilder()
                .withId(id)
                .withData("text/plain", data.getBytes())
                .build();

            // We need to write the event to the request, so create a MessageWriter.
            if (i % 2 == 0) {
                VertxMessageFactory.createWriter(request)
                    .writeBinary(event); // Use binary mode.
            } else {
                VertxMessageFactory.createWriter(request)
                    .writeStructured(event, new Json()); // Use structured mode.
            }
        }
    }
}
