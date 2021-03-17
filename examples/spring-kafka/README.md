# Spring Kafka + CloudEvents sample

## Build

```shell
mvn package
```

## Start Consumer

```shell
mvn spring-boot:run
```

You can try sending a request using any kafka client, or using the intergration tests in this project. You send to the "in" topic and it echos back a cloud event on the "out" topic. The listener is implemented like this (the request and response are modelled directly as a `CloudEvent`):

```java
@KafkaListener(id = "listener", topics = "in", clientIdPrefix = "demo")
@SendTo("out")
public CloudEvent listen(CloudEvent event) {
    return ...;
}
```

and to make that work we need to install the Kafka message converter as a `@Bean`:

```java
@Bean
public CloudEventRecordMessageConverter recordMessageConverter() {
    return new CloudEventRecordMessageConverter();
}
```
