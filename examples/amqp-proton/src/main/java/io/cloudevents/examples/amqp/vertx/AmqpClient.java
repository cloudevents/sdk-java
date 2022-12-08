package io.cloudevents.examples.amqp.vertx;

import io.cloudevents.CloudEvent;
import io.cloudevents.amqp.ProtonAmqpMessageFactory;
import io.cloudevents.core.message.MessageReader;
import io.cloudevents.core.v1.CloudEventBuilder;
import io.cloudevents.types.Time;
import io.vertx.core.Future;
import io.vertx.core.Promise;
import io.vertx.core.Vertx;
import io.vertx.core.json.JsonObject;
import io.vertx.proton.*;
import org.apache.qpid.proton.amqp.messaging.Accepted;
import org.apache.qpid.proton.message.Message;

import java.io.PrintWriter;
import java.net.URI;
import java.nio.charset.StandardCharsets;

/**
 * A example vertx-based AMQP client that interacts with a remote AMQP server to send and receive CloudEvent messages.
 */
public class AmqpClient {

    private static ProtonConnection connection;

    private static final String SERVER_HOST = "127.0.0.1";
    private static final int SERVER_PORT = 5672;
    private static final String SEND_MESSAGE = "send";
    private static final String RECEIVE_MESSAGE = "receive";

    final static Vertx VERTX = Vertx.vertx();
    private static PrintWriter writer = new PrintWriter(System.out, true);

    public static void main(String args[]) {

        if (args.length < 1) {
            writer.println("Usage: AmqpClient [send|receive]");
            return;
        }

        final String action = args[0].toLowerCase();

        switch (action) {

        case SEND_MESSAGE:
            sendMessage();
            break;

        case RECEIVE_MESSAGE:
            receiveMessage();
            break;

        default:
            writer.println("Unknown action");
        }
    }

    private static void sendMessage() {
        connectToServer(SERVER_HOST, SERVER_PORT)
        .compose(conn -> {
            connection = conn;
            writer.printf("[Client] Connected to %s:%s", SERVER_HOST, SERVER_PORT);

            return openSenderLink();
        }).onSuccess(sender -> {

            final JsonObject payload = new JsonObject().put("temp", 50);

            final CloudEvent event = new CloudEventBuilder()
                .withId("client-id")
                .withSource(URI.create("http://127.0.0.1/amqp-client"))
                .withType("com.example.sampletype1")
                .withTime(Time.parseTime("2020-11-06T21:47:12.037467+00:00"))
                    .withData(payload.toString().getBytes(StandardCharsets.UTF_8))
                    .build();

            final Message message = ProtonAmqpMessageFactory.createWriter().writeBinary(event);
            message.setAddress("/telemetry");
            sender.send(message, delivery -> {
                if (Accepted.class.isInstance(delivery.getRemoteState())) {
                    writer.println("[Client:] message delivered and accepted by remote peer");
                }
                connection.close();
            });
        }).onFailure(t -> {
            writer.printf("[Client] Connection failed (%s)", t.getCause().getMessage());
        });

    }

    private static void receiveMessage() {
        connectToServer(SERVER_HOST, SERVER_PORT)
            .compose(conn -> {
                connection = conn;
                writer.println("[Client] Connected");
                return Future.succeededFuture();
            }).onSuccess(success ->
            openReceiverLink((delivery, message) -> {
                final MessageReader reader = ProtonAmqpMessageFactory.createReader(message);
                final CloudEvent event = reader.toEvent();
                writer.printf("[Client] received CloudEvent[Id=%s, Source=%s]", event.getId(),
                    event.getSource().toString());
                connection.close();
            })
        ).onFailure(t -> {
            writer.println("[Client] Connection failed");
        });
    }

    private static Future<ProtonConnection> connectToServer(final String host, final int port) {

        final Promise<ProtonConnection> connectAttempt = Promise.promise();
        final ProtonClientOptions options = new ProtonClientOptions();
        final ProtonClient client = ProtonClient.create(VERTX);

        client.connect(options, host, port, connectAttempt);

        return connectAttempt.future()
                .compose(unopenedConnection -> {
                    final Promise<ProtonConnection> con = Promise.promise();
                    unopenedConnection.openHandler(con);
                    unopenedConnection.open();
                    return con.future();
                });
    }

    private static Future<ProtonSender> openSenderLink() {
        if (connection == null || connection.isDisconnected()) {
            throw new IllegalStateException("[Client] connection not established");
        }

        final Promise<ProtonSender> result = Promise.promise();
        final ProtonSender sender = connection.createSender(null);
        sender.openHandler(result);
        sender.open();
        return result.future();
    }

    private static Future<ProtonReceiver> openReceiverLink(final ProtonMessageHandler msgHandler) {
        if (connection == null || connection.isDisconnected()) {
            throw new IllegalStateException("[Client] connection not established");
        }

        final Promise<ProtonReceiver> result = Promise.promise();
        final ProtonReceiver receiver = connection.createReceiver(null);
        receiver.setQoS(ProtonQoS.AT_LEAST_ONCE);
        receiver.handler(msgHandler);
        receiver.openHandler(result);
        receiver.open();
        return result.future().map(recver -> {
            //  Ready to receive messages
            return recver;
        });
    }

}
