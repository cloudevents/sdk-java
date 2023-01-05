---
title: Home
nav_order: 1
---

# Java SDK for CloudEvents

1. [Introduction](#introduction)
1. [Supported features](#supported-features)
1. [Get Started](#get-started)
1. [Modules](#modules)

## Introduction

The Java SDK for CloudEvents is a collection of Java libraries to adopt
CloudEvents in your Java application.

Using the Java SDK you can:

-   Access, create and manipulate `CloudEvent` inside your application.
-   Serialize and deserialize `CloudEvent` back and forth using the _CloudEvents
    Event Format_, like Json.
-   Read and write `CloudEvent` back and forth to HTTP, Kafka, AMQP using the
    _CloudEvents Protocol Binding_ implementations we provide for a wide range
    of well known Java frameworks/libraries.

## Supported features

|                                                    | [v0.3](https://github.com/cloudevents/spec/tree/v0.3) | [v1.0](https://github.com/cloudevents/spec/tree/v1.0) |
|:--------------------------------------------------:|:-----------------------------------------------------:|:-----------------------------------------------------:|
|                  CloudEvents Core                  |                  :heavy_check_mark:                   |                  :heavy_check_mark:                   |
|               AMQP Protocol Binding                |                          :x:                          |                          :x:                          |
|             - [Proton](amqp-proton.md)             |                  :heavy_check_mark:                   |                  :heavy_check_mark:                   |
 |               MQTT Protocol Binding                |                          :x:                          |                          :x:                          |
 |               - [Paho](mqtt-paho.md)               |                  :heavy_check_mark:                   |                  :heavy_check_mark:                   |
|                 AVRO Event Format                  |                          :x:                          |                          :x:                          |
|               HTTP Protocol Binding                |                  :heavy_check_mark:                   |                  :heavy_check_mark:                   |
|             - [Vert.x](http-vertx.md)              |                  :heavy_check_mark:                   |                  :heavy_check_mark:                   |
| - [Jakarta Restful WS](http-jakarta-restful-ws.md) |                  :heavy_check_mark:                   |                  :heavy_check_mark:                   |
|              - [Basic](http-basic.md)              |                  :heavy_check_mark:                   |                  :heavy_check_mark:                   |
|               - [Spring](spring.md)                |                  :heavy_check_mark:                   |                  :heavy_check_mark:                   |
|           - [http4k][http4k]<sup>†</sup>           |                  :heavy_check_mark:                   |                  :heavy_check_mark:                   |
|                 JSON Event Format                  |                  :heavy_check_mark:                   |                  :heavy_check_mark:                   |
|            - [Jackson](json-jackson.md)            |                  :heavy_check_mark:                   |                  :heavy_check_mark:                   |
|               Protobuf Event Format                |                  :heavy_check_mark:                   |                  :heavy_check_mark:                   |
|               - [Proto](protobuf.md)               |                  :heavy_check_mark:                   |                  :heavy_check_mark:                   |
|                  XML Event Format                  |                  :heavy_check_mark:                   |                  :heavy_check_mark:                   |
|                  - [XML](xml.md)                   |                  :heavy_check_mark:                   |                  :heavy_check_mark:                   |
|         [Kafka Protocol Binding](kafka.md)         |                  :heavy_check_mark:                   |                  :heavy_check_mark:                   |
|               MQTT Protocol Binding                |                          :x:                          |                          :x:                          |
|               NATS Protocol Binding                |                          :x:                          |                          :x:                          |
|                      Web hook                      |                          :x:                          |                          :x:                          |

<sub>† Source/artifacts hosted externally</sub>

## Get Started

In order to start learning how to create, access and manipulate `CloudEvent`s,
check out the [Core module documentation](core.md).

If you want to serialize and deserialize events and data back and forth to JSON,
check out the [Jackson Json module documentation](json-jackson.md).

Depending on the protocol and framework you're using, if you want to send and
receive CloudEvents, check out the dedicated pages:

-   [AMQP using Proton](amqp-proton.md)
-   [HTTP using Vert.x](http-vertx.md)
-   [HTTP using Jakarta EE 8 - Jakarta Restful WS](http-jakarta-restful-ws.md)
-   [HTTP using Jakarta EE 9+ - Jakarta Restful WS](http-jakarta-restful-ws-jakarta.md)
-   [HTTP using Spring](spring.md)
-   [HTTP using Jackson](json-jackson.md)
-   [Kafka](kafka.md)

If you're interested in implementing an object conforming to the `CloudEvent`
and related interfaces, in order to interoperate with the other components of
the SDK, check out the [API module documentation](api.md).

You can also check out the
[**Examples**](https://github.com/cloudevents/sdk-java/tree/main/examples).

## Modules

The CloudEvents SDK for Java is composed by several modules, each one providing
a different feature from the different sub specs of
[CloudEvents specification](#supported-features):

-   [`cloudevents-api`] Module providing the `CloudEvent` and other base
    interfaces
-   [`cloudevents-core`] Module providing `CloudEvent` implementation,
    `CloudEventBuilder` to create `CloudEvent`s programmatically, `EventFormat`
    to implement
    [Event Formats](https://github.com/cloudevents/spec/blob/v1.0/spec.md#event-format),
    `MessageReader` /`MessageWriter` to implement
    [Protocol bindings](https://github.com/cloudevents/spec/blob/v1.0/spec.md#protocol-binding)
-   [`cloudevents-bom`] Module providing a
    [bill of materials (BOM)](https://maven.apache.org/guides/introduction/introduction-to-dependency-mechanism.html#bill-of-materials-bom-poms)
    for easier integration of CloudEvents in other projects
-   [`cloudevents-json-jackson`] Implementation of [JSON Event format] with
    [Jackson](https://github.com/FasterXML/jackson)
-   [`cloudevents-protobuf`] Implementation of [Protobuf Event format] using code generated
    from the standard [protoc](https://github.com/protocolbuffers/protobuf) compiler.
-   [`cloudevents-xml`] Implementation of the XML Event Format.
-   [`cloudevents-http-vertx`] Implementation of [HTTP Protocol Binding] with
    [Vert.x Core](https://vertx.io/)
-   [`cloudevents-http-restful-ws`] Implementation of [HTTP Protocol Binding]
    for [Jakarta EE 8 Restful WS](https://jakarta.ee/specifications/restful-ws/2.1/)
-   [`cloudevents-http-restful-ws-jakarta`] Implementation of  [HTTP Protocol Binding]
      for [Jakarta EE 9+ Restful WS](https://jakarta.ee/specifications/restful-ws/)
-   [`cloudevents-http-basic`] Generic implementation of [HTTP Protocol
    Binding], primarily intended for integrators
-   [`cloudevents-kafka`] Implementation of [Kafka Protocol Binding]
-   [`cloudevents-amqp-proton`] Implementation of [AMQP Protocol Binding] with
    [Proton](http://qpid.apache.org/proton/)
-   [`cloudevents-spring`] Integration of `CloudEvent` with different Spring
    APIs, like MVC, WebFlux and Messaging

You can look at the latest published artifacts on
[Maven Central](https://search.maven.org/search?q=g:io.cloudevents).

[JSON Event format]: https://github.com/cloudevents/spec/blob/v1.0/json-format.md
[Protobuf Event format]: https://github.com/cloudevents/spec/blob/v1.0.1/protobuf-format.md
[HTTP Protocol Binding]: https://github.com/cloudevents/spec/blob/v1.0/http-protocol-binding.md
[Kafka Protocol Binding]: https://github.com/cloudevents/spec/blob/v1.0/kafka-protocol-binding.md
[AMQP Protocol Binding]: https://github.com/cloudevents/spec/blob/v1.0/amqp-protocol-binding.md
[`cloudevents-api`]: https://github.com/cloudevents/sdk-java/tree/main/api
[`cloudevents-bom`]: https://github.com/cloudevents/sdk-java/tree/main/bom
[`cloudevents-core`]: https://github.com/cloudevents/sdk-java/tree/main/core
[`cloudevents-json-jackson`]: https://github.com/cloudevents/sdk-java/tree/main/formats/json-jackson
[`cloudevents-protobuf`]: https://github.com/cloudevents/sdk-java/tree/main/formats/protobuf
[`cloudevents-xml`]: https://github.com/cloudevents/sdk-java/tree/main/formats/xml
[`cloudevents-http-vertx`]: https://github.com/cloudevents/sdk-java/tree/main/http/vertx
[`cloudevents-http-basic`]: https://github.com/cloudevents/sdk-java/tree/main/http/basic
[`cloudevents-http-restful-ws`]: https://github.com/cloudevents/sdk-java/tree/main/http/restful-ws
[`cloudevents-http-restful-ws-jakarta`]: https://github.com/cloudevents/sdk-java/tree/main/http/restful-ws-jakarta
[`cloudevents-kafka`]: https://github.com/cloudevents/sdk-java/tree/main/kafka
[`cloudevents-amqp-proton`]: https://github.com/cloudevents/sdk-java/tree/main/amqp
[`cloudevents-spring`]: https://github.com/cloudevents/sdk-java/tree/main/spring
[http4k]: https://www.http4k.org/guide/modules/cloud_events/
