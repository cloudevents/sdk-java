---
title: CloudEvents XML Format
nav_order: 4
---

# CloudEvents XML Format

[![Javadocs](http://www.javadoc.io/badge/io.cloudevents/cloudevents-xml.svg?color=green)](http://www.javadoc.io/doc/io.cloudevents/cloudevents-xml)

This module provides and `EventFormat` implementation that adheres
to the CloudEvent XML Format specification.

This format also supports specialized handling for XML CloudEvent `data`.

For Maven based projects, use the following dependency:

```xml
<dependency>
    <groupId>io.cloudevents</groupId>
    <artifactId>cloudevents-xml</artifactId>
    <version>4.0.0</version>
</dependency>
```

## Using the XML Event Format

You don't need to perform any operation to configure the module, more than
adding the dependency to your project:

```java
import io.cloudevents.CloudEvent;
import io.cloudevents.core.format.ContentType;
import io.cloudevents.core.provider.EventFormatProvider;
import io.cloudevents.core.builder.CloudEventBuilder;

CloudEvent event = CloudEventBuilder.v1()
    .withId("hello")
    .withType("example.xml")
    .withSource(URI.create("http://localhost"))
    .build();

byte[] serialized = EventFormatProvider
    .getInstance()
    .resolveFormat(ContentType.XML)
    .serialize(event);
```

The `EventFormatProvider` will resolve automatically the `XMLFormat` using the
`ServiceLoader` APIs.

XML Document data handling is supported via the `XMLCloudEventData`
facility. This convenience wrapper can be used with `any` other supported
format.

```java
import org.w3c.dom.Document;
import io.cloudevents.CloudEvent;
import io.cloudevents.core.builder.CloudEventBuilder;
import io.cloudevents.xml.XMLCloudEventData;

// Create the business event data.
Document xmlDoc = .... ;

// Wrap it into CloudEventData
CloudEventData myData = XMLCloudEventData.wrap(xmlDoc);

// Construct the event
CloudEvent event = CloudEventBuilder.v1()
    .withId("hello")
    .withType("example.xml")
    .withSource(URI.create("http://localhost"))
    .withData(myData)
    .build();
```



