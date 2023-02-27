---
title: CloudEvents HTTP Vert.x
nav_order: 5
---

# HTTP Protocol Binding for Eclipse Vert.x

[![Javadocs](http://www.javadoc.io/badge/io.cloudevents/cloudevents-http-vertx.svg?color=green)](http://www.javadoc.io/doc/io.cloudevents/cloudevents-http-vertx)

For Maven based projects, use the following to configure the CloudEvents Vertx
HTTP Transport:

```xml
<dependency>
    <groupId>io.cloudevents</groupId>
    <artifactId>cloudevents-http-vertx</artifactId>
    <version>2.4.2</version>
</dependency>
```

## Receiving CloudEvents

Assuming you have in classpath [`cloudevents-json-jackson`](json-jackson.md),
below is a sample on how to read and write CloudEvents:

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
          .onSuccess(messageReader -> {
            CloudEvent event = messageReader.toEvent();
            // Echo the message, as structured mode
            VertxMessageFactory
              .createWriter(req.response())
              .writeStructured(event, "application/cloudevents+json");
          })
          .onFailure(t -> req.response().setStatusCode(500).end());
      })
      .listen(8080)
      .onSuccess(server ->
        System.out.println("Server started on port " + server.actualPort())
      ).onFailure(t -> {
        System.out.println("Error starting the server");
        serverResult.cause().printStackTrace();
      });
  }
}
```

## Sending CloudEvents

Below is a sample on how to use the client to send and receive a CloudEvent:

```java
import io.cloudevents.CloudEvent;
import io.cloudevents.core.builder.CloudEventBuilder;
import io.cloudevents.http.vertx.VertxMessageFactory;
import io.vertx.core.AbstractVerticle;
import io.vertx.ext.web.client.WebClient;

import java.net.URI;

public class CloudEventClientVerticle extends AbstractVerticle {

  public void start() {
    WebClient client = WebClient.create(vertx);

    CloudEvent reqEvent = CloudEventBuilder.v1()
        .withId("hello")
        .withType("example.vertx")
        .withSource(URI.create("http://localhost"))
        .build();

    VertxMessageFactory
        .createWriter(client.postAbs("http://localhost:8080"))
        .writeBinary(reqEvent)
        .onSuccess(response -> {
          CloudEvent responseEvent = VertxMessageFactory
              .createReader(response)
              .toEvent();
        })
        .onFailure(Throwable::printStackTrace);
  }
}
```

## Examples:

- [Vert.x Client and Server](https://github.com/cloudevents/sdk-java/tree/master/examples/vertx)
