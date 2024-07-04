---
title: CloudEvents Core
nav_order: 3
---

# CloudEvents Core

[![Javadocs](http://www.javadoc.io/badge/io.cloudevents/cloudevents-core.svg?color=green)](http://www.javadoc.io/doc/io.cloudevents/cloudevents-core)

This package includes implementations and utilities to create and process
`CloudEvent` and interfaces to deal with Protocol Bindings and Event Formats.

For Maven based projects, use the following dependency:

```xml
<dependency>
    <groupId>io.cloudevents</groupId>
    <artifactId>cloudevents-core</artifactId>
    <version>4.0.0</version>
</dependency>
```

### Using `CloudEvent`s

To create an event, you can use the `CloudEventBuilder`:

```java
import io.cloudevents.CloudEvent;
import io.cloudevents.core.builder.CloudEventBuilder;

import java.net.URI;

final CloudEvent event = CloudEventBuilder.v1()
    .withId("000")
    .withType("example.demo")
    .withSource(URI.create("http://example.com"))
    .withData("text/plain","Hello world!".getBytes("UTF-8"))
    .build();
```

Now you can access the event context attributes and data:

```java
// Get an event attribute
String id = event.getId();

// Get an event extension
Object someExtension = event.getExtension("myextension");

// Retrieve the event data
CloudEventData data = event.getData();
```

### Creating and accessing to the event data

`CloudEventData` is an abstraction that can wrap any kind of data payload, while
still enforcing the conversion to bytes. To convert to bytes:

```java
// Convert the event data to bytes
byte[]bytes = cloudEventData.toBytes();
```

If you want to map the JSON event data to POJOs using Jackson, check out the
[CloudEvents Jackson documentation](json-jackson.md).

When building an event, you can wrap a POJO inside its payload using
`PojoCloudEventData`, providing the function to convert it back to bytes:

```java
CloudEventData pojoData = PojoCloudEventData.wrap(myPojo, myPojo::toBytes);
```

### Using Event Formats

The SDK implements
[Event Formats](https://github.com/cloudevents/spec/blob/v1.0/spec.md#event-format)
in various submodules, but keeping a single entrypoint for users who wants to
serialize/deserialize events back and forth to event formats.

To use an Event Format, you just need to add the event format implementation you
prefer as dependency in your project and the core module, through the
`ServiceLoader` mechanism, will load it into the classpath. For example, to use
the
[JSON event format](https://github.com/cloudevents/spec/blob/v1.0/json-format.md)
with Jackson, add `cloudevents-json-jackson` as a dependency and then using the
`EventFormatProvider`:

```java
import io.cloudevents.core.provider.EventFormatProvider;
import io.cloudevents.jackson.JsonFormat;

EventFormat format = EventFormatProvider
  .getInstance()
  .resolveFormat(JsonFormat.CONTENT_TYPE);

// Serialize event
byte[] serialized = format.serialize(event);

// Deserialize event
CloudEvent event = format.deserialize(bytes);
```

### Materialize an Extension

CloudEvent extensions can be materialized in their respective POJOs using the
`ExtensionProvider`:

```java
import io.cloudevents.core.extensions.DistributedTracingExtension;
import io.cloudevents.core.provider.ExtensionProvider;

DistributedTracingExtension dte = ExtensionProvider.getInstance()
    .parseExtension(DistributedTracingExtension.class, event);
```
