# HTTP Transport for Eclipse Vert.x

For Maven based projects, use the following to configure the CloudEvents Vertx HTTP Transport:

```xml
<dependency>
    <groupId>io.cloudevents</groupId>
    <artifactId>http-vertx</artifactId>
    <version>2.0.0-SNAPSHOT </version>
</dependency>
```

## Receiving CloudEvents

Below is a sample on how to read and write CloudEvents:

```java
import io.cloudevents.http.vertx.VertxHttpServerResponseMessageVisitor;
import io.cloudevents.http.vertx.VertxMessage;
import io.cloudevents.CloudEvent;
import io.vertx.core.http.HttpHeaders;
import io.vertx.core.AbstractVerticle;

public class CloudEventServerVerticle extends AbstractVerticle {

  public void start() {
    vertx.createHttpServer()
      .requestHandler(req -> {
        VertxMessage.fromHttpServerRequest(req)
          .onComplete(result -> {
            // If decoding succeeded, we should write the event back
            if (result.succeeded()) {
              CloudEvent event = result.result().toEvent();
              // Echo the message, as binary mode
              event
                .asBinaryMessage()
                .visit(VertxHttpServerResponseMessageVisitor.create(req.response()));
            }
            req.response().setStatusCode(500).end();
          });
      })
      .listen(8080, serverResult -> {
        if (serverResult.succeeded()) {
          System.out.println("Server started on port " + serverResult.result().actualPort());
        } else {
          System.out.println("Error starting the server");
          serverResult.cause().printStackTrace();
        }
      });
  }
}
```

## Sending CloudEvents

Below is a sample on how to use the client to send and receive a CloudEvent:

```java
import io.cloudevents.http.vertx.VertxHttpClientRequestMessageVisitor;import io.cloudevents.http.vertx.VertxHttpServerResponseMessageVisitor;
import io.cloudevents.http.vertx.VertxMessage;
import io.cloudevents.CloudEvent;
import io.vertx.core.http.HttpClientRequest;
import io.vertx.core.http.HttpHeaders;
import io.vertx.core.http.HttpClient;
import io.vertx.core.AbstractVerticle;
import java.net.URI;

public class CloudEventClientVerticle extends AbstractVerticle {

  public void start() {
    HttpClient client = vertx.createHttpClient();

    HttpClientRequest request = client.postAbs("http://localhost:8080")
        .handler(httpClientResponse -> {
          VertxMessage
            .fromHttpClientResponse(httpClientResponse)
            .onComplete(result -> {
              if (result.succeeded()) {
                CloudEvent event = result.result().toEvent();
              }
          });
        });

    CloudEvent event = CloudEvent.buildV1()
      .withId("hello")
      .withType("example.vertx")
      .withSource(URI.create("http://localhost"))
      .build();

    // Write request as binary
    event
      .asBinaryMessage()
      .visit(VertxHttpClientRequestMessageVisitor.create(request));
  }
}
```
