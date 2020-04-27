# Java SDK for CloudEvents

A Java API for the [CloudEvents specification](https://github.com/cloudevents/spec)

## Supported specification features

Supported features of the specification:

|                               |  [v0.3](https://github.com/cloudevents/spec/tree/v0.3) | [v1.0](https://github.com/cloudevents/spec/tree/v1.0) |
| :---------------------------: | :----------------------------------------------------------------------------: | :---------------------------------------------------------------------------------: |
| CloudEvents Core              | :heavy_check_mark: | :heavy_check_mark: |
| AMQP Protocol Binding         | :x: | :x:  |
| AVRO Event Format             | :x: | :x: |
| HTTP Protocol Binding         | :heavy_check_mark: | :heavy_check_mark: |
| - [Vert.x](http/vertx)        | :heavy_check_mark: | :heavy_check_mark: |
| JSON Event Format             | :heavy_check_mark: | :heavy_check_mark: |
| - [Jackson](formats/json-jackson) | :heavy_check_mark: | :heavy_check_mark: |
| Kafka Protocol Binding        | :x: | :x: |
| MQTT Protocol Binding         | :x: | :x: |
| NATS Protocol Binding         | :x: | :x: |
| Web hook                      | :x: | :x: |
