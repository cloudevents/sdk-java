# CloudEvents Kafka Transport Binding

The impl of Kafka Transport Biding for CloudEvents.

> See spec [here](https://github.com/cloudevents/spec/blob/master/kafka-transport-binding.md)

## How to Use

See some examples of how to use with Kafka Consumer and Kafka Producer.

### Producer

Producing CloudEvents in Kafka.

#### Binary Content Mode

```java
import java.net.URI;
import java.util.Properties;

import org.apache.kafka.clients.producer.KafkaProducer;
import org.apache.kafka.clients.producer.ProducerConfig;
import org.apache.kafka.clients.producer.ProducerRecord;
import org.apache.kafka.common.serialization.ByteArraySerializer;

import io.cloudevents.extensions.ExtensionFormat;
import io.cloudevents.format.BinaryMarshaller;
import io.cloudevents.format.Wire;
import io.cloudevents.format.builder.EventStep;
import io.cloudevents.json.Json;
import io.cloudevents.kafka.CloudEventsKafkaProducer;
import io.cloudevents.v02.Accessor;
import io.cloudevents.v02.AttributesImpl;
import io.cloudevents.v02.CloudEventBuilder;
import io.cloudevents.v02.CloudEventImpl;

// . . .

Properties props = new Properties();

/* all other properties */

// But, the value serializer MUST be ByteArraySerializer
props.put(ProducerConfig.VALUE_SERIALIZER_CLASS_CONFIG,
			ByteArraySerializer.class);

// Instantiate the Kafka Producer
final KafkaProducer<String, byte[]> producer =
		new KafkaProducer<String, byte[]>(props);

// Create your own marshaller: binary or structured
final EventStep<AttributesImpl, String, byte[], byte[]> builder =
	BinaryMarshaller.<AttributesImpl, String, byte[], byte[]>
	  builder()
		.map(AttributesImpl::marshal)
		.map(Accessor::extensionsOf)
		.map(ExtensionFormat::marshal)
		.map(HeaderMapper::map)
		.map(Json::binaryMarshal)
		.builder(Wire<byte[], String, Object>::new);

// Then, instantiate the CloudEvents Kafka Producer
try(CloudEventsKafkaProducer<String, AttributesImpl, String>
		ceProducer = new CloudEventsKafkaProducer<>(producer, builder)){

	// Build an event
	CloudEventImpl<String> ce =
		CloudEventBuilder.<String>builder()
			.withId("x10")
			.withSource(URI.create("/source"))
			.withType("event-type")
			.withContenttype("application/json")
			.withData("Event Data")
			.build();

	// Produce the event
	ceProducer.send(new ProducerRecord<>("your.topic", ce));

}
```

#### Structured Content Mode

TODO

### Consumer

Consuming CloudEvents from Kafka.

#### Binary Content Mode

TODO

#### Structured Content Mode

TODO
