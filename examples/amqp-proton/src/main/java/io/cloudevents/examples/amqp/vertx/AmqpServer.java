package io.cloudevents.examples.amqp.vertx;

import java.io.PrintWriter;
import java.net.URI;
import java.nio.charset.StandardCharsets;
import java.time.OffsetDateTime;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.apache.qpid.proton.message.Message;

import io.cloudevents.CloudEvent;
import io.cloudevents.amqp.ProtonAmqpMessageFactory;
import io.cloudevents.core.builder.CloudEventBuilder;
import io.cloudevents.core.message.MessageReader;
import io.cloudevents.core.message.MessageWriter;
import io.vertx.core.Vertx;
import io.vertx.proton.ProtonConnection;
import io.vertx.proton.ProtonServer;
import io.vertx.proton.ProtonServerOptions;

/**
 * An example vertx-based AMQP server that receives and sends CloudEvent messages to/from a remote client.
 */
public class AmqpServer {

    private static final String DEFAULT_HOST = "127.0.0.1";
    private static final int DEFAULT_PORT = 5672;

    private static PrintWriter writer = new PrintWriter(System.out, true);

    public static void main(String argv[]) {
        final Vertx vertx = Vertx.vertx();

        final List<String> args = new ArrayList<>();
        if (argv.length > 1) {
            args.addAll(Arrays.asList(argv[0].split(":", 1)));            
        }

        final String host = args.isEmpty() ? DEFAULT_HOST : args.get(0);
        final int port = args.isEmpty() ? DEFAULT_PORT : Integer.parseInt(args.get(1));

        final ProtonServerOptions options = new ProtonServerOptions();
        options.setHost(host);
        options.setPort(port);

        final ProtonServer server = ProtonServer.create(vertx, options);
        server.connectHandler(con -> onConnectRequest(con)).listen(ar -> {
            if (ar.succeeded()) {
                writer.printf("[Server] started and listening on %s:%s\n", host, port);
            } else {
                writer.printf("[Server] failed to start (%s)\n", ar.cause());
                System.exit(1);
            }
        });
    }

    private static void onConnectRequest(final ProtonConnection con) {
        // BEGIN frame received
        con.sessionOpenHandler(remoteSession -> {
            remoteSession.open();
        });
        // ATTACH frame received -> client wants to send messages to this server.
        con.receiverOpenHandler(remoteReceiver -> {
            remoteReceiver.handler((delivery, message) -> {
                // message received -> convert to CloudEvent
                final MessageReader reader = ProtonAmqpMessageFactory.createReader(message);
                final CloudEvent event = reader.toEvent();

                writer.printf("[Server] received CloudEvent[Id=%s, Source=%s]\n", event.getId(),
                        event.getSource().toString());
                
            }).open();
        });
        // ATTACH frame received -> client wants to receive messages from this server.
        con.senderOpenHandler(sender -> {
            try {
                MessageWriter<?, Message> writer = ProtonAmqpMessageFactory.createWriter();
                final CloudEvent event = CloudEventBuilder.v1()
                        .withId("amqp-server-id")
                        .withType("com.example.sampletype1")
                        .withSource(URI.create("http://127.0.0.1/amqp-server"))
                        .withTime(OffsetDateTime.now())
                        .withData("{\"temp\": 5}".getBytes(StandardCharsets.UTF_8))
                        .build();

                final Message message = writer.writeBinary(event);
                sender.send(message);
 
            } catch (final Exception e) {
                writer.println("[Server] failed to send ");
            }
            sender.open();
        });
        //OPEN frame received
        con.openHandler(remoteOpen -> {
            if (remoteOpen.failed()) {
                // connection with client failed.
                writer.println(remoteOpen.cause());
            } else {
                remoteOpen.result().open();
            }
        });
        // CLOSE Frame received
        con.closeHandler(remoteClose -> {
            con.close();
        });
    }
}
