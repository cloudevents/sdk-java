---
title: CloudEvents Spring
nav_order: 5
---

# CloudEvents Spring

[![Javadocs](http://www.javadoc.io/badge/io.cloudevents/cloudevents-spring.svg?color=green)](http://www.javadoc.io/doc/io.cloudevents/cloudevents-spring)

This module provides the integration of `CloudEvent` with different Spring APIs,
like MVC, WebFlux, RSocket and Messaging

For Maven based projects, use the following dependency:

```xml

<dependency>
    <groupId>io.cloudevents</groupId>
    <artifactId>cloudevents-spring</artifactId>
    <version>4.0.0</version>
</dependency>
```

plus whatever you need to support your use case (e.g. `spring-boot-starter-webflux` for reactive HTTP).

## Introduction

This module provides classes and interfaces that can be used by
[Spring frameworks](https://spring.io/) and integrations to assist with Cloud
Event processing.

Given that Spring defines
[Message](https://docs.spring.io/spring-framework/docs/current/javadoc-api/org/springframework/messaging/Message.html)
abstraction, which perfectly maps to the structure defined by Cloud Events
specification, one may say Cloud Events are already supported by any Spring
framework that relies on `Message`. So this modules provides several utilities
and interfaces to simplify working with Cloud Events in the context of Spring
frameworks and integrations (see individual component's javadocs for more
details).

## Examples

### Spring MVC

There is a `CloudEventHttpMessageConverter` that you can register for Spring MVC:

```java
@Configuration
public static class CloudEventHandlerConfiguration implements WebMvcConfigurer {

    @Override
    public void configureMessageConverters(List<HttpMessageConverter<?>> converters) {
        converters.add(0, new CloudEventHttpMessageConverter());
    }

}
```

With this in place you can write a `@RestController` with `CloudEvent` inputs or outputs, and the conversion will be handled by Spring. Example "echo" endpoint:

```java
@PostMapping("/echo")
public CloudEvent ce(@RequestBody CloudEvent event) {
    return CloudEventBuilder.from(event)
            .withId(UUID.randomUUID().toString())
            .withSource(URI.create("https://spring.io/foos"))
            .withType("io.spring.event.Foo")
            .withData(event.getData().toBytes())
            .build();
}
```

Both structured and binary events are supported. So if you know that the `CloudEvent` is in binary mode and the data can be converted to a `Foo`, then you can also use the `CloudEventHttpUtils` to deal with HTTP headers and stick to POJOs in the handler method. Example:

```java
@PostMapping("/echo")
public ResponseEntity<Foo> echo(@RequestBody Foo foo, @RequestHeader HttpHeaders headers) {
    CloudEvent attributes = CloudEventHttpUtils.fromHttp(headers)
            .withId(UUID.randomUUID().toString())
            .withSource(URI.create("https://spring.io/foos"))
            .withType("io.spring.event.Foo")
            .build();
    HttpHeaders outgoing = CloudEventHttpUtils.toHttp(attributes);
    return ResponseEntity.ok().headers(outgoing).body(foo);
}
```

### Spring Webflux

If you are using Spring Webflux instead of Spring MVC you can use the same patterns, but the configuration is different. In this case we have a pair of readers and writers that you can register with the `CodecCustomizer`:

```java
@Configuration
public static class CloudEventHandlerConfiguration implements CodecCustomizer {

    @Override
    public void customize(CodecConfigurer configurer) {
        configurer.customCodecs().register(new CloudEventHttpMessageReader());
        configurer.customCodecs().register(new CloudEventHttpMessageWriter());
    }

}
```

Then you can write similar code to the MVC example above, but with reactive signatures. Example echo endpoint:

```java
@PostMapping("/event")
public Mono<CloudEvent> event(@RequestBody Mono<CloudEvent> body) {
    return body.map(event -> CloudEventBuilder.from(event)
        .withId(UUID.randomUUID().toString())
        .withSource(URI.create("https://spring.io/foos"))
        .withType("io.spring.event.Foo")
        .withData(event.getData().toBytes()).build());
}
```

The `CodecCustomizer` also works on the client side, so you can use it anywhere that you use a `WebClient` (including in an MVC application). Here's a simple example of a Cloud Event HTTP client:

```java
WebClient client = ...; // Either WebClient.create() or @Autowired a WebClient.Builder
CloudEvent event = ...; // Create a CloudEvent
Mono<CloudEvent> response = client.post()
  .uri("http://localhost:8080/events")
  .bodyValue(event)
  .retrieve()
  .bodyToMono(CloudEvent.class);
```

### Messaging

Spring Messaging is applicable in a wide range of use cases including WebSockets, JMS, Apache Kafka, RabbitMQ and others. It is also a core part of the Spring Cloud Function and Spring Cloud Stream libraries, so those are natural tools to use to build applications that use Cloud Events. The core abstraction in Spring is the `Message` which carries headers and a payload, just like a `CloudEvent`. Since the mapping is quite direct it makes sense to have a set of converters for Spring applications, so you can consume and produce `CloudEvents`, by treating them as `Messages`. This project provides a converter that you can register in a Spring Messaging application:

```java
@Configuration
public static class CloudEventMessageConverterConfiguration {
	@Bean
	public CloudEventMessageConverter cloudEventMessageConverter() {
		return new CloudEventMessageConverter();
	}
}
```

A simple echo with Spring Cloud Function could then be written as:

```java
@Bean
public Function<CloudEvent, CloudEvent> events() {
    return event -> CloudEventBuilder.from(event)
        .withId(UUID.randomUUID().toString())
        .withSource(URI.create("https://spring.io/foos"))
        .withType("io.spring.event.Foo")
        .withData(event.getData().toBytes())
        .build();
}
```

(If the application was a webapp with `spring-cloud-function-web` you would need the HTTP converters or codecs as well, per the example above.)

### Generic Encoder and Decoder

Some applications present Cloud Events as binary data, but do not have "headers" like in HTTP or messages. For those use cases there is a lower level construct in Spring, and this project provides implementations in the form of `CloudEventEncoder` and `CloudEventDecoder`. Since the headers are not available in the surrounding abstraction, these only support _structured_ Cloud Events, where the attributes and data are packed together in the same byte buffer. As an example in an RSockets application you can register them like this:

```java
@Bean
@Order(-1)
public RSocketStrategiesCustomizer cloudEventsCustomizer() {
    return new RSocketStrategiesCustomizer() {
        @Override
        public void customize(Builder strategies) {
            strategies.encoder(new CloudEventEncoder());
            strategies.decoder(new CloudEventDecoder());
        }
    };

}
```

and then a simple echo endpoint could be written like this:

```java
@MessageMapping("event")
public Mono<CloudEvent> event(@RequestBody Mono<CloudEvent> body) {
    return body.map(event -> CloudEventBuilder.from(event)
            .withId(UUID.randomUUID().toString())
            .withSource(URI.create("https://spring.io/foos"))
            .withType("io.spring.event.Foo")
            .withData(event.getData().toBytes())
            .build());
}
```

### More

Check out the integration tests and samples:

-   [spring-reactive](https://github.com/cloudevents/sdk-java/tree/main/examples/spring-reactive)
    shows how to receive and send CloudEvents through HTTP using Spring Boot and
    Webflux.

-   [spring-rsocket](https://github.com/cloudevents/sdk-java/tree/main/examples/spring-rsocket)
    shows how to receive and send CloudEvents through RSocket using Spring Boot.

-   [spring-cloud-function](https://github.com/cloudevents/sdk-java/tree/main/examples/spring-function)
    shows how to consume and process CloudEvents via Spring Cloud Function.
