---
title: CloudEvents MQTT (Paho)
nav_order: 5
---

# CloudEvents MQTT Paho

This module implements `MessageReader` and `MessageWriter` using the Paho
library.

It currently only support MQTT Version 5.

For Maven based projects, use the following to include the `Paho` MQTT Binding

```xml
<dependency>
    <groupId>io.cloudevents</groupId>
    <artifactId>cloudevents-mqtt-paho</artifactId>
    <version>2.5.0</version>
</dependency>
```

# Sending & Receiving CloudEvents

The `PahoMessageFactory` provides methods to create CloudEvent `MessageReader` and `MessageWriter`
instances that are used to encode and decode CloudEvents into MQTT messages.

```java
public final class PahoMessageFactory {

    public static MessageReader createReader(MqttMessage mqttMessage);

    public static MessageWriter createWriter();

}
```

## Reading CloudEvents

## Sending CloudEvents

