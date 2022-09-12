package io.cloudevents.examples.nats;

import io.cloudevents.CloudEvent;
import io.cloudevents.nats.NatsMessageFactory;

import java.io.IOException;

public class SampleNATSSink {
    public static void main(String[] args) throws IOException, InterruptedException {
        final SampleNATSConnector conn = new SampleNATSConnector(args);

        conn.nats.createDispatcher().subscribe(conn.subject, (msg) -> {
            CloudEvent event = NatsMessageFactory.createReader(msg).toEvent();

            System.out.println(event);
        });

        System.out.println("Waiting for messages");
        Thread.currentThread().join(); // wait
    }
}
