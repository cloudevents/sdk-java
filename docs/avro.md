---
title: CloudEvents Avro Turbo
nav_order: 4
---

# CloudEvents Avro Turbo

[![Javadocs](http://www.javadoc.io/badge/io.cloudevents/cloudevents-avro-turbo.svg?color=green)](http://www.javadoc.io/doc/io.cloudevents/cloudevents-avro-turbo)

This module provides the Avro Turbo `EventFormat` implementation.

# Setup
For Maven based projects, use the following dependency:

```xml
<dependency>
    <groupId>io.cloudevents</groupId>
    <artifactId>cloudevents-avro-turbo</artifactId>
    <version>x.y.z</version>
</dependency>
```

No further configuration is required is use the module.

## Using the Avro Turbo Event Format

### Event serialization

```java
import io.cloudevents.CloudEvent;
import io.cloudevents.core.format.EventFormatProvider;
import io.cloudevents.core.builder.CloudEventBuilder;
import io.cloudevents.avro.avroturbo.AvroTurboFormat;

CloudEvent event = CloudEventBuilder.v1()
    .withId("hello")
    .withType("example.vertx")
    .withSource(URI.create("http://localhost"))
    .build();

byte[]serialized = EventFormatProvider
    .getInstance()
    .resolveFormat(AvroTurboFormat.CONTENT_TYPE)
    .serialize(event);
```

The `EventFormatProvider` will automatically resolve the format using the
`ServiceLoader` APIs.

