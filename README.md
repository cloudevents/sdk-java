# Java SDK for CloudEvents API

[![Build Status](https://travis-ci.org/cloudevents/sdk-java.png)](https://travis-ci.org/cloudevents/sdk-java)
[![License](https://img.shields.io/:license-Apache2-blue.svg)](http://www.apache.org/licenses/LICENSE-2.0)
[![Maven Central](https://maven-badges.herokuapp.com/maven-central/io.cloudevents/cloudevents-parent/badge.svg)](https://maven-badges.herokuapp.com/maven-central/io.cloudevents/cloudevents-parent)
[![Javadocs](http://www.javadoc.io/badge/io.cloudevents/cloudevents-api.svg?color=green)](http://www.javadoc.io/doc/io.cloudevents/cloudevents-api)

A Java API for the [CloudEvents specification](https://github.com/cloudevents/spec)

## Motivation

The [CloudEvents specification](https://github.com/cloudevents/spec) is a vendor-neutral specification for defining the format of event data that is being exchanged between different cloud systems. The specification basically defines an abstract envelope for any event data payload, without knowing specific implementation details of the actual underlying event. The current version of the spec is at `0.2` and it describes a simple event format, which was demonstrated at [KubeCon 2018](https://youtu.be/TZPPjAv12KU) using different _Serverless platforms_, such as [Apache Openwhisk](https://github.com/apache/incubator-openwhisk).

## Java API

For Maven based projects, use the following to configure the CloudEvents Java SDK:

```xml
<dependency>
    <groupId>io.cloudevents</groupId>
    <artifactId>cloudevents-api</artifactId>
    <version>0.2.2</version>
</dependency>
```

Application developers can now create strongly-typed CloudEvents, such as:

```java
import io.cloudevents.v02.CloudEventBuilder;
import io.cloudevents.v02.CloudEvent;
import io.cloudevents.v02.ExtensionFormat;
import io.cloudevents.json.Json;
import io.cloudevents.extensions.DistributedTracingExtension;

// given
final String eventId = UUID.randomUUID().toString();
final URI src = URI.create("/trigger");
final String eventType = "My.Cloud.Event.Type";
final MyCustomEvent payload = ...

// add trace extension usin the in-memory format
final DistributedTracingExtension dt = new DistributedTracingExtension();
dt.setTraceparent("00-4bf92f3577b34da6a3ce929d0e0e4736-00f067aa0ba902b7-01");
dt.setTracestate("rojo=00-4bf92f3577b34da6a3ce929d0e0e4736-00f067aa0ba902b7-01");

final ExtensionFormat tracing = new DistributedTracingExtension.InMemory(dt);

// passing in the given attributes
final CloudEvent<MyCustomEvent> cloudEvent = new CloudEventBuilder<MyCustomEvent>()
    .withType(eventType)
    .withId(eventId)
    .withSource(src)
    .withData(payload)
    .withExtension(tracing)
    .build();

// marshalling as json
final String json = Json.encode(cloudEvent);
```

## Possible Integrations

The API is kept simple, for allowing a wide range of possible integrations:

* [CDI](cdi/)
* [Eclipse Vert.x](http/vertx/)
