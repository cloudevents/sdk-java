---
title: CloudEvents Avro
nav_order: 4
---

# CloudEvents Avro

[![Javadocs](http://www.javadoc.io/badge/io.cloudevents/cloudevents-avro.svg?color=green)](http://www.javadoc.io/doc/io.cloudevents/cloudevents-avro)

This module provides the Avro Buffer (avro) `EventFormat` implementation using the Java
avro runtime and classes generated from the CloudEvents
[avro spec](https://github.com/cloudevents/spec/blob/v1.0.1/spec.avro).

# Setup
For Maven based projects, use the following dependency:

```xml
<dependency>
    <groupId>io.cloudevents</groupId>
    <artifactId>cloudevents-avro</artifactId>
    <version>x.y.z</version>
</dependency>
```

No further configuration is required is use the module.

## Using the avro Event Format

### Event serialization

```java
import io.cloudevents.CloudEvent;
import io.cloudevents.core.format.EventFormatProvider;
import io.cloudevents.core.builder.CloudEventBuilder;
import io.cloudevents.avro.avroFormat;

CloudEvent event = CloudEventBuilder.v1()
    .withId("hello")
    .withType("example.vertx")
    .withSource(URI.create("http://localhost"))
    .build();

byte[]serialized = EventFormatProvider
    .getInstance()
    .resolveFormat(avroFormat.CONTENT_TYPE)
    .serialize(event);
```

The `EventFormatProvider` will automatically resolve the `avroFormat` using the
`ServiceLoader` APIs.

## Passing avro messages as CloudEvent data.

The `AvroCloudEventData` capability provides a convenience mechanism to handle avro message object data.

### Building

```java
// Build my business event message.
com.google.avro.Message myMessage = ..... ;

// Wrap the avro message as CloudEventData.
CloudEventData ceData = AvroCloudEventData.wrap(myMessage);

// Build the CloudEvent
CloudEvent event = CloudEventBuilder.v1()
    .withId("hello")
    .withType("example.avrodata")
    .withSource(URI.create("http://localhost"))
    .withData(ceData)
    .build();
```

### Reading

If the `AvroFormat` is used to deserialize a CloudEvent that contains a avro message object as data you can use
the `AvroCloudEventData` to access it as an 'Any' directly.

```java

// Deserialize the event.
CloudEvent myEvent = eventFormat.deserialize(raw);

// Get the Data
CloudEventData eventData = myEvent.getData();

if (ceData instanceOf AvroCloudEventData) {

    // Obtain the avro 'any'
    Any anAny = ((AvroCloudEventData) eventData).getAny();

    ...
}

```

