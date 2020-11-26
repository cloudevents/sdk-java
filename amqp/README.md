# AMQP Protocol Binding

[![Javadocs](http://www.javadoc.io/badge/io.cloudevents/cloudevents-amqp-proton.svg?color=green)](http://www.javadoc.io/doc/io.cloudevents/cloudevents-amqp-proton)

This module implements `MessageReader` and `MessageWriter` using the Qpid Proton library. It can be used with Qpid Proton or any integrations based on Qpid Proton  (e.g vertx-proton). 

For Maven based projects, use the following to configure the `proton` AMQP binding for CloudEvents:

```xml
<dependency>
    <groupId>io.cloudevents</groupId>
    <artifactId>cloudevents-amqp-proton</artifactId>
    <version>2.0.0.RC1</version>
</dependency>
```

## Sending and Receiving CloudEvents

To send and receive CloudEvents we use `MessageWriter` and `MessageReader`, respectively.
This module offers factory methods for creation of those in `ProtonAmqpMessageFactory`.

```java
public class ProtonAmqpMessageFactory {
    public static MessageReader createReader(final Message message);
    public static MessageReader createReader(final String contentType, final byte[] payload);
    public static MessageReader createReader(final String contentType, final ApplicationProperties props, final byte[] payload);
    public static MessageWriter createWriter();
}
```

Examples:

The example uses the vertx-proton integration to send/receive CloudEvent messages over AMQP.
* [Vertx AmqpServer](../../examples/amqp-proton/src/main/java/io/cloudevents/examples/amqp/vertx/AmqpServer.java)
* [Vertx AmqpClient](../../examples/amqp-proton/src/main/java/io/cloudevents/examples/amqp/vertx/AmqpClient.java)
