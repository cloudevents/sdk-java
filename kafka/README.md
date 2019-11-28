# CloudEvents Kafka Transport Binding

The impl of Kafka Transport Biding for CloudEvents.

> See spec [here](https://github.com/cloudevents/spec/blob/master/kafka-transport-binding.md)

## How to Use

See some examples of how to use with Kafka Consumer and Kafka Producer.

Add the dependency in your project:

```xml
<dependency>
    <groupId>io.cloudevents</groupId>
    <artifactId>cloudevents-kafka</artifactId>
    <version>1.0.1</version>
</dependency>
```

### Producer

Producing CloudEvents in Kafka.

#### Binary Content Mode

```java
import java.net.URI;
import java.util.Properties;

import org.apache.kafka.clients.producer.ProducerConfig;
import org.apache.kafka.clients.producer.ProducerRecord;
import org.apache.kafka.common.serialization.ByteArraySerializer;

import io.cloudevents.format.builder.EventStep;
import io.cloudevents.kafka.CloudEventsKafkaProducer;
import io.cloudevents.v1.CloudEventImpl;
import io.cloudevents.v1.AttributesImpl;
import io.cloudevents.v1.kafka.Marshallers;

// . . .

Properties props = new Properties();

/* all other properties */

// But, the value serializer MUST be ByteArraySerializer
props.put(ProducerConfig.VALUE_SERIALIZER_CLASS_CONFIG,
			ByteArraySerializer.class);

// Then, instantiate the CloudEvents Kafka Producer
try(CloudEventsKafkaProducer<String, AttributesImpl, String>
		ceProducer = new CloudEventsKafkaProducer<>(props,
			Marshallers.binary())){

	// Build an event
	CloudEventImpl<String> ce =
		CloudEventBuilder.<String>builder()
			.withId("x10")
			.withSource(URI.create("/source"))
			.withType("event-type")
			.withDataContentType("application/json")
			.withData("Event Data")
			.build();

	// Produce the event
	ceProducer.send(new ProducerRecord<>("your.topic", ce));
}
```

#### Structured Content Mode

```java
import java.net.URI;
import java.util.Properties;

import org.apache.kafka.clients.producer.ProducerConfig;
import org.apache.kafka.clients.producer.ProducerRecord;
import org.apache.kafka.common.serialization.ByteArraySerializer;

import io.cloudevents.format.builder.EventStep;
import io.cloudevents.kafka.CloudEventsKafkaProducer;
import io.cloudevents.v1.CloudEventImpl;
import io.cloudevents.v1.AttributesImpl;
import io.cloudevents.v1.kafka.Marshallers;

// . . .

Properties props = new Properties();

/* all other properties */

// But, the value serializer MUST be ByteArraySerializer
props.put(ProducerConfig.VALUE_SERIALIZER_CLASS_CONFIG,
			ByteArraySerializer.class);

// Then, instantiate the CloudEvents Kafka Producer
try(CloudEventsKafkaProducer<String, AttributesImpl, String>
		ceProducer = new CloudEventsKafkaProducer<>(props,
			Marshallers.structured())){

	// Build an event
	CloudEventImpl<String> ce =
		CloudEventBuilder.<String>builder()
			.withId("x10")
			.withSource(URI.create("/source"))
			.withType("event-type")
			.withDataContentType("application/json")
			.withData("Event Data")
			.build();

	// Produce the event
	ceProducer.send(new ProducerRecord<>("your.topic", ce));
}
```

### Consumer

Consuming CloudEvents from Kafka.

#### Binary Content Mode

```java
import java.time.Duration;
import java.util.Collections;
import java.util.Properties;

import org.apache.kafka.clients.consumer.ConsumerConfig;
import org.apache.kafka.clients.consumer.ConsumerRecords;
import org.apache.kafka.common.serialization.ByteArrayDeserializer;

import io.cloudevents.CloudEvent;
import io.cloudevents.kafka.CloudEventsKafkaConsumer;
import io.cloudevents.types.Much;
import io.cloudevents.v1.AttributesImpl;
import io.cloudevents.v1.kafka.Unmarshallers;

// . . .

Properties props = new Properties();

/* all other properties */

// But, the value deserializer MUST be ByteArraySerializer		
props.put(ConsumerConfig.VALUE_DESERIALIZER_CLASS_CONFIG,
		ByteArrayDeserializer.class);

// Then, instantiate the CloudEvents Kafka Consumer
try(CloudEventsKafkaConsumer<String, AttributesImpl, Much> ceConsumer =
		new CloudEventsKafkaConsumer<>(props,
				Unmarshallers.binary(Much.class))){

	// Subscribe . . .
	ceConsumer.subscribe(Collections.singletonList("the.topic.name"));

	// Pool . . .
	ConsumerRecords<String, CloudEvent<AttributesImpl, Much>> records =
			ceConsumer.poll(Duration.ofMillis(800));

	// Use the records
	records.forEach(cloudevent -> {
		// Do something useful . . .
	});
}
```

#### Structured Content Mode

```java
import java.time.Duration;
import java.util.Collections;
import java.util.Properties;

import org.apache.kafka.clients.consumer.ConsumerConfig;
import org.apache.kafka.clients.consumer.ConsumerRecords;
import org.apache.kafka.common.serialization.ByteArrayDeserializer;

import io.cloudevents.CloudEvent;
import io.cloudevents.kafka.CloudEventsKafkaConsumer;
import io.cloudevents.types.Much;
import io.cloudevents.v1.AttributesImpl;
import io.cloudevents.v1.kafka.Unmarshallers;

// . . .

Properties props = new Properties();

/* all other properties */

// But, the value deserializer MUST be ByteArraySerializer		
props.put(ConsumerConfig.VALUE_DESERIALIZER_CLASS_CONFIG,
		ByteArrayDeserializer.class);

// Then, instantiate the CloudEvents Kafka Consumer
try(CloudEventsKafkaConsumer<String, AttributesImpl, Much> ceConsumer =
		new CloudEventsKafkaConsumer<>(props,
				Unmarshallers.structured(Much.class))){

	// Subscribe . . .
	ceConsumer.subscribe(Collections.singletonList("the.topic.name"));

	// Pool . . .
	ConsumerRecords<String, CloudEvent<AttributesImpl, Much>> records =
			ceConsumer.poll(Duration.ofMillis(800));

	// Use the records
	records.forEach(cloudevent -> {
		// Do something useful . . .
	});
}
```
