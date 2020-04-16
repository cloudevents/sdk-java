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
import static io.cloudevents.v02.kafka.Unmarshallers.binary;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.net.URI;
import java.time.Duration;
import java.util.Collections;
import java.util.Map;
import java.util.function.Supplier;

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
import io.cloudevents.format.builder.HeadersStep;
import io.cloudevents.format.builder.PayloadStep;
import io.cloudevents.kafka.CloudEventsKafkaConsumer;
import io.cloudevents.types.Much;
import io.cloudevents.v02.AttributesImpl;

/**
 *
 * @author fabiojose
 *
 */
public class KafkaConsumerBinaryTest {

	private static final Duration TIMEOUT = Duration.ofSeconds(30);

	@Test
	public void should_throws_when_configuration_is_null() {
		assertThrows(NullPointerException.class, () -> {
			new CloudEventsKafkaConsumer<String, AttributesImpl, Much>(null,
					new HeadersStep<AttributesImpl, Much, byte[]>() {
						@Override
						public PayloadStep<AttributesImpl, Much, byte[]> withHeaders(
								Supplier<Map<String, Object>> headers) {
							return null;
						}
			});
		});
	}

	@Test
	public void should_be_ok_with_all_required_attributes() throws Exception {
		// setup
		Much expected = new Much();
		expected.setWow("amz");

		RecordHeader id = new RecordHeader(HEADER_PREFIX
				+ "id", "0x44".getBytes());
		RecordHeader specversion = new RecordHeader(HEADER_PREFIX
				+ "specversion", "0.2".getBytes());
		RecordHeader source = new RecordHeader(HEADER_PREFIX
				+ "source", "/source".getBytes());
		RecordHeader type = new RecordHeader(HEADER_PREFIX
				+ "type", "type".getBytes());

		RecordHeader contenttype = new RecordHeader(HEADER_PREFIX
				+ "contenttype", "application/json".getBytes());

		RecordHeaders kafkaHeaders = new RecordHeaders(
				new RecordHeader[]{id, specversion, source, type, contenttype});

		byte[] payload = "{\"wow\" : \"amz\"}".getBytes();

		final String topic = "binary.c";

		MockConsumer<String, byte[]> mockConsumer =
				new MockConsumer<String, byte[]>(OffsetResetStrategy.EARLIEST);

		// act
		try(CloudEventsKafkaConsumer<String, AttributesImpl, Much> ceConsumer =
				new CloudEventsKafkaConsumer<>(binary(Much.class), mockConsumer)){

			ceConsumer.assign(Collections.singletonList(new TopicPartition(topic, 0)));

			mockConsumer.seek(new TopicPartition(topic, 0), 0);
			mockConsumer.addRecord(
				new ConsumerRecord<String, byte[]>(topic, 0, 0L, 0,
						TimestampType.CREATE_TIME, 0L, 0, 0, "0xk", payload, kafkaHeaders)
			);

			ConsumerRecords<String, CloudEvent<AttributesImpl, Much>> records =
					ceConsumer.poll(TIMEOUT);

			assertFalse(records.isEmpty());

			ConsumerRecord<String, CloudEvent<AttributesImpl, Much>> record =
					records.iterator().next();

			// assert
			CloudEvent<AttributesImpl, Much> actual = record.value();
			assertEquals("0x44", actual.getAttributes().getId());
			assertEquals("0.2", actual.getAttributes().getSpecVersion());
			assertEquals(URI.create("/source"), actual.getAttributes().getSource());
			assertEquals("type", actual.getAttributes().getType());
			assertTrue(actual.getAttributes().getContenttype().isPresent());
			assertEquals("application/json", actual.getAttributes().getContenttype().get());
			assertTrue(actual.getData().isPresent());
			assertEquals(expected, actual.getData().get());
		}
	}

	@Test
	public void should_be_ok_with_no_data() throws Exception {
		// setup
		RecordHeader id = new RecordHeader(HEADER_PREFIX
				+ "id", "0x44".getBytes());
		RecordHeader specversion = new RecordHeader(HEADER_PREFIX
				+ "specversion", "0.2".getBytes());
		RecordHeader source = new RecordHeader(HEADER_PREFIX
				+ "source", "/source".getBytes());
		RecordHeader type = new RecordHeader(HEADER_PREFIX
				+ "type", "type".getBytes());

		RecordHeader contenttype = new RecordHeader(HEADER_PREFIX
				+ "contenttype", "application/json".getBytes());

		RecordHeaders kafkaHeaders = new RecordHeaders(
				new RecordHeader[]{id, specversion, source, type, contenttype});

		byte[] payload = null;

		final String topic = "binary.c";

		MockConsumer<String, byte[]> mockConsumer =
				new MockConsumer<String, byte[]>(OffsetResetStrategy.EARLIEST);

		// act
		try(CloudEventsKafkaConsumer<String, AttributesImpl, Much> ceConsumer =
				new CloudEventsKafkaConsumer<>(binary(Much.class), mockConsumer)){
			ceConsumer.assign(Collections.singletonList(new TopicPartition(topic, 0)));

			mockConsumer.seek(new TopicPartition(topic, 0), 0);
			mockConsumer.addRecord(
				new ConsumerRecord<String, byte[]>(topic, 0, 0L, 0,
						TimestampType.CREATE_TIME, 0L, 0, 0, "0xk", payload, kafkaHeaders)
			);

			ConsumerRecords<String, CloudEvent<AttributesImpl, Much>> records =
					ceConsumer.poll(TIMEOUT);

			ConsumerRecord<String, CloudEvent<AttributesImpl, Much>> record =
					records.iterator().next();

			// assert
			CloudEvent<AttributesImpl, Much> actual = record.value();
			assertEquals("0x44", actual.getAttributes().getId());
			assertEquals("0.2", actual.getAttributes().getSpecVersion());
			assertEquals(URI.create("/source"), actual.getAttributes().getSource());
			assertEquals("type", actual.getAttributes().getType());
			assertTrue(actual.getAttributes().getContenttype().isPresent());
			assertEquals("application/json", actual.getAttributes().getContenttype().get());
			assertFalse(actual.getData().isPresent());
		}
	}

	@Test
	public void should_tracing_extension_ok() throws Exception {
		// setup
		RecordHeader id = new RecordHeader(HEADER_PREFIX
				+ "id", "0x44".getBytes());
		RecordHeader specversion = new RecordHeader(HEADER_PREFIX
				+ "specversion", "0.2".getBytes());
		RecordHeader source = new RecordHeader(HEADER_PREFIX
				+ "source", "/source".getBytes());
		RecordHeader type = new RecordHeader(HEADER_PREFIX
				+ "type", "type".getBytes());

		RecordHeader contenttype = new RecordHeader(HEADER_PREFIX
				+ "contenttype", "application/json".getBytes());

		RecordHeader traceparent =
				new RecordHeader("traceparent", "0".getBytes());
		RecordHeader tracestate =
				new RecordHeader("tracestate", "congo=4".getBytes());

		RecordHeaders kafkaHeaders = new RecordHeaders(
				new RecordHeader[]{id, specversion, source, type, contenttype, traceparent, tracestate});

		byte[] payload = null;

		final String topic = "binary.c";

		MockConsumer<String, byte[]> mockConsumer =
				new MockConsumer<String, byte[]>(OffsetResetStrategy.EARLIEST);

		// act
		try(CloudEventsKafkaConsumer<String, AttributesImpl, Much> ceConsumer =
				new CloudEventsKafkaConsumer<>(binary(Much.class), mockConsumer)){
			ceConsumer.assign(Collections.singletonList(new TopicPartition(topic, 0)));

			mockConsumer.seek(new TopicPartition(topic, 0), 0);
			mockConsumer.addRecord(
				new ConsumerRecord<String, byte[]>(topic, 0, 0L, 0,
						TimestampType.CREATE_TIME, 0L, 0, 0, "0xk", payload, kafkaHeaders)
			);

			ConsumerRecords<String, CloudEvent<AttributesImpl, Much>> records =
					ceConsumer.poll(TIMEOUT);

			ConsumerRecord<String, CloudEvent<AttributesImpl, Much>> record =
					records.iterator().next();

			// assert
			CloudEvent<AttributesImpl, Much> actual = record.value();
			assertEquals("0x44", actual.getAttributes().getId());
			assertEquals("0.2", actual.getAttributes().getSpecVersion());
			assertEquals(URI.create("/source"), actual.getAttributes().getSource());
			assertEquals("type", actual.getAttributes().getType());
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
}
