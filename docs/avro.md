---
title: CloudEvents Avro
nav_order: 4
---

# CloudEvents Avro

[![Javadocs](http://www.javadoc.io/badge/io.cloudevents/cloudevents-avro.svg?color=green)](http://www.javadoc.io/doc/io.cloudevents/cloudevents-avro)

This module provides the Avro Buffer (avro) `EventFormat` implementation using the Java
avro runtime and classes generated from the CloudEvents
[avro spec](https://github.com/cloudevents/spec/blob/main/spec.avro).

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

## Using the Avro Event Format

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
    .resolveFormat(AvroFormat.CONTENT_TYPE)
    .serialize(event);
```

The `EventFormatProvider` will automatically resolve the `avroFormat` using the
`ServiceLoader` APIs.

