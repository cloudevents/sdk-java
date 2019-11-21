package io.cloudevents.v02.kafka;

import static io.cloudevents.v02.kafka.Marshallers.structured;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;

import java.net.URI;

import org.apache.kafka.clients.producer.MockProducer;
import org.apache.kafka.clients.producer.ProducerRecord;
import org.apache.kafka.clients.producer.RecordMetadata;
import org.apache.kafka.common.serialization.ByteArraySerializer;
import org.apache.kafka.common.serialization.Serdes;
import org.apache.kafka.common.serialization.StringSerializer;
import org.junit.jupiter.api.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import io.cloudevents.extensions.DistributedTracingExtension;
import io.cloudevents.extensions.ExtensionFormat;
import io.cloudevents.kafka.CloudEventsKafkaProducer;
import io.cloudevents.types.Much;
import io.cloudevents.v02.AttributesImpl;
import io.cloudevents.v02.CloudEventBuilder;
import io.cloudevents.v02.CloudEventImpl;

/**
 * 
 * @author fabiojose
 *
 */
public class KafkaProducerStructuredTest {
	private static final Logger log = 
			LoggerFactory.getLogger(KafkaProducerStructuredTest.class);

	@Test
	public void should_be_ok_with_all_required_attributes() throws Exception {
		// setup
		String expected = "{\"data\":{\"wow\":\"nice!\"},\"id\":\"x10\",\"source\":\"/source\",\"specversion\":\"0.2\",\"type\":\"event-type\",\"contenttype\":\"application/json\"}";
		final Much data = new Much();
		data.setWow("nice!");
		
		CloudEventImpl<Much> ce = 
			CloudEventBuilder.<Much>builder()
				.withId("x10")
				.withSource(URI.create("/source"))
				.withType("event-type")
				.withContenttype("application/json")
				.withData(data)
				.build();
		
		final String topic = "binary.t";
		
		MockProducer<String, byte[]> mocked = new MockProducer<String, byte[]>(true,
				new StringSerializer(), new ByteArraySerializer());

		try(CloudEventsKafkaProducer<String, AttributesImpl, Much> 
			ceProducer = new CloudEventsKafkaProducer<>(structured(), mocked)){
			// act
			RecordMetadata metadata = 
				ceProducer.send(new ProducerRecord<>(topic, ce)).get();
			
			log.info("Producer metadata {}", metadata);
			
			assertFalse(mocked.history().isEmpty());
			mocked.history().forEach(actual -> {
				// assert
				byte[] actualData = actual.value();
				assertNotNull(actualData);
				
				String actualJson = Serdes.String().deserializer()
						.deserialize(null, actualData);
				assertEquals(expected, actualJson);
			});
		}
	}

	@Test
	public void should_be_ok_with_no_data() throws Exception {
		// setup
		String expected = "{\"id\":\"x10\",\"source\":\"/source\",\"specversion\":\"0.2\",\"type\":\"event-type\",\"contenttype\":\"application/json\"}";
		
		CloudEventImpl<Much> ce = 
			CloudEventBuilder.<Much>builder()
				.withId("x10")
				.withSource(URI.create("/source"))
				.withType("event-type")
				.withContenttype("application/json")
				.build();
		
		final String topic = "binary.t";
		
		MockProducer<String, byte[]> mocked = new MockProducer<String, byte[]>(true,
				new StringSerializer(), new ByteArraySerializer());

		try(CloudEventsKafkaProducer<String, AttributesImpl, Much> 
			ceProducer = new CloudEventsKafkaProducer<>(structured(), mocked)){
			// act
			RecordMetadata metadata = 
				ceProducer.send(new ProducerRecord<>(topic, ce)).get();
			
			log.info("Producer metadata {}", metadata);
			
			assertFalse(mocked.history().isEmpty());
			mocked.history().forEach(actual -> {
				// assert
				byte[] actualData = actual.value();
				assertNotNull(actualData);
				
				String actualJson = Serdes.String().deserializer()
						.deserialize(null, actualData);
				assertEquals(expected, actualJson);
			});
		}
	}
	
	@Test
	public void should_tracing_extension_ok() throws Exception {
		// setup
		String expected = "{\"id\":\"x10\",\"source\":\"/source\",\"specversion\":\"0.2\",\"type\":\"event-type\",\"contenttype\":\"application/json\",\"distributedTracing\":{\"traceparent\":\"0\",\"tracestate\":\"congo=4\"}}";
		
		final DistributedTracingExtension dt = new DistributedTracingExtension();
		dt.setTraceparent("0");
		dt.setTracestate("congo=4");
		
		final ExtensionFormat tracing = new DistributedTracingExtension.Format(dt);
		
		CloudEventImpl<Much> ce = 
			CloudEventBuilder.<Much>builder()
				.withId("x10")
				.withSource(URI.create("/source"))
				.withType("event-type")
				.withContenttype("application/json")
				.withExtension(tracing)
				.build();
		
		final String topic = "binary.t";
		
		MockProducer<String, byte[]> mocked = new MockProducer<String, byte[]>(true,
				new StringSerializer(), new ByteArraySerializer());
			
		try(CloudEventsKafkaProducer<String, AttributesImpl, Much> 
			ceProducer = new CloudEventsKafkaProducer<>(structured(), mocked)){
			// act
			RecordMetadata metadata = 
				ceProducer.send(new ProducerRecord<>(topic, ce)).get();
			
			log.info("Producer metadata {}", metadata);
			
			assertFalse(mocked.history().isEmpty());
			mocked.history().forEach(actual -> {
				// assert
				byte[] actualData = actual.value();
				assertNotNull(actualData);
				
				String actualJson = Serdes.String().deserializer()
						.deserialize(null, actualData);
				assertEquals(expected, actualJson);
			});
		}
	}
	
	@Test
	public void should_be_with_wrong_value_serializer() throws Exception {
		// setup
		String expected = "{\"id\":\"x10\",\"source\":\"/source\",\"specversion\":\"0.2\",\"type\":\"event-type\",\"contenttype\":\"application/json\",\"distributedTracing\":{\"traceparent\":\"0\",\"tracestate\":\"congo=4\"}}";
		
		final DistributedTracingExtension dt = new DistributedTracingExtension();
		dt.setTraceparent("0");
		dt.setTracestate("congo=4");
		
		final ExtensionFormat tracing = new DistributedTracingExtension.Format(dt);
		
		CloudEventImpl<Much> ce = 
			CloudEventBuilder.<Much>builder()
				.withId("x10")
				.withSource(URI.create("/source"))
				.withType("event-type")
				.withContenttype("application/json")
				.withExtension(tracing)
				.build();
		
		final String topic = "binary.t";
		
		MockProducer<String, byte[]> mocked = new MockProducer<String, byte[]>(true,
				new StringSerializer(), new ByteArraySerializer());
			
		try(CloudEventsKafkaProducer<String, AttributesImpl, Much> 
			ceProducer = new CloudEventsKafkaProducer<>(structured(), mocked)){
			// act
			RecordMetadata metadata = 
				ceProducer.send(new ProducerRecord<>(topic, ce)).get();
			
			log.info("Producer metadata {}", metadata);
			
			assertFalse(mocked.history().isEmpty());
			mocked.history().forEach(actual -> {
				// assert
				byte[] actualData = actual.value();
				assertNotNull(actualData);
				
				String actualJson = Serdes.String().deserializer()
						.deserialize(null, actualData);
				assertEquals(expected, actualJson);
			});
		}
	}
}
