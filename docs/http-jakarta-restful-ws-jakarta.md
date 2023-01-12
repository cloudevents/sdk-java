---
title: CloudEvents HTTP Jakarta EE 9+ - Jakarta RESTful Web Services
nav_order: 5
---

# HTTP Protocol Binding for Jakarta EE 9+ - Jakarta RESTful Web Services

[![Javadocs](https://www.javadoc.io/badge/io.cloudevents/cloudevents-http-restful-ws.svg?color=green)](https://www.javadoc.io/doc/io.cloudevents/cloudevents-http-restful-ws)

For Maven based projects, use the following to configure the CloudEvents Jakarta
RESTful Web Services Binding for Jakarta EE 9+:

```xml
<dependency>
    <groupId>io.cloudevents</groupId>
    <artifactId>cloudevents-http-restful-ws-jakarta</artifactId>
    <version>2.5.0-SNAPSHOT</version>
</dependency>
```

This integration is tested with Jersey (Requires JDK11 or higher), RestEasy & Microprofile Liberty.

#### * Before using this package ensure your web framework  does support the `jakarta.*` namespace.

## Receiving CloudEvents

You need to configure the `CloudEventsProvider` to enable
marshalling/unmarshalling of CloudEvents.

Below is a sample on how to read and write CloudEvents:

```java
import io.cloudevents.CloudEvent;
import io.cloudevents.core.builder.CloudEventBuilder;

import jakarta.ws.rs.GET;
import jakarta.ws.rs.POST;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.core.Response;

@Path("/")
public class EventReceiverResource {



    @GET
    @Path("getMinEvent")
    public CloudEvent getMinEvent() {
        return CloudEventBuilder.v1()
            .withId("hello")
            .withType("example.vertx")
            .withSource(URI.create("http://localhost"))
            .build();
    }

    // Return the CloudEvent using the HTTP binding structured encoding
    @GET
    @Path("getStructuredEvent")
    @StructuredEncoding("application/cloudevents+csv")
    public CloudEvent getStructuredEvent() {
        return CloudEventBuilder.v1()
            .withId("hello")
            .withType("example.vertx")
            .withSource(URI.create("http://localhost"))
            .build();
    }

    @POST
    @Path("postEventWithoutBody")
    public Response postEvent(CloudEvent inputEvent) {
        // Handle the event
        return Response.ok().build();
    }
}
```

## Sending CloudEvents

You need to configure the `CloudEventsProvider` to enable
marshalling/unmarshalling of CloudEvents.

Below is a sample on how to use the client to send a CloudEvent:

```java
import io.cloudevents.CloudEvent;
import io.cloudevents.http.restful.ws.CloudEventsProvider;

import jakarta.ws.rs.client.Entity;
import jakarta.ws.rs.client.WebTarget;
import jakarta.ws.rs.core.HttpHeaders;
import jakarta.ws.rs.core.Response;

public class CloudEventSender {

    public Response sendEvent(WebTarget target, CloudEvent event) {
        return target
            .path("postEvent")
            .request()
            .buildPost(Entity.entity(event, CloudEventsProvider.CLOUDEVENT_TYPE))
            .invoke();
    }

    public Response sendEventAsStructured(WebTarget target, CloudEvent event) {
        return target
            .path("postEvent")
            .request()
            .buildPost(Entity.entity(event, "application/cloudevents+json"))
            .invoke();
    }

    public CloudEvent getEvent(WebTarget target) {
        Response response = target
            .path("getEvent")
            .request()
            .buildGet()
            .invoke();

        return response.readEntity(CloudEvent.class);
    }
}
```

## Migrating EE 8 applications to EE 9+
The main change between Jakarta EE 8 and Jakarta EE 9 and future versions is the changing of the `javax.` to `jakarta.` namespaces used by key packages such as `jakarta.ws.rs-api` which provides the restful-ws API.

This change largely impacts only `import` statements it does filter down to dependencies such as this.

### Application migration
For application migration we would recommend reviewing materials available from https://jakarta.ee/resources/#documentation as a starting point.

### CloudEvents Dependency
To migrate to use EE 9+ supported package - replace `cloudevents-http-restful-ws` with `cloudevents-http-restful-ws-jakarta` and ensure the version is a minimum of `2.5.0-SNAPSHOT`

## Examples

- [Microprofile and Liberty](https://github.com/cloudevents/sdk-java/tree/main/examples/restful-ws-micropofile-liberty)

