package io.cloudevents.v02.kafka;

import static java.lang.System.getProperty;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

import java.io.File;
import java.net.URI;
import java.time.Duration;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.Properties;

import org.apache.kafka.clients.consumer.ConsumerConfig;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.apache.kafka.clients.consumer.ConsumerRecords;
import org.apache.kafka.clients.consumer.KafkaConsumer;
import org.apache.kafka.clients.consumer.OffsetResetStrategy;
import org.apache.kafka.clients.producer.KafkaProducer;
import org.apache.kafka.clients.producer.ProducerConfig;
import org.apache.kafka.clients.producer.ProducerRecord;
import org.apache.kafka.clients.producer.RecordMetadata;
import org.apache.kafka.common.serialization.ByteArrayDeserializer;
import org.apache.kafka.common.serialization.ByteArraySerializer;
import org.apache.kafka.common.serialization.Serdes;
import org.apache.kafka.common.serialization.StringDeserializer;
import org.apache.kafka.common.serialization.StringSerializer;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import io.cloudevents.extensions.DistributedTracingExtension;
import io.cloudevents.extensions.ExtensionFormat;
import io.cloudevents.format.StructuredMarshaller;
import io.cloudevents.format.builder.EventStep;
import io.cloudevents.json.Json;
import io.cloudevents.kafka.CloudEventsKafkaProducer;
import io.cloudevents.types.Much;
import io.cloudevents.v02.AttributesImpl;
import io.cloudevents.v02.CloudEventBuilder;
import io.cloudevents.v02.CloudEventImpl;
import io.debezium.junit.SkipLongRunning;
import io.debezium.kafka.KafkaCluster;
import io.debezium.util.Testing;

/**
 * 
 * @author fabiojose
 *
 */
public class KafkaProducerStructuredTest {
	private static final Logger log = 
			LoggerFactory.getLogger(KafkaProducerStructuredTest.class);

	private static final int ONE_BROKER = 1;
	private static final Duration TIMEOUT = Duration.ofSeconds(5);
	
	private static final Map<String, Object> NO_HEADERS = 
			new HashMap<>();

	private KafkaCluster kafka;
	private File data;

	@BeforeEach
	public void beforeEach() {
		data = Testing.Files.createTestingDirectory("cluster");
		
		int zk = Integer.parseInt(getProperty("zookeeper.port"));
		int kf = Integer.parseInt(getProperty("kafka.port"));
		
		kafka = new KafkaCluster()
				.usingDirectory(data)
				.deleteDataPriorToStartup(true)
				.deleteDataUponShutdown(true)
				.withPorts(zk, kf);
	}

	@AfterEach
	public void afterEach() {
		kafka.shutdown();
		Testing.Files.delete(data);
	}

	@Test
	@SkipLongRunning
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
		
		kafka.addBrokers(ONE_BROKER).startup();
		kafka.createTopics(topic);
		
		Properties producerProperties = 
			kafka.useTo().getProducerProperties("bin.me");
		producerProperties.put(ProducerConfig.KEY_SERIALIZER_CLASS_CONFIG,
				StringSerializer.class);
		producerProperties.put(ProducerConfig.VALUE_SERIALIZER_CLASS_CONFIG,
				ByteArraySerializer.class);
		
		Properties consumerProperties = kafka.useTo()
				.getConsumerProperties("consumer", "consumer.id",OffsetResetStrategy.EARLIEST);
			consumerProperties.put(ConsumerConfig.KEY_DESERIALIZER_CLASS_CONFIG,
					StringDeserializer.class);
			consumerProperties.put(ConsumerConfig.VALUE_DESERIALIZER_CLASS_CONFIG,
					ByteArrayDeserializer.class);
		
		final KafkaProducer<String, byte[]> producer = 
				new KafkaProducer<String, byte[]>(producerProperties);
		
	    final EventStep<AttributesImpl, Much, byte[], byte[]> builder = 
			StructuredMarshaller.<AttributesImpl, Much, byte[], byte[]>
			  builder()
				.mime("content-type", "application/cloudevents+json".getBytes())
				.map((event) -> 
					Json.binaryMarshal(event, NO_HEADERS))
				.skip();
			
		try(CloudEventsKafkaProducer<String, AttributesImpl, Much> 
			ceProducer = new CloudEventsKafkaProducer<>(producer, builder)){
			// act
			RecordMetadata metadata = 
				ceProducer.send(new ProducerRecord<>(topic, ce)).get();
			
			log.info("Producer metadata {}", metadata);
		}
		
		try(KafkaConsumer<String, byte[]> consumer = 
				new KafkaConsumer<>(consumerProperties)){
			consumer.subscribe(Collections.singletonList(topic));
			
			ConsumerRecords<String, byte[]> records = 
					consumer.poll(TIMEOUT);
			
			ConsumerRecord<String, byte[]> actual =
					records.iterator().next();
			
			// assert
			byte[] actualData = actual.value();
			assertNotNull(actualData);
			
			String actualJson = Serdes.String().deserializer()
					.deserialize(null, actualData);
			assertEquals(expected, actualJson);
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
		
		kafka.addBrokers(ONE_BROKER).startup();
		kafka.createTopics(topic);
		
		Properties producerProperties = 
			kafka.useTo().getProducerProperties("bin.me");
		producerProperties.put(ProducerConfig.KEY_SERIALIZER_CLASS_CONFIG,
				StringSerializer.class);
		producerProperties.put(ProducerConfig.VALUE_SERIALIZER_CLASS_CONFIG,
				ByteArraySerializer.class);
		
		Properties consumerProperties = kafka.useTo()
				.getConsumerProperties("consumer", "consumer.id",OffsetResetStrategy.EARLIEST);
			consumerProperties.put(ConsumerConfig.KEY_DESERIALIZER_CLASS_CONFIG,
					StringDeserializer.class);
			consumerProperties.put(ConsumerConfig.VALUE_DESERIALIZER_CLASS_CONFIG,
					ByteArrayDeserializer.class);
		
		final KafkaProducer<String, byte[]> producer = 
				new KafkaProducer<String, byte[]>(producerProperties);
		
	    final EventStep<AttributesImpl, Much, byte[], byte[]> builder = 
			StructuredMarshaller.<AttributesImpl, Much, byte[], byte[]>
			  builder()
				.mime("content-type", "application/cloudevents+json".getBytes())
				.map((event) -> 
					Json.binaryMarshal(event, NO_HEADERS))
				.skip();
			
		try(CloudEventsKafkaProducer<String, AttributesImpl, Much> 
			ceProducer = new CloudEventsKafkaProducer<>(producer, builder)){
			// act
			RecordMetadata metadata = 
				ceProducer.send(new ProducerRecord<>(topic, ce)).get();
			
			log.info("Producer metadata {}", metadata);
		}
		
		try(KafkaConsumer<String, byte[]> consumer = 
				new KafkaConsumer<>(consumerProperties)){
			consumer.subscribe(Collections.singletonList(topic));
			
			ConsumerRecords<String, byte[]> records = 
					consumer.poll(TIMEOUT);
			
			ConsumerRecord<String, byte[]> actual =
					records.iterator().next();
			
			// assert
			byte[] actualData = actual.value();
			assertNotNull(actualData);
			
			String actualJson = Serdes.String().deserializer()
					.deserialize(null, actualData);
			assertEquals(expected, actualJson);
		}
	}
	
	@Test
	@SkipLongRunning
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
		
		kafka.addBrokers(ONE_BROKER).startup();
		kafka.createTopics(topic);
		
		Properties producerProperties = 
			kafka.useTo().getProducerProperties("bin.me");
		producerProperties.put(ProducerConfig.KEY_SERIALIZER_CLASS_CONFIG,
				StringSerializer.class);
		producerProperties.put(ProducerConfig.VALUE_SERIALIZER_CLASS_CONFIG,
				ByteArraySerializer.class);
		
		Properties consumerProperties = kafka.useTo()
				.getConsumerProperties("consumer", "consumer.id",OffsetResetStrategy.EARLIEST);
			consumerProperties.put(ConsumerConfig.KEY_DESERIALIZER_CLASS_CONFIG,
					StringDeserializer.class);
			consumerProperties.put(ConsumerConfig.VALUE_DESERIALIZER_CLASS_CONFIG,
					ByteArrayDeserializer.class);
		
		final KafkaProducer<String, byte[]> producer = 
				new KafkaProducer<String, byte[]>(producerProperties);
		
	    final EventStep<AttributesImpl, Much, byte[], byte[]> builder = 
			StructuredMarshaller.<AttributesImpl, Much, byte[], byte[]>
			  builder()
				.mime("content-type", "application/cloudevents+json".getBytes())
				.map((event) -> 
					Json.binaryMarshal(event, NO_HEADERS))
				.skip();
			
		try(CloudEventsKafkaProducer<String, AttributesImpl, Much> 
			ceProducer = new CloudEventsKafkaProducer<>(producer, builder)){
			// act
			RecordMetadata metadata = 
				ceProducer.send(new ProducerRecord<>(topic, ce)).get();
			
			log.info("Producer metadata {}", metadata);
		}
		
		try(KafkaConsumer<String, byte[]> consumer = 
				new KafkaConsumer<>(consumerProperties)){
			consumer.subscribe(Collections.singletonList(topic));
			
			ConsumerRecords<String, byte[]> records = 
					consumer.poll(TIMEOUT);
			
			ConsumerRecord<String, byte[]> actual =
					records.iterator().next();
			
			// assert
			byte[] actualData = actual.value();
			assertNotNull(actualData);
			
			String actualJson = Serdes.String().deserializer()
					.deserialize(null, actualData);
			assertEquals(expected, actualJson);
		}
	}
}
