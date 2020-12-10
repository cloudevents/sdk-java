---
title: CloudEvents Spring
nav_order: 5
---

# CloudEvents Spring

[![Javadocs](http://www.javadoc.io/badge/io.cloudevents/cloudevents-spring.svg?color=green)](http://www.javadoc.io/doc/io.cloudevents/cloudevents-spring)

This module provides the integration of `CloudEvent` with different Spring APIs,
like MVC, WebFlux and Messaging

For Maven based projects, use the following dependency:

```xml

<dependency>
    <groupId>io.cloudevents</groupId>
    <artifactId>cloudevents-spring</artifactId>
    <version>2.0.0.RC1</version>
</dependency>
```

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

Check out the samples:

-   [spring-reactive](https://github.com/cloudevents/sdk-java/tree/master/examples/spring-reactive)
    shows how to receive and send CloudEvents through HTTP using Spring Boot and
    Webflux.
