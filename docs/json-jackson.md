---
title: CloudEvents Json Jackson
nav_order: 4
---

# CloudEvents Json Jackson

[![Javadocs](http://www.javadoc.io/badge/io.cloudevents/cloudevents-json-jackson.svg?color=green)](http://www.javadoc.io/doc/io.cloudevents/cloudevents-json-jackson)

This module provides the JSON `EventFormat` implementation using Jackson and a
`PojoCloudEventDataMapper` to convert `CloudEventData` to POJOs using the
Jackson `ObjectMapper`.

For Maven based projects, use the following dependency:

```xml
<dependency>
    <groupId>io.cloudevents</groupId>
    <artifactId>cloudevents-json-jackson</artifactId>
    <version>4.0.0</version>
</dependency>
```

## Using the JSON Event Format

You don't need to perform any operation to configure the module, more than
adding the dependency to your project:

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
    .resolveFormat(ContentType.JSON)
    .serialize(event);
```

The `EventFormatProvider` will resolve automatically the `JsonFormat` using the
`ServiceLoader` APIs.

## Mapping `CloudEventData` to POJOs using Jackson `ObjectMapper`

Using the Jackson `ObjectMapper`, you can easily extract a POJO starting from
any `CloudEventData`:

```java
import io.cloudevents.core.data.PojoCloudEventData;
import io.cloudevents.jackson.PojoCloudEventDataMapper;

import static io.cloudevents.core.CloudEventUtils.mapData;

PojoCloudEventData<User> cloudEventData = mapData(
    inputEvent,
    PojoCloudEventDataMapper.from(objectMapper,User.class)
);
// check if cloudEventData is null
User user = cloudEventData.getValue();
```
