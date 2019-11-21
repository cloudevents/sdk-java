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

import static io.cloudevents.v03.kafka.AttributeMapper.HEADER_PREFIX;
import static io.cloudevents.v03.kafka.Unmarshallers.structured;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.net.URI;
import java.time.Duration;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.apache.kafka.clients.consumer.ConsumerRecords;
import org.apache.kafka.clients.consumer.MockConsumer;
import org.apache.kafka.clients.consumer.OffsetResetStrategy;
import org.apache.kafka.common.TopicPartition;
import org.apache.kafka.common.header.internals.RecordHeader;
import org.apache.kafka.common.header.internals.RecordHeaders;
import org.apache.kafka.common.record.TimestampType;
import org.junit.jupiter.api.Test;

import io.cloudevents.CloudEvent;
import io.cloudevents.extensions.DistributedTracingExtension;
import io.cloudevents.kafka.CloudEventsKafkaConsumer;
import io.cloudevents.types.Much;
import io.cloudevents.v03.AttributesImpl;

/**
 * 
 * @author fabiojose
 *
 */
public class KafkaConsumerStructuredTest {

	private static final Duration TIMEOUT = Duration.ofSeconds(8);

	@Test
	public void should_be_ok_with_all_required_attributes() throws Exception {
		// setup
		Map<String, Object> httpHeaders = new HashMap<>();
		httpHeaders.put("Content-Type", "application/cloudevents+json");
		
		String payload = "{\"data\":{\"wow\":\"yes!\"},\"id\":\"x10\",\"source\":\"/source\",\"specversion\":\"0.3\",\"type\":\"event-type\",\"datacontenttype\":\"application/json\",\"subject\":\"subject\"}";
		
		Much expected = new Much();
		expected.setWow("yes!");
		
		RecordHeader contenttype = new RecordHeader(HEADER_PREFIX
				+ "contenttype", "application/json".getBytes());
		
		RecordHeaders kafkaHeaders = new RecordHeaders(
				new RecordHeader[]{contenttype});
		
		final String topic = "binary.c";
		
		MockConsumer<String, byte[]> mockConsumer = 
				new MockConsumer<String, byte[]>(OffsetResetStrategy.EARLIEST);
			
		// act
		try(CloudEventsKafkaConsumer<String, AttributesImpl, Much> ceConsumer = 
				new CloudEventsKafkaConsumer<>(structured(Much.class),
						mockConsumer)){
			
			ceConsumer.assign(Collections.singletonList(new TopicPartition(topic, 0)));

			mockConsumer.seek(new TopicPartition(topic, 0), 0);
			mockConsumer.addRecord(
				new ConsumerRecord<String, byte[]>(topic, 0, 0L, 0,
						TimestampType.CREATE_TIME, 0L, 0, 0, "0xk",
						payload.getBytes(), kafkaHeaders)
			);
			
			ConsumerRecords<String, CloudEvent<AttributesImpl, Much>> records =
					ceConsumer.poll(TIMEOUT);
			
			ConsumerRecord<String, CloudEvent<AttributesImpl, Much>> record =
					records.iterator().next();
			
			CloudEvent<AttributesImpl, Much> actual = record.value();
			assertEquals("x10", actual.getAttributes().getId());
			assertEquals("0.3", actual.getAttributes().getSpecversion());
			assertEquals(URI.create("/source"), actual.getAttributes().getSource());
			assertEquals("event-type", actual.getAttributes().getType());
			assertTrue(actual.getAttributes().getDatacontenttype().isPresent());
			assertEquals("application/json", actual.getAttributes().getDatacontenttype().get());
			assertTrue(actual.getAttributes().getSubject().isPresent());
			assertEquals("subject", actual.getAttributes().getSubject().get());
			
			assertTrue(actual.getData().isPresent());
			assertEquals(expected, actual.getData().get());
		}
	}

	@Test
	public void should_be_ok_with_no_data() throws Exception {
		// setup
		Map<String, Object> httpHeaders = new HashMap<>();
		httpHeaders.put("Content-Type", "application/cloudevents+json");
		
		String payload = "{\"id\":\"x10\",\"source\":\"/source\",\"specversion\":\"0.3\",\"type\":\"event-type\",\"datacontenttype\":\"application/json\"}";

		RecordHeader contenttype = new RecordHeader(HEADER_PREFIX
				+ "contenttype", "application/json".getBytes());
		
		RecordHeaders kafkaHeaders = new RecordHeaders(
				new RecordHeader[]{contenttype});
		
		final String topic = "binary.c";
		
		MockConsumer<String, byte[]> mockConsumer = 
				new MockConsumer<String, byte[]>(OffsetResetStrategy.EARLIEST);
			
		// act
		try(CloudEventsKafkaConsumer<String, AttributesImpl, Much> ceConsumer = 
				new CloudEventsKafkaConsumer<>(structured(Much.class), mockConsumer)){
			
			ceConsumer.assign(Collections.singletonList(new TopicPartition(topic, 0)));

			mockConsumer.seek(new TopicPartition(topic, 0), 0);
			mockConsumer.addRecord(
				new ConsumerRecord<String, byte[]>(topic, 0, 0L, 0,
						TimestampType.CREATE_TIME, 0L, 0, 0, "0xk",
						payload.getBytes(), kafkaHeaders)
			);
			
			ConsumerRecords<String, CloudEvent<AttributesImpl, Much>> records =
					ceConsumer.poll(TIMEOUT);
			
			ConsumerRecord<String, CloudEvent<AttributesImpl, Much>> record =
					records.iterator().next();
			
			CloudEvent<AttributesImpl, Much> actual = record.value();
			assertEquals("x10", actual.getAttributes().getId());
			assertEquals("0.3", actual.getAttributes().getSpecversion());
			assertEquals(URI.create("/source"), actual.getAttributes().getSource());
			assertEquals("event-type", actual.getAttributes().getType());
			assertTrue(actual.getAttributes().getDatacontenttype().isPresent());
			assertEquals("application/json", actual.getAttributes().getDatacontenttype().get());
			
			assertFalse(actual.getData().isPresent());
		}
	}
	
	@Test
	public void should_tracing_extension_ok() throws Exception {
		// setup
		Map<String, Object> httpHeaders = new HashMap<>();
		httpHeaders.put("Content-Type", "application/cloudevents+json");
		
		String payload = "{\"id\":\"x10\",\"source\":\"/source\",\"specversion\":\"0.3\",\"type\":\"event-type\",\"datacontenttype\":\"application/json\",\"distributedTracing\":{\"traceparent\":\"0\",\"tracestate\":\"congo=4\"}}";
		
		RecordHeader contenttype = new RecordHeader(HEADER_PREFIX
				+ "contenttype", "application/json".getBytes());
		
		RecordHeaders kafkaHeaders = new RecordHeaders(
				new RecordHeader[]{contenttype});
		
		final String topic = "binary.c";
		
		MockConsumer<String, byte[]> mockConsumer = 
				new MockConsumer<String, byte[]>(OffsetResetStrategy.EARLIEST);
			
		// act
		try(CloudEventsKafkaConsumer<String, AttributesImpl, Much> ceConsumer = 
				new CloudEventsKafkaConsumer<>(structured(Much.class), mockConsumer)){
			
			ceConsumer.assign(Collections.singletonList(new TopicPartition(topic, 0)));

			mockConsumer.seek(new TopicPartition(topic, 0), 0);
			mockConsumer.addRecord(
				new ConsumerRecord<String, byte[]>(topic, 0, 0L, 0,
						TimestampType.CREATE_TIME, 0L, 0, 0, "0xk",
						payload.getBytes(), kafkaHeaders)
			);
			
			ConsumerRecords<String, CloudEvent<AttributesImpl, Much>> records =
					ceConsumer.poll(TIMEOUT);
			
			ConsumerRecord<String, CloudEvent<AttributesImpl, Much>> record =
					records.iterator().next();
			
			CloudEvent<AttributesImpl, Much> actual = record.value();
			assertEquals("x10", actual.getAttributes().getId());
			assertEquals("0.3", actual.getAttributes().getSpecversion());
			assertEquals(URI.create("/source"), actual.getAttributes().getSource());
			assertEquals("event-type", actual.getAttributes().getType());
			assertTrue(actual.getAttributes().getDatacontenttype().isPresent());
			assertEquals("application/json", actual.getAttributes().getDatacontenttype().get());
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
	
	@Test
	public void should_be_with_wrong_value_deserializer() throws Exception {
		// setup
		Map<String, Object> httpHeaders = new HashMap<>();
		httpHeaders.put("Content-Type", "application/cloudevents+json");
		
		String payload = "{\"id\":\"x10\",\"source\":\"/source\",\"specversion\":\"0.3\",\"type\":\"event-type\",\"datacontenttype\":\"application/json\",\"distributedTracing\":{\"traceparent\":\"0\",\"tracestate\":\"congo=4\"}}";
		
		RecordHeader contenttype = new RecordHeader(HEADER_PREFIX
				+ "contenttype", "application/json".getBytes());
		
		RecordHeaders kafkaHeaders = new RecordHeaders(
				new RecordHeader[]{contenttype});
		
		final String topic = "binary.c";
		
		MockConsumer<String, byte[]> mockConsumer = 
				new MockConsumer<String, byte[]>(OffsetResetStrategy.EARLIEST);
			
		// act
		try(CloudEventsKafkaConsumer<String, AttributesImpl, Much> ceConsumer = 
				new CloudEventsKafkaConsumer<>(structured(Much.class), mockConsumer)){
			
			ceConsumer.assign(Collections.singletonList(new TopicPartition(topic, 0)));

			mockConsumer.seek(new TopicPartition(topic, 0), 0);
			mockConsumer.addRecord(
				new ConsumerRecord<String, byte[]>(topic, 0, 0L, 0,
						TimestampType.CREATE_TIME, 0L, 0, 0, "0xk",
						payload.getBytes(), kafkaHeaders)
			);
			
			ConsumerRecords<String, CloudEvent<AttributesImpl, Much>> records =
					ceConsumer.poll(TIMEOUT);
			
			ConsumerRecord<String, CloudEvent<AttributesImpl, Much>> record =
					records.iterator().next();
			
			CloudEvent<AttributesImpl, Much> actual = record.value();
			assertEquals("x10", actual.getAttributes().getId());
			assertEquals("0.3", actual.getAttributes().getSpecversion());
			assertEquals(URI.create("/source"), actual.getAttributes().getSource());
			assertEquals("event-type", actual.getAttributes().getType());
			assertTrue(actual.getAttributes().getDatacontenttype().isPresent());
			assertEquals("application/json", actual.getAttributes().getDatacontenttype().get());
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
}
