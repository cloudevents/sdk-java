/**
 * Copyright 2019 The CloudEvents Authors
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package io.cloudevents.v02.kafka;

import static io.cloudevents.v02.kafka.AttributeMapper.HEADER_PREFIX;
import static io.cloudevents.v02.kafka.Unmarshallers.structured;
import static java.lang.System.getProperty;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

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
import org.apache.kafka.clients.consumer.OffsetResetStrategy;
import org.apache.kafka.clients.producer.KafkaProducer;
import org.apache.kafka.clients.producer.ProducerConfig;
import org.apache.kafka.clients.producer.ProducerRecord;
import org.apache.kafka.common.header.internals.RecordHeader;
import org.apache.kafka.common.header.internals.RecordHeaders;
import org.apache.kafka.common.serialization.ByteArrayDeserializer;
import org.apache.kafka.common.serialization.ByteArraySerializer;
import org.apache.kafka.common.serialization.StringDeserializer;
import org.apache.kafka.common.serialization.StringSerializer;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import io.cloudevents.CloudEvent;
import io.cloudevents.extensions.DistributedTracingExtension;
import io.cloudevents.kafka.CloudEventsKafkaConsumer;
import io.cloudevents.types.Much;
import io.cloudevents.v02.AttributesImpl;
import io.debezium.kafka.KafkaCluster;
import io.debezium.util.Testing;

/**
 * 
 * @author fabiojose
 *
 */
public class KafkaConsumerStructuredTest {
	
	private static final int ONE_BROKER = 1;
	private static final Integer ANY_PARTITION = null;
	private static final Duration TIMEOUT = Duration.ofSeconds(8);

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
	public void should_be_ok_with_all_required_attributes() throws Exception {
		// setup
		Map<String, Object> httpHeaders = new HashMap<>();
		httpHeaders.put("Content-Type", "application/cloudevents+json");
		
		String payload = "{\"data\":{\"wow\":\"yes!\"},\"id\":\"x10\",\"source\":\"/source\",\"specversion\":\"0.2\",\"type\":\"event-type\",\"contenttype\":\"application/json\"}";
		
		Much expected = new Much();
		expected.setWow("yes!");
		
		RecordHeader contenttype = new RecordHeader(HEADER_PREFIX
				+ "contenttype", "application/json".getBytes());
		
		RecordHeaders kafkaHeaders = new RecordHeaders(
				new RecordHeader[]{contenttype});
		
		final String topic = "binary.c";
		
		kafka.addBrokers(ONE_BROKER).startup();
		kafka.createTopics(topic);
		
		Properties producerProperties = 
				kafka.useTo().getProducerProperties("bin.me");
			producerProperties.put(ProducerConfig.KEY_SERIALIZER_CLASS_CONFIG,
					StringSerializer.class);
			producerProperties.put(ProducerConfig.VALUE_SERIALIZER_CLASS_CONFIG,
					ByteArraySerializer.class);
			
		try(KafkaProducer<String, byte[]> producer = 
				new KafkaProducer<>(producerProperties)){
			ProducerRecord<String, byte[]> record = 
				new ProducerRecord<>(topic, ANY_PARTITION, "0x", payload.getBytes(),
						kafkaHeaders);
			
			producer.send(record);
		}
		
		Properties consumerProperties = kafka.useTo()
				.getConsumerProperties("consumer", "consumer.id",OffsetResetStrategy.EARLIEST);
			consumerProperties.put(ConsumerConfig.KEY_DESERIALIZER_CLASS_CONFIG,
					StringDeserializer.class);
			consumerProperties.put(ConsumerConfig.VALUE_DESERIALIZER_CLASS_CONFIG,
					ByteArrayDeserializer.class);
			
		// act
		try(CloudEventsKafkaConsumer<String, AttributesImpl, Much> ceConsumer = 
				new CloudEventsKafkaConsumer<>(consumerProperties, 
						structured(Much.class))){
			
			ceConsumer.subscribe(Collections.singletonList(topic));
			
			ConsumerRecords<String, CloudEvent<AttributesImpl, Much>> records =
					ceConsumer.poll(TIMEOUT);
			
			ConsumerRecord<String, CloudEvent<AttributesImpl, Much>> record =
					records.iterator().next();
			
			CloudEvent<AttributesImpl, Much> actual = record.value();
			assertEquals("x10", actual.getAttributes().getId());
			assertEquals("0.2", actual.getAttributes().getSpecversion());
			assertEquals(URI.create("/source"), actual.getAttributes().getSource());
			assertEquals("event-type", actual.getAttributes().getType());
			assertTrue(actual.getAttributes().getContenttype().isPresent());
			assertEquals("application/json", actual.getAttributes().getContenttype().get());
			
			assertTrue(actual.getData().isPresent());
			assertEquals(expected, actual.getData().get());
		}
	}

	@Test
	public void should_be_ok_with_no_data() throws Exception {
		// setup
		Map<String, Object> httpHeaders = new HashMap<>();
		httpHeaders.put("Content-Type", "application/cloudevents+json");
		
		String payload = "{\"id\":\"x10\",\"source\":\"/source\",\"specversion\":\"0.2\",\"type\":\"event-type\",\"contenttype\":\"application/json\"}";

		RecordHeader contenttype = new RecordHeader(HEADER_PREFIX
				+ "contenttype", "application/json".getBytes());
		
		RecordHeaders kafkaHeaders = new RecordHeaders(
				new RecordHeader[]{contenttype});
		
		final String topic = "binary.c";
		
		kafka.addBrokers(ONE_BROKER).startup();
		kafka.createTopics(topic);
		
		Properties producerProperties = 
				kafka.useTo().getProducerProperties("bin.me");
			producerProperties.put(ProducerConfig.KEY_SERIALIZER_CLASS_CONFIG,
					StringSerializer.class);
			producerProperties.put(ProducerConfig.VALUE_SERIALIZER_CLASS_CONFIG,
					ByteArraySerializer.class);
			
		try(KafkaProducer<String, byte[]> producer = 
				new KafkaProducer<>(producerProperties)){
			ProducerRecord<String, byte[]> record = 
				new ProducerRecord<>(topic, ANY_PARTITION, "0x", payload.getBytes(),
						kafkaHeaders);
			
			producer.send(record);
		}
		
		Properties consumerProperties = kafka.useTo()
				.getConsumerProperties("consumer", "consumer.id",OffsetResetStrategy.EARLIEST);
			consumerProperties.put(ConsumerConfig.KEY_DESERIALIZER_CLASS_CONFIG,
					StringDeserializer.class);
			consumerProperties.put(ConsumerConfig.VALUE_DESERIALIZER_CLASS_CONFIG,
					ByteArrayDeserializer.class);
			
		// act
		try(CloudEventsKafkaConsumer<String, AttributesImpl, Much> ceConsumer = 
				new CloudEventsKafkaConsumer<>(consumerProperties, 
						structured(Much.class))){
			
			ceConsumer.subscribe(Collections.singletonList(topic));
			
			ConsumerRecords<String, CloudEvent<AttributesImpl, Much>> records =
					ceConsumer.poll(TIMEOUT);
			
			ConsumerRecord<String, CloudEvent<AttributesImpl, Much>> record =
					records.iterator().next();
			
			CloudEvent<AttributesImpl, Much> actual = record.value();
			assertEquals("x10", actual.getAttributes().getId());
			assertEquals("0.2", actual.getAttributes().getSpecversion());
			assertEquals(URI.create("/source"), actual.getAttributes().getSource());
			assertEquals("event-type", actual.getAttributes().getType());
			assertTrue(actual.getAttributes().getContenttype().isPresent());
			assertEquals("application/json", actual.getAttributes().getContenttype().get());
			
			assertFalse(actual.getData().isPresent());
		}
	}
	
	@Test
	public void should_tracing_extension_ok() throws Exception {
		// setup
		Map<String, Object> httpHeaders = new HashMap<>();
		httpHeaders.put("Content-Type", "application/cloudevents+json");
		
		String payload = "{\"id\":\"x10\",\"source\":\"/source\",\"specversion\":\"0.2\",\"type\":\"event-type\",\"contenttype\":\"application/json\",\"distributedTracing\":{\"traceparent\":\"0\",\"tracestate\":\"congo=4\"}}";
		
		RecordHeader contenttype = new RecordHeader(HEADER_PREFIX
				+ "contenttype", "application/json".getBytes());
		
		RecordHeaders kafkaHeaders = new RecordHeaders(
				new RecordHeader[]{contenttype});
		
		final String topic = "binary.c";
		
		kafka.addBrokers(ONE_BROKER).startup();
		kafka.createTopics(topic);
		
		Properties producerProperties = 
				kafka.useTo().getProducerProperties("bin.me");
			producerProperties.put(ProducerConfig.KEY_SERIALIZER_CLASS_CONFIG,
					StringSerializer.class);
			producerProperties.put(ProducerConfig.VALUE_SERIALIZER_CLASS_CONFIG,
					ByteArraySerializer.class);
			
		try(KafkaProducer<String, byte[]> producer = 
				new KafkaProducer<>(producerProperties)){
			ProducerRecord<String, byte[]> record = 
				new ProducerRecord<>(topic, ANY_PARTITION, "0x", payload.getBytes(),
						kafkaHeaders);
			
			producer.send(record);
		}
		
		Properties consumerProperties = kafka.useTo()
				.getConsumerProperties("consumer", "consumer.id",OffsetResetStrategy.EARLIEST);
			consumerProperties.put(ConsumerConfig.KEY_DESERIALIZER_CLASS_CONFIG,
					StringDeserializer.class);
			consumerProperties.put(ConsumerConfig.VALUE_DESERIALIZER_CLASS_CONFIG,
					ByteArrayDeserializer.class);
			
		// act
		try(CloudEventsKafkaConsumer<String, AttributesImpl, Much> ceConsumer = 
				new CloudEventsKafkaConsumer<>(consumerProperties, 
						structured(Much.class))){
			
			ceConsumer.subscribe(Collections.singletonList(topic));
			
			ConsumerRecords<String, CloudEvent<AttributesImpl, Much>> records =
					ceConsumer.poll(TIMEOUT);
			
			ConsumerRecord<String, CloudEvent<AttributesImpl, Much>> record =
					records.iterator().next();
			
			CloudEvent<AttributesImpl, Much> actual = record.value();
			assertEquals("x10", actual.getAttributes().getId());
			assertEquals("0.2", actual.getAttributes().getSpecversion());
			assertEquals(URI.create("/source"), actual.getAttributes().getSource());
			assertEquals("event-type", actual.getAttributes().getType());
			assertTrue(actual.getAttributes().getContenttype().isPresent());
			assertEquals("application/json", actual.getAttributes().getContenttype().get());
			assertFalse(actual.getData().isPresent());
			
			Object tracing = actual.getExtensions()
					.get(DistributedTracingExtension.Format.IN_MEMORY_KEY);
			
			assertNotNull(tracing);
			assertTrue(tracing instanceof DistributedTracingExtension);
			DistributedTracingExtension dte = 
					(DistributedTracingExtension)tracing;
			assertEquals("0", dte.getTraceparent());
			assertEquals("congo=4", dte.getTracestate());
		}
	}
//
}
