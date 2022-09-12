---
title: CloudEvents NATS
nav_order: 5
---

# CloudEvents NATS

[![Javadocs](http://www.javadoc.io/badge/io.cloudevents/cloudevents-nats.svg?color=green)](http://www.javadoc.io/doc/io.cloudevents/cloudevents-nats)

Implementation of the NATS protocol binding to send and receive CloudEvents.

The NATS protocol binding Maven dependency is:

```xml

<dependency>
    <groupId>io.cloudevents</groupId>
    <artifactId>cloudevents-nats</artifactId>
    <version>2.5.0-SNAPSHOT</version>
</dependency>
```

The jnats library that it depends on is a `provided` dependency, so this means you must specify your own,

```xml
<dependency>
    <groupId>io.nats</groupId>
    <artifactId>jnats</artifactId>
    <version>2.15.6</version>
</dependency>
```

The library is built with 2.15.6+ as the jnats library which does support Headers as were introduced in 2.2+ of
NATS. If you are running NATS 2.1 or earlier the headers will be ignored, the only way to determine if your NATS
server is 2.2+ is to have an active connection, which is not required for this library.

The library is `Message` centric, it creates and decodes NATS `Message` objects.

## Writing Cloud Events

To write a CloudEvent, you can use a structured or binary writer. This SDK recommends the binary writer if you are
using NATS 2.2+ because it supports headers which are routable. It also allows considerable leeway in terms of
how you pass your data (such as sending `application/json+gzip` format data).

To write a binary message:

```java
import io.nats.client.Message;
import io.cloudevents.nats.NatsMessageFactory;

Message message = NatsMessageFactory.createWriter(subject).writeBinary(event);
```

To write a structured message:

```java
import io.nats.client.Message;
import io.cloudevents.nats.NatsMessageFactory;

Message message = NatsMessageFactory.createWriter(subject).writeStructured(event, new JsonFormat());
```

alternatively you can use the content type:

```java
import io.nats.client.Message;
import io.cloudevents.nats.NatsMessageFactory;

Message message = NatsMessageFactory.createWriter(subject).writeStructured(event, JsonFormat.CONTENT_TYPE);
```

NOTE: you can choose any suitable format for the format as long as it is supported (i.e. in your classpath).

You do not need to pass a subject, if one does not exist, then it will pick it up from the `subject` field of the
cloud event. If it is still not set, then the `subject` field of your message will be `null` and you will need to
set it before sending the message.

NOTE: writers are single-use - they hold the necessary information for sending a single event.

A more full example would be:

```java

Connection conn = ... // connect to NATS

conn.publish(NatsMessageFactory.createWriter(subject).writeBinary(event));
```

## Reading Cloud Events

Reading CloudEvent's with the NATS binding is even more straightforward as the CloudEvents framework detects how
the message was sent and decodes it for you.

```java

CloudEvent event = NatsMessageFactory.createReader(message).toEvent();

```

