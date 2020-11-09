# Kafka Protocol Binding

[![Javadocs](http://www.javadoc.io/badge/io.cloudevents/cloudevents-kafka.svg?color=green)](http://www.javadoc.io/doc/io.cloudevents/cloudevents-kafka)

For Maven based projects, use the following to configure the [Kafka Protocol Binding](https://github.com/cloudevents/spec/blob/master/kafka-protocol-binding.md):

```xml
<dependency>
    <groupId>io.cloudevents</groupId>
    <artifactId>cloudevents-kafka</artifactId>
    <version>2.0.0-milestone4</version>
</dependency>
```

### Producing CloudEvents

To produce CloudEvents in Kafka, configure the KafkaProducer to use the provided [`CloudEventSerializer`](src/main/java/io/cloudevents/kafka/CloudEventSerializer.java):

```java
import java.util.Properties;

import io.cloudevents.CloudEvent;
import io.cloudevents.core.builder.CloudEventBuilder;
import io.cloudevents.kafka.CloudEventSerializer;
import org.apache.kafka.clients.producer.KafkaProducer;
import org.apache.kafka.clients.producer.ProducerConfig;
import org.apache.kafka.common.serialization.StringSerializer;

public class CloudEventProducer {

    public static void main(String[] args) {
        Properties props = new Properties();

        // Other config props

        props.put(ProducerConfig.KEY_SERIALIZER_CLASS_CONFIG, StringSerializer.class);
        props.put(ProducerConfig.VALUE_SERIALIZER_CLASS_CONFIG, CloudEventSerializer.class);

        try(KafkaProducer<String, CloudEvent> producer = new KafkaProducer<>(props)){

            // Build an event
            CloudEvent event = CloudEventBuilder.v1()
              .withId("hello")
              .withType("example.kafka")
              .withSource(URI.create("http://localhost"))
              .build();

        	// Produce the event
        	producer.send(new ProducerRecord<>("your.topic", event));
        }
    }

}
```

You can configure the Encoding and EventFormat to use to emit the event.
Check out the [`CloudEventSerializer`](src/main/java/io/cloudevents/kafka/CloudEventSerializer.java)
javadoc for more info.

## Consuming CloudEvents

To consume CloudEvents in Kafka, configure the KafkaConsumer to use the provided [`CloudEventDeserializer`](src/main/java/io/cloudevents/kafka/CloudEventDeserializer.java):

```java
import java.time.Duration;import java.util.Properties;

import io.cloudevents.CloudEvent;
import io.cloudevents.kafka.CloudEventDeserializer;
import org.apache.kafka.clients.consumer.ConsumerConfig;
import org.apache.kafka.clients.consumer.ConsumerRecords;
import org.apache.kafka.clients.consumer.KafkaConsumer;
import org.apache.kafka.common.serialization.StringDeserializer;

public class CloudEventConsumer {

    public static void main(String[] args) {
        Properties props = new Properties();

        // Other config props

        props.put(ConsumerConfig.KEY_DESERIALIZER_CLASS_CONFIG, StringDeserializer.class);
        props.put(ConsumerConfig.VALUE_DESERIALIZER_CLASS_CONFIG, CloudEventDeserializer.class);

        try(KafkaConsumer<String, CloudEvent> consumer = new KafkaConsumer<>(props)){

            ConsumerRecords<String, CloudEvent> records = consumer.poll(Duration.ofSeconds(10));

            records.forEach(rec -> {
                System.out.println(rec.value().toString());
            });
        }
    }

}
```
