---
title: CloudEvents HTTP Basic
nav_order: 5
---

# Generic HTTP Protocol Binding

[![Javadocs](http://www.javadoc.io/badge/io.cloudevents/cloudevents-http-basic.svg?color=green)](http://www.javadoc.io/doc/io.cloudevents/cloudevents-http-basic)

This module is designed to be usable with various HTTP APIs.

There are also more specialized HTTP bindings:

-   [`cloudevents-http-vertx`](http-vertx.md)
-   [`cloudevents-http-restful-ws`](http-jakarta-restful-ws.md)
-   [`cloudevents-spring`](spring.md)

Since this module is generic it doesn't offer optimal performance for all HTTP
implementations. For better performance consider implementing `MessageReader`
and `MessageWriter` that are tailored for specific HTTP implementation. As a
reference you can take aforementioned existing bindings.

For Maven based projects, use the following to configure the CloudEvents Generic
HTTP Transport:

```xml
<dependency>
    <groupId>io.cloudevents</groupId>
    <artifactId>cloudevents-http-basic</artifactId>
    <version>2.0.0.RC1</version>
</dependency>
```

## Sending and Receiving CloudEvents

To send and receive CloudEvents we use `MessageWriter` and `MessageReader`,
respectively. This module offers factory methods for creation of those in
`HttpMessageFactory`.

```java
public class HttpMessageFactory {
    public static MessageReader createReader(Consumer<BiConsumer<String,String>> forEachHeader, byte[] body);
    public static MessageReader createReader(Map<String,String> headers, byte[] body);
    public static MessageReader createReaderFromMultimap(Map<String,List<String>> headers, byte[] body);
    public static MessageWriter createWriter(BiConsumer<String, String> putHeader, Consumer<byte[]> sendBody);
}
```

## Examples

-   [Standard Java HttpServer](https://github.com/cloudevents/sdk-java/tree/master/examples/basic-http/src/main/java/io/cloudevents/examples/http/basic/BasicHttpServer.java)
-   [Http Client with HttpURLConnection](https://github.com/cloudevents/sdk-java/tree/master/examples/basic-http/src/main/java/io/cloudevents/examples/http/basic/HttpURLConnectionClient.java)
-   [Http Servlet with Jetty](https://github.com/cloudevents/sdk-java/tree/master/examples/basic-http/src/main/java/io/cloudevents/examples/http/basic/JettyServer.java)
