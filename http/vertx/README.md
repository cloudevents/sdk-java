# HTTP Transport Util for Eclipse Vert.x

## Receiving CloudEvents

Below is a sample on how to read CloudEvents from an HttpRequest:

```java
import io.vertx.core.AbstractVerticle;
public class Server extends AbstractVerticle {
  public void start() {
    vertx.createHttpServer().requestHandler(req -> {

      CeVertx.readFromRequest(req, reply -> {

        if (reply.succeeded()) {

          final CloudEvent<?> receivedEvent = reply.result();
          // access the attributes:
          System.out.println(receivedEvent.getEventID());
          ...
        });

      req.response()
        .putHeader("content-type", "text/plain")
        .end("Got a CloudEvent!");
    }).listen(8080);
  }
}
```

## Sending CloudEvents

Below is a sample on how to use the client to send a CloudEvent:

```java
final HttpClientRequest request = vertx.createHttpClient().post(7890, "localhost", "/");

CeVertx.writeToHttpClientRequest(cloudEvent, request);
    request.handler(resp -> {
        context.assertEquals(resp.statusCode(), 200);
    });
request.end();
```

