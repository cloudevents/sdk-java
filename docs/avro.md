---
title: CloudEvents Avro Compact
nav_order: 4
---

# CloudEvents Avro Compact

[![Javadocs](http://www.javadoc.io/badge/io.cloudevents/cloudevents-avro-compact.svg?color=green)](http://www.javadoc.io/doc/io.cloudevents/cloudevents-avro-compact)

This module provides the Avro Compact `EventFormat` implementation.

# Setup
For Maven based projects, use the following dependency:

```xml
<dependency>
    <groupId>io.cloudevents</groupId>
    <artifactId>cloudevents-avro-compact</artifactId>
    <version>4.0.0</version>
</dependency>
```

No further configuration is required is use the module.

## Using the Avro Compact Event Format

### Event serialization

```java
import io.cloudevents.CloudEvent;
import io.cloudevents.core.provider.EventFormatProvider;
import io.cloudevents.core.builder.CloudEventBuilder;
import io.cloudevents.avro.avro.compact.AvroCompactFormat;

CloudEvent event = CloudEventBuilder.v1()
    .withId("hello")
    .withType("example.vertx")
    .withSource(URI.create("http://localhost"))
    .build();

byte[] serialized = EventFormatProvider
    .getInstance()
    .resolveFormat(AvroCompactFormat.CONTENT_TYPE)
    .serialize(event);
```

The `EventFormatProvider` will automatically resolve the format using the
`ServiceLoader` APIs.

