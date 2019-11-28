# CDI Integration

## Firing CloudEvents using CDI

For Maven based projects, use the following to configure the CloudEvents CDI library:

```xml
<dependency>
    <groupId>io.cloudevents</groupId>
    <artifactId>cdi</artifactId>
    <version>1.0.1</version>
</dependency>
```

In _Enterprise Java_ applications, implemented with [Jakarta EE](https://jakarta.ee/) or the [Eclipse MicroProfile](https://microprofile.io/), it's trivial to combine this CloudEvents API with CDI. Application developers can now fire a CloudEvent for further processing inside of the application:

```java
import io.cloudevents.v02.CloudEventBuilder;
import io.cloudevents.v02.CloudEventImpl;
import io.cloudevents.cdi.EventTypeQualifier;

import javax.enterprise.event.Event;
import javax.inject.Inject;

import java.net.URI;
import java.util.UUID;

public class Router {

    @Inject
    private Event<CloudEventImpl<MyCustomEvent>> cloudEvent;

    public void routeMe() throws Exception {

        final CloudEventImpl<MyCustomEvent> event =
            CloudEventBuilder.<MyCustomEvent>builder()
                .withType("Cloud.Storage.Item.Created")
                .withSource(new URI("/trigger"))
                .withId(UUID.randomUUID().toString())
                .withData(new MyCustomEvent(...))
                .build();

        cloudEvent.select(
                new EventTypeQualifier("Cloud.Storage.Item.Created"))
                .fire(event);
    }
}
```

The method above creates a CloudEvent object, and uses the _injected_ CDI `Event` implementation,
where a `select()` is performed to fire a _qualified_ event, for all interested parties.

## Receiving CloudEvents with CDI

If other parts of the application are interested in the `Cloud.Storage.Item.Created` event,
it needs a matching `@Observes` annotation:

```java
public void receiveCloudEvent(
  @Observes @EventType(name = "My.Cloud.Event.Type") CloudEvent cloudEvent) {
  // handle the event
}                                                                                       
```

The application now is able to work with the _observed_ CloudEvent object.
