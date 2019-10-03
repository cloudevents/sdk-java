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
package io.cloudevents.v03.kafka;

import static io.cloudevents.v03.kafka.Marshallers.binary;
import static java.lang.System.getProperty;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;

import java.io.File;
import java.net.URI;
import java.time.Duration;
import java.util.Collections;
import java.util.Properties;

import org.apache.kafka.clients.consumer.ConsumerConfig;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.apache.kafka.clients.consumer.ConsumerRecords;
import org.apache.kafka.clients.consumer.KafkaConsumer;
import org.apache.kafka.clients.consumer.OffsetResetStrategy;
import org.apache.kafka.clients.producer.ProducerConfig;
import org.apache.kafka.clients.producer.ProducerRecord;
import org.apache.kafka.clients.producer.RecordMetadata;
import org.apache.kafka.common.header.Header;
import org.apache.kafka.common.serialization.ByteArrayDeserializer;
import org.apache.kafka.common.serialization.ByteArraySerializer;
import org.apache.kafka.common.serialization.Deserializer;
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
import io.cloudevents.kafka.CloudEventsKafkaProducer;
import io.cloudevents.types.Much;
import io.cloudevents.v03.AttributesImpl;
import io.cloudevents.v03.CloudEventBuilder;
import io.cloudevents.v03.CloudEventImpl;
import io.debezium.junit.SkipLongRunning;
import io.debezium.kafka.KafkaCluster;
import io.debezium.util.Testing;

/**
 * 
 * @author fabiojose
 *
 */
public class KafkaProducerBinaryTest {
	private static final Logger log = 
			LoggerFactory.getLogger(KafkaProducerBinaryTest.class);
		
	private static final Deserializer<String> DESERIALIZER = 
		Serdes.String().deserializer();
	
	private static final int ONE_BROKER = 1;
	private static final Duration TIMEOUT = Duration.ofSeconds(5);

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
	public void should_throws_when_producer_is_null() {
		assertThrows(NullPointerException.class, () -> {
			new CloudEventsKafkaProducer<String, AttributesImpl, Much>(null,
					(e) -> null);
		});
	}
	
	@Test
	@SkipLongRunning
	public void should_be_ok_with_all_required_attributes() throws Exception {
		// setup
		String dataJson = "{\"wow\":\"nice!\"}";
		final Much data = new Much();
		data.setWow("nice!");
		
		CloudEventImpl<Much> ce = 
			CloudEventBuilder.<Much>builder()
				.withId("x10")
				.withSource(URI.create("/source"))
				.withType("event-type")
				.withDatacontenttype("application/json")
				.withSubject("subject")
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
					
		try(CloudEventsKafkaProducer<String, AttributesImpl, Much> 
			ceProducer = new CloudEventsKafkaProducer<>(producerProperties, binary())){
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
			assertNotNull(actual);
			Header specversion = 
				actual.headers().lastHeader("ce_specversion");
			
			assertNotNull(specversion);
			assertEquals("0.3", DESERIALIZER
					.deserialize(null, specversion.value()));
			
			Header id = 
					actual.headers().lastHeader("ce_id");
			assertNotNull(id);
			assertEquals("x10", DESERIALIZER
					.deserialize(null, id.value()));
			
			Header source =
					actual.headers().lastHeader("ce_source");
			assertNotNull(source);
			assertEquals("/source", DESERIALIZER
					.deserialize(null, source.value()));
			
			Header type = 
					actual.headers().lastHeader("ce_type");
			assertNotNull(source);
			assertEquals("event-type", DESERIALIZER
					.deserialize(null, type.value()));
			
			Header subject = 
					actual.headers().lastHeader("ce_subject");
			assertNotNull(subject);
			assertEquals("subject", DESERIALIZER
					.deserialize(null, subject.value()));
			
			byte[] actualData = actual.value();
			assertNotNull(actualData);
			assertEquals(dataJson, DESERIALIZER
					.deserialize(null, actualData));
		}
	}
	
	@Test
	public void should_be_ok_with_no_data() throws Exception {
		// setup
		CloudEventImpl<Much> ce = 
			CloudEventBuilder.<Much>builder()
				.withId("x10")
				.withSource(URI.create("/source"))
				.withType("event-type")
				.withSchemaurl(URI.create("/schema"))
				.withDatacontenttype("application/json")
				.withSubject("subject")
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
					
		try(CloudEventsKafkaProducer<String, AttributesImpl, Much> 
			ceProducer = new CloudEventsKafkaProducer<>(producerProperties, binary())){
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
			assertNotNull(actual);
			Header specversion = 
				actual.headers().lastHeader("ce_specversion");
			
			assertNotNull(specversion);
			assertEquals("0.3", DESERIALIZER
					.deserialize(null, specversion.value()));
			
			Header id = 
					actual.headers().lastHeader("ce_id");
			assertNotNull(id);
			assertEquals("x10", DESERIALIZER
					.deserialize(null, id.value()));
			
			Header source =
					actual.headers().lastHeader("ce_source");
			assertNotNull(source);
			assertEquals("/source", DESERIALIZER
					.deserialize(null, source.value()));
			
			Header type = 
					actual.headers().lastHeader("ce_type");
			assertNotNull(source);
			assertEquals("event-type", DESERIALIZER
					.deserialize(null, type.value()));
			
			Header schemaurl = 
					actual.headers().lastHeader("ce_schemaurl");
			assertNotNull(source);
			assertEquals("/schema", DESERIALIZER
					.deserialize(null, schemaurl.value()));
			
			Header subject = 
					actual.headers().lastHeader("ce_subject");
			assertNotNull(subject);
			assertEquals("subject", DESERIALIZER
					.deserialize(null, subject.value()));
		}
	}
	
	@Test
	@SkipLongRunning
	public void should_tracing_extension_ok() throws Exception {
		// setup
		final DistributedTracingExtension dt = new DistributedTracingExtension();
		dt.setTraceparent("0");
		dt.setTracestate("congo=4");
		
		final ExtensionFormat tracing = new DistributedTracingExtension.Format(dt);
		
		final Much data = new Much();
		data.setWow("nice!");
		
		CloudEventImpl<Much> ce = 
			CloudEventBuilder.<Much>builder()
				.withId("x10")
				.withSource(URI.create("/source"))
				.withType("event-type")
				.withSchemaurl(URI.create("/schema"))
				.withDatacontenttype("application/json")
				.withSubject("subject")
				.withData(data)
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
		
		try(CloudEventsKafkaProducer<String, AttributesImpl, Much> 
			ceProducer = new CloudEventsKafkaProducer<>(producerProperties, binary())){
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
			assertNotNull(actual);
			Header traceparent = 
				actual.headers().lastHeader("traceparent");
			
			assertNotNull(traceparent);
			assertEquals("0", DESERIALIZER
					.deserialize(null, traceparent.value()));
			
			Header tracestate = 
					actual.headers().lastHeader("tracestate");
			assertNotNull(tracestate);
			assertEquals("congo=4", DESERIALIZER
					.deserialize(null, tracestate.value()));
		}
	}
}
