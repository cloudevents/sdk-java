---
title: CloudEvents Protocol Buffers
nav_order: 4
---

# CloudEvents Protocol Buffers

[![Javadocs](http://www.javadoc.io/badge/io.cloudevents/cloudevents-protobuf.svg?color=green)](http://www.javadoc.io/doc/io.cloudevents/cloudevents-protobuf)

This module provides the Protocol Buffer (protobuf) `EventFormat` implementation using the Java
Protobuf runtime and classes generated from the CloudEvents
[proto spec](https://github.com/cloudevents/spec/blob/v1.0.1/spec.proto).

# Setup
For Maven based projects, use the following dependency:

```xml
<dependency>
    <groupId>io.cloudevents</groupId>
    <artifactId>cloudevents-protobuf</artifactId>
    <version>4.0.0</version>
</dependency>
```

No further configuration is required is use the module.

## Using the Protobuf Event Format

### Event serialization

```java
import io.cloudevents.CloudEvent;
import io.cloudevents.core.format.ContentType;
import io.cloudevents.core.provider.EventFormatProvider;
import io.cloudevents.core.builder.CloudEventBuilder;

CloudEvent event = CloudEventBuilder.v1()
    .withId("hello")
    .withType("example.vertx")
    .withSource(URI.create("http://localhost"))
    .build();

byte[]serialized = EventFormatProvider
    .getInstance()
    .resolveFormat(ContentType.PROTO)
    .serialize(event);
```

The `EventFormatProvider` will automatically resolve the `ProtobufFormat` using the
`ServiceLoader` APIs.

## Passing Protobuf messages as CloudEvent data.

The `ProtoCloudEventData` capability provides a convenience mechanism to handle Protobuf message object data.

### Building

```java
// Build my business event message.
com.google.protobuf.Message myMessage = ..... ;

// Wrap the protobuf message as CloudEventData.
CloudEventData ceData = ProtoCloudEventData.wrap(myMessage);

// Build the CloudEvent
CloudEvent event = CloudEventBuilder.v1()
    .withId("hello")
    .withType("example.protodata")
    .withSource(URI.create("http://localhost"))
    .withData(ceData)
    .build();
```

### Reading

If the `ProtobufFormat` is used to deserialize a CloudEvent that contains a protobuf message object as data you can use
the `ProtoCloudEventData` to access it as an 'Any' directly.

```java

// Deserialize the event.
CloudEvent myEvent = eventFormat.deserialize(raw);

// Get the Data
CloudEventData eventData = myEvent.getData();

if (ceData instanceOf ProtoCloudEventData) {

    // Obtain the protobuf 'any'
    Any anAny = ((ProtoCloudEventData) eventData).getAny();

    ...
}

```

