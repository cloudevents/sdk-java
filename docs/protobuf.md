---
title: CloudEvents Protocol Buffers
nav_order: 4
---

# CloudEvents Protocol Buffers

[![Javadocs](http://www.javadoc.io/badge/io.cloudevents/cloudevents-protobuf.svg?color=green)](http://www.javadoc.io/doc/io.cloudevents/cloudevents-protobuf)

This module provides the Protocol Buffer (protobuf) `EventFormat` implementation using the Java
Protobuf runtime and classes generated from the CloudEvents
[proto spec](https://github.com/cloudevents/spec/blob/v1.0.1/spec.proto).

For Maven based projects, use the following dependency:

```xml
<dependency>
    <groupId>io.cloudevents</groupId>
    <artifactId>cloudevents-protobuf</artifactId>
    <version>2.5.0-SNAPSHOT</version>
</dependency>
```

## Using the Protobuf Event Format

You don't need to perform any operation to configure the module, more than
adding the dependency to your project:

```java
import io.cloudevents.CloudEvent;
import io.cloudevents.core.format.EventFormatProvider;
import io.cloudevents.core.builder.CloudEventBuilder;
import io.cloudevents.protobuf.ProtobufFormat;

CloudEvent event = CloudEventBuilder.v1()
    .withId("hello")
    .withType("example.vertx")
    .withSource(URI.create("http://localhost"))
    .build();

byte[]serialized = EventFormatProvider
    .getInstance()
    .resolveFormat(ProtobufFormat.CONTENT_TYPE)
    .serialize(event);
```

The `EventFormatProvider` will resolve automatically the `ProtobufFormat` using the
`ServiceLoader` APIs.
