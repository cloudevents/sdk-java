package io.cloudevents.examples.nats;

import io.nats.client.Connection;
import io.nats.client.Nats;
import io.nats.client.Options;

import java.io.IOException;

public class SampleNATSConnector {

    public final Connection nats;
    public final String subject;

    public SampleNATSConnector(String[] args) throws IOException, InterruptedException {
        String natsUrls;

        if (args.length > 0) {
            natsUrls = args[0];
        } else {
            natsUrls = System.getenv("NATS_URLS");
        }

        subject = System.getenv("NATS_SUBJECT") == null ? "nats-cloud-events" : System.getenv("NATS_SUBJECT");

        final Options natsOpts = new Options.Builder().server(natsUrls == null ? "nats://localhost:4222" : natsUrls).build();

        nats = Nats.connect(natsOpts);
    }
}
