# HTTP Protocol Binding for Eclipse Vert.x

[![Javadocs](http://www.javadoc.io/badge/io.cloudevents/cloudevents-http-vertx.svg?color=green)](http://www.javadoc.io/doc/io.cloudevents/cloudevents-http-vertx)

For Maven based projects, use the following to configure the CloudEvents Vertx HTTP Transport:

```xml
<dependency>
    <groupId>io.cloudevents</groupId>
    <artifactId>cloudevents-http-vertx</artifactId>
    <version>2.0.0-milestone1</version>
</dependency>
```

## Receiving CloudEvents

Assuming you have in classpath [`cloudevents-json-jackson`](../../formats/json-jackson), below is a sample on how to read and write CloudEvents:

```java

import io.cloudevents.http.vertx.VertxMessageFactory;
import io.cloudevents.core.message.StructuredMessageReader;
import io.cloudevents.CloudEvent;
import io.vertx.core.AbstractVerticle;

public class CloudEventServerVerticle extends AbstractVerticle {

  public void start() {
    vertx.createHttpServer()
      .requestHandler(req -> {
        VertxMessageFactory.createReader(req)
          .onComplete(result -> {
            // If decoding succeeded, we should write the event back
            if (result.succeeded()) {
              CloudEvent event = result.result().toEvent();
              // Echo the message, as structured mode
              VertxMessageFactory
                .createWriter(req.response())
                .writeStructured(event, "application/cloudevents+json");
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
import io.cloudevents.core.builder.CloudEventBuilder;
import io.cloudevents.core.message.MessageReader;
import io.cloudevents.http.vertx.VertxMessageFactory;
import io.cloudevents.CloudEvent;
import io.vertx.core.http.HttpClientRequest;
import io.vertx.core.http.HttpClient;
import io.vertx.core.AbstractVerticle;
import java.net.URI;

public class CloudEventClientVerticle extends AbstractVerticle {

  public void start() {
    HttpClient client = vertx.createHttpClient();

    HttpClientRequest request = client.postAbs("http://localhost:8080")
        .handler(httpClientResponse -> {
          VertxMessageFactory
            .createReader(httpClientResponse)
            .onComplete(result -> {
              if (result.succeeded()) {
                CloudEvent event = result.result().toEvent();
              }
          });
        });

    CloudEvent event = CloudEventBuilder.v1()
      .withId("hello")
      .withType("example.vertx")
      .withSource(URI.create("http://localhost"))
      .build();

    // Write request as binary
    VertxMessageFactory
      .createWriter(request)
      .writeBinary(event);
  }
}
```
