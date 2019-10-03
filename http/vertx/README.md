# HTTP Transport Util for Eclipse Vert.x

## Receiving CloudEvents

For Maven based projects, use the following to configure the CloudEvents Vertx HTTP Transport:

```xml
<dependency>
    <groupId>io.cloudevents</groupId>
    <artifactId>http-vertx</artifactId>
    <version>0.3.1</version>
</dependency>
```

Below is a sample on how to use [Vert.x API for RxJava 2](https://vertx.io/docs/vertx-rx/java2/) for reading CloudEvents from an HttpServerRequest:

```java
import io.cloudevents.http.reactivex.vertx.VertxCloudEvents;
import io.vertx.core.http.HttpHeaders;
import io.vertx.reactivex.core.AbstractVerticle;

public class CloudEventVerticle extends AbstractVerticle {

  public void start() {

    vertx.createHttpServer()
      .requestHandler(req -> { 
        VertxCloudEvents.create().rxReadFromRequest(req)
          .subscribe((receivedEvent, throwable) -> {
            if (receivedEvent != null) {
              // I got a CloudEvent object:
              System.out.println("The event type: " + receivedEvent.getEventType());
            }
          });
          req.response().end();
        }
      )
      .rxListen(8080)
      .subscribe(server -> {
        System.out.println("Server running!");
    });
  }
}
```

## Sending CloudEvents

Below is a sample on how to use the client to send a CloudEvent:

```java
final HttpClientRequest request = vertx.createHttpClient().post(8080, "localhost", "/");

// add a client response handler
request.handler(resp -> {
    // react on the server response
});

// write the CloudEvent to the given HTTP Post request object
VertxCloudEvents.create().writeToHttpClientRequest(cloudEvent, request);
request.end();
```
