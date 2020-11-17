## Examples of Cloud Events with Spring Cloud Function and Kafka

### Introduction
The current example uses [Spring Cloud Function](https://spring.io/projects/spring-cloud-function) framework as its core as well as 
the support provided by [Cloud Events Java SDK](https://github.com/cloudevents/sdk-java).
As many things in Spring, Spring Cloud Function allows users to concentrate only on functional aspects of 
their requirement while taking care-off the non-functional ones. 
For more information on Spring Cloud Function please visit our [project page](https://spring.io/projects/spring-cloud-function).

It also uses [Spring Cloud Stream](https://spring.io/projects/spring-cloud-stream) microservices framework which also comes with 
many messaging middleware binders and as you may have guessed we'll be using Kafka binder to connect your 
functionality (i.e., your function) to Kafka topics.

The example consists of a `CloudeventDemoApplication` class that is a typical Spring Boot Application with a single 
function bean which provides implementation of some imaginary functional requirements.

```java
@Bean
public Function<SpringReleaseEvent, SpringReleaseEvent> pojoToPojo() {
    return event -> {
        System.out.println("RECEIVED Spring Release Event: " + event);
        return event.setReleaseDateAsString("01-10-2006").setVersion("2.0");
    };
}
```
As you can see from its definition it expects and instance of POJO (i.e., `SpringReleaseEvent`) which it also returns 
after few updates. 

Once you start the application you can send Kafka messages to it.
Please refer to [Kafka Quick Start](https://kafka.apache.org/quickstart) on how to navigate Kafka message broker, 
although we assume that if you are here you already know how.

Assuming you have Apache Kafka broker running we simplified the process of demo/testing of how to _produce_ and _consume_ Cloud Events 
with Apache Kafka. To do this we included `CloudeventDemoApplicationTests` which uses [Spring Kafka](https://spring.io/projects/spring-kafka) 
framework to assist with sending Spring Messages as binary-mode and structured-mode Cloud Events.

Here is one example of sending binary-mode message:

```java
public void testAsBinary() throws Exception {
    try( ConfigurableApplicationContext context = SpringApplication.run(CloudeventDemoApplication.class)) {
        KafkaTemplate kafka = context.getBean(KafkaTemplate.class);

        String binaryEvent = "{\"releaseDate\":\"24-03-2004\", \"releaseName\":\"Spring Framework\", \"version\":\"1.0\"}";

        Message<byte[]> message = MessageBuilder.withPayload(binaryEvent.getBytes(StandardCharsets.UTF_8))
            .setHeader(CloudEventAttributeUtils.DEFAULT_ATTR_PREFIX + CloudEventAttributeUtils.ID, UUID.randomUUID().toString())
            .setHeader(CloudEventAttributeUtils.DEFAULT_ATTR_PREFIX + CloudEventAttributeUtils.SOURCE, "https://spring.io/")
            .setHeader(CloudEventAttributeUtils.DEFAULT_ATTR_PREFIX + CloudEventAttributeUtils.SPECVERSION, "1.0")
            .setHeader(CloudEventAttributeUtils.DEFAULT_ATTR_PREFIX + CloudEventAttributeUtils.TYPE, "org.springframework")
            .setHeader(KafkaHeaders.TOPIC, "pojoToPojo-in-0")
            .build();

        ListenableFuture<SendResult<String, String>> future = kafka.send(message);

        assertThat(future.get(1000, TimeUnit.MILLISECONDS).getRecordMetadata()).isNotNull();
    }
}
 ```
 
You really don't need to do anything else as Spring Cloud Stream and Spring Cloud Function will take care of all the boilerplate functionality 
that deals with connectivity to Kafka, creation of topics and more. You should refer to individual project documentation to learn more details 
on how it is done, but here is a quick description:

_By including `spring-cloud-stream-binder-kafka` as your dependency you've enabled Spring Boot auto-configuration which as typical to Spring Boot
relies on certain defaults (i.e., host, port etc.). It is also recognizes that you have a function and such function will be bound as message 
listener to topics which will also be auto-created for you by the framework (yes, you can manage and configure and override all these defaults; 
see individual project documentation for more details). The two topics that will be auto-created for you are `pojoToPojo-in-0` and `pojoToPojo-out-0`.
As you can see their names derived from function name and the `in/out` part signifies input and output which effectively corresponds to input and 
output of your function, so your function will effectively listen on `pojoToPojo-in-0` topic and it's output will be sent to `pojoToPojo-out-0`._

Once you run the test you can see the log message form your function

```text
RECEIVED Spring Release Event: releaseDate:24-03-2004; releaseName:Spring Framework; version:1.0
```

You can also subscribe to `pojoToPojo-out-0` to see the result message.