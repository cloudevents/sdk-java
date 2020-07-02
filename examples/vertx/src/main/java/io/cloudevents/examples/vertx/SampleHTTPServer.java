package io.cloudevents.examples.vertx;

import io.cloudevents.core.message.MessageReader;
import io.cloudevents.http.vertx.VertxMessageFactory;
import io.vertx.core.Promise;
import io.vertx.core.Vertx;
import io.vertx.core.http.HttpServer;
import java.util.Arrays;

public class SampleHTTPServer {

    public static void main(String[] args) {

        if (args.length != 1) {
            System.err.println("Usage: SampleHTTPServer <port>");
            return;
        }
        final int port = Integer.parseInt(args[0]);

        final Vertx vertx = Vertx.vertx();

        final Promise<HttpServer> serverStartedPromise = Promise.promise();

        // Create HTTP server.
        vertx.createHttpServer()
            .exceptionHandler(System.err::println)
            .requestHandler(request -> {

                // We need to read the event from the HTTP request we get, so create a MessageReader.
                VertxMessageFactory.createReader(request)
                    // Covert the MessageReader to a CloudEvent.
                    .map(MessageReader::toEvent)
                    .onSuccess(event -> {
                        // Print out the event.
                        System.out.println(event);

                        // Write the same event as response in binary mode.
                        VertxMessageFactory.createWriter(request.response()).writeBinary(event);
                    })
                    .onFailure(System.err::println);


            })
            .listen(port, serverStartedPromise);

        serverStartedPromise.future()
            .onSuccess(server -> System.out.println(
                "Server listening on port: " + server.actualPort()
            ))
            .onFailure(System.err::println);
    }
}
