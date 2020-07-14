---
title: Home
nav_order: 1
---

# Java SDK for CloudEvents

A Java API for the [CloudEvents specification](https://github.com/cloudevents/spec)

1. [Supported Specification Features](#supported-specification-features)
1. [Modules](#modules)
1. [Get Started](#get-started)
1. [Use an event format](#use-an-event-format)
1. [Use a protocol binding](#use-a-protocol-binding)

## Supported Specification Features

Supported features of the specification:

|                               |  [v0.3](https://github.com/cloudevents/spec/tree/v0.3) | [v1.0](https://github.com/cloudevents/spec/tree/v1.0) |
| -------- | -- | -- |
| CloudEvents Core              | :heavy_check_mark: | :heavy_check_mark: |
| AMQP Protocol Binding         | :x: | :x:  |
| AVRO Event Format             | :x: | :x: |
| HTTP Protocol Binding         | :heavy_check_mark: | :heavy_check_mark: |
| - [Vert.x](https://github.com/cloudevents/sdk-java/tree/master/http/vertx)        | :heavy_check_mark: | :heavy_check_mark: |
| - [Jakarta Restful WS](https://github.com/cloudevents/sdk-java/tree/master/http/restful-ws) | :heavy_check_mark: | :heavy_check_mark: |
| JSON Event Format             | :heavy_check_mark: | :heavy_check_mark: |
| - [Jackson](https://github.com/cloudevents/sdk-java/tree/master/formats/json-jackson) | :heavy_check_mark: | :heavy_check_mark: |
| [Kafka Protocol Binding](https://github.com/cloudevents/sdk-java/tree/master/kafka)        | :heavy_check_mark: | :heavy_check_mark: |
| MQTT Protocol Binding         | :x: | :x: |
| NATS Protocol Binding         | :x: | :x: |
| Web hook                      | :x: | :x: |

## Modules

The CloudEvents SDK for Java is composed by several modules, each one providing a different feature from the different sub specs of [CloudEvents specification](#supported-specification-features):

* [`cloudevents-api`] Module providing the `CloudEvent` and other base interfaces
* [`cloudevents-core`] Module providing `CloudEvent` implementation, `CloudEventBuilder` to create `CloudEvent`s programmatically, `EventFormat` to implement [Event Formats](https://github.com/cloudevents/spec/blob/v1.0/spec.md#event-format), `Message`/`MessageVisitor` to implement [Protocol bindings](https://github.com/cloudevents/spec/blob/v1.0/spec.md#protocol-binding)
* [`cloudevents-json-jackson`] Implementation of [JSON Event format] with [Jackson](https://github.com/FasterXML/jackson)
* [`cloudevents-http-vertx`] Implementation of [HTTP Protocol Binding] with [Vert.x Core](https://vertx.io/)
* [`cloudevents-http-restful-ws`] Implementation of [HTTP Protocol Binding] for [Jakarta Restful WS](https://jakarta.ee/specifications/restful-ws/)
* [`cloudevents-kafka`] Implementation of [Kafka Protocol Binding]

The latest SDK version is _2.0.0-milestone1_.

## Get Started

You can start creating events using the `CloudEventBuilder`:

```java
import io.cloudevents.CloudEvent;
import io.cloudevents.core.builder.CloudEventBuilder;
import java.net.URI;

final CloudEvent event = CloudEventBuilder.v1()
    .withId("000")
    .withType("example.demo")
    .withSource(URI.create("http://example.com"))
    .withData("application/json", "{}".getBytes())
    .build();
```

Look at [`cloudevents-core`] README for more information.

## Use an event format

Event formats implementations are auto-discovered through an SPI. You can use them accessing through `EventFormatProvider`.
For example, given you have in your classpath the [`cloudevents-json-jackson`] module, you can serialize/deserialize an event to/from JSON using:

```java
import io.cloudevents.core.format.EventFormatProvider;
import io.cloudevents.jackson.Json;

EventFormat format = EventFormatProvider
  .getInstance()
  .resolveFormat(JsonFormat.CONTENT_TYPE);

// Serialize event
byte[] serialized = format.serialize(event);

// Deserialize event
CloudEvent event = format.deserialize(bytes);
```

## Use a protocol binding

Each protocol binding has its own APIs, depending on the library/framework it's integrating with.
Check out the documentation of the protocol binding modules:

* [`cloudevents-http-vertx`]
* [`cloudevents-http-restful-ws`]
* [`cloudevents-kafka`]

[JSON Event Format]: https://github.com/cloudevents/spec/blob/v1.0/json-format.md
[HTTP Protocol Binding]: https://github.com/cloudevents/spec/blob/v1.0/http-protocol-binding.md
[Kafka Protocol Binding]: https://github.com/cloudevents/spec/blob/v1.0/kafka-protocol-binding.md
[`cloudevents-api`]: https://github.com/cloudevents/sdk-java/tree/master/api
[`cloudevents-core`]: https://github.com/cloudevents/sdk-java/tree/master/core
[`cloudevents-json-jackson`]: https://github.com/cloudevents/sdk-java/tree/master/formats/json-jackson
[`cloudevents-http-vertx`]: https://github.com/cloudevents/sdk-java/tree/master/http/vertx
[`cloudevents-http-restful-ws`]: https://github.com/cloudevents/sdk-java/tree/master/http/restful-ws
[`cloudevents-kafka`]: https://github.com/cloudevents/sdk-java/tree/master/kafka
