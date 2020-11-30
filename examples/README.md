# CloudEvents sdk-java examples

This directory includes some examples on how to use CloudEvents sdk-java:

-   [amqp-proton](amqp-proton) shows how to use the module
    `cloudevents-amqp-proton` to send and receive CloudEvents using AMQP 1.0.
-   [kafka](kafka) shows how to use the module `cloudevents-kafka` to produce
    and consume CloudEvents on Kafka topics.
-   [restful-ws-quarkus](restful-ws-quarkus) shows how to use the module
    `cloudevents-http-restful-ws` with Quarkus to receive and send CloudEvents
    through HTTP.
-   [restful-ws-spring-boot](restful-ws-spring-boot) shows how to use the module
    `cloudevents-http-restful-ws` with Spring Boot and Jersey to receive and
    send CloudEvents through HTTP.
-   [spring-reactive](spring-reactive) shows how to use the module
    `cloudevents-spring` with Spring Boot and Webflux to receive and
    send CloudEvents through HTTP.
-   [vertx](vertx) shows how to use the module `cloudevents-http-vertx` to
    receive and send CloudEvents through HTTP using `vertx-web-client` and
    `vertx-core`.
-   [basic-http](basic-http) shows how to use the module
    `cloudevents-http-basic` to send and receive CloudEvents using `JDK`'s
    `HttpServer`, `HttpURLConnection` and Jetty.
