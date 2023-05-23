# RocketMQ + CloudEvents Sample

This example demonstrates the integration of [RocketMQ 5.x client library](https://github.com/apache/rocketmq-clients)
with CloudEvents to create a RocketMQ binding.

## Building the Project

```shell
mvn package
```

## Setting Up a RocketMQ Instance

Follow the [quickstart guide](https://rocketmq.apache.org/docs/quick-start/01quickstart) on the official RocketMQ
website to set up the necessary components, including nameserver, proxy, and broker.

## Event Production

```shell
mvn exec:java -Dexec.mainClass="io.cloudevents.examples.rocketmq.RocketmqProducer" -Dexec.args="foobar:8081 sample-topic"
```

## Event Consumption

```shell
mvn exec:java -Dexec.mainClass="io.cloudevents.examples.rocketmq.RocketmqConsumer" -Dexec.args="foobar:8081 sample-topic sample-consumer-group"
```
