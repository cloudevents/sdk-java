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
package io.cloudevents.kafka;

import static java.util.Optional.ofNullable;
import static org.apache.kafka.clients.producer.ProducerConfig.VALUE_SERIALIZER_CLASS_CONFIG;

import java.util.AbstractMap.SimpleEntry;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.Properties;
import java.util.Set;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;
import java.util.function.Supplier;
import java.util.stream.Collectors;

import org.apache.kafka.clients.consumer.OffsetAndMetadata;
import org.apache.kafka.clients.producer.Callback;
import org.apache.kafka.clients.producer.KafkaProducer;
import org.apache.kafka.clients.producer.Producer;
import org.apache.kafka.clients.producer.ProducerRecord;
import org.apache.kafka.clients.producer.RecordMetadata;
import org.apache.kafka.common.Metric;
import org.apache.kafka.common.MetricName;
import org.apache.kafka.common.PartitionInfo;
import org.apache.kafka.common.TopicPartition;
import org.apache.kafka.common.errors.ProducerFencedException;
import org.apache.kafka.common.header.Header;
import org.apache.kafka.common.header.internals.RecordHeader;
import org.apache.kafka.common.serialization.ByteArraySerializer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import io.cloudevents.Attributes;
import io.cloudevents.CloudEvent;
import io.cloudevents.format.Wire;
import io.cloudevents.format.builder.EventStep;

/**
 * 
 * @author fabiojose
 *
 * @param <K> The key type
 * @param <A> The attributes type
 * @param <T> The CloudEvent 'data' type
 */
public class CloudEventsKafkaProducer<K, A extends Attributes, T> implements 
	Producer<K, CloudEvent<A, T>> {
	
	private static final Logger log = 
			LoggerFactory.getLogger(CloudEventsKafkaProducer.class);

	private final Producer<K, byte[]> producer;
	private final EventStep<A, T, byte[], byte[]> builder;
	
	/**
	 * Instantiate a producer to emit {@link CloudEvent} instances in Kafka
	 * 
	 * @param configuration To build the {@link KafkaProducer} for delegation
	 * @param builder The builder to build the kafka records value
	 */
	public CloudEventsKafkaProducer(Properties configuration,
			EventStep<A, T, byte[], byte[]> builder) {
		Objects.requireNonNull(configuration);
		Objects.requireNonNull(builder);
		
		ofNullable(configuration.get(VALUE_SERIALIZER_CLASS_CONFIG))
			.map(config -> config.toString())
			.filter(config -> 
				!config.contains(ByteArraySerializer.class.getName()))
			.ifPresent(wrong -> {
				log.warn("Fixing the wrong deserializer {}", wrong);
				configuration.put(VALUE_SERIALIZER_CLASS_CONFIG,
						ByteArraySerializer.class);
			});
		
		this.builder = builder;
		this.producer = new KafkaProducer<>(configuration);
	}
	
	/**
	 * Instantiate a producer to emit {@link CloudEvent} instances in Kafka, but using
	 * your own configured producer.
	 * 
	 * @param builder The builder to build the kafka records value
	 * @param producer Your configured producer
	 */
	public CloudEventsKafkaProducer(EventStep<A, T, byte[], byte[]> builder,
			Producer<K, byte[]> producer) {
		Objects.requireNonNull(builder);
		Objects.requireNonNull(producer);
		
		this.builder = builder;
		this.producer = producer;
	}
	
	private Wire<byte[], String, byte[]> marshal(Supplier<CloudEvent<A, T>> event) {
		
		return 
			Optional.ofNullable(builder)
				.map(step -> step.withEvent(event))
				.map(marshaller -> marshaller.marshal())
				.get();
		
	}
	
	/**
	 * Casts the Object value of header into byte[]. This is
	 * guaranteed by the HeaderMapper implementation
	 * 
	 * @param headers
	 * @return
	 */
	private Set<Header> marshal(Map<String, byte[]> headers) {

		return 
		  headers.entrySet()
			.stream()
			.map(header -> 
				new SimpleEntry<>(header.getKey(), header.getValue()))
			.map(header -> new RecordHeader(header.getKey(), header.getValue()))
			.collect(Collectors.toSet());
		
	}
	
	private ProducerRecord<K, byte[]> marshal(ProducerRecord<K, CloudEvent<A, T>>
			event) {
		Wire<byte[], String, byte[]> wire = marshal(() -> event.value());
		Set<Header> headers = marshal(wire.getHeaders());
		
		byte[] payload = wire
				.getPayload()
				.orElse(null); 
		
		ProducerRecord<K, byte[]> record = 
			new ProducerRecord<K, byte[]>(
				event.topic(),
				event.partition(),
				event.timestamp(),
				event.key(),
				payload,
				headers);
			
		return record;
	}
	
	@Override
	public Future<RecordMetadata> send(ProducerRecord<K, CloudEvent<A, T>>
			event) {
		
		return producer.send(marshal(event));
		
	}

	@Override
	public Future<RecordMetadata> send(ProducerRecord<K, CloudEvent<A, T>> 
			event, Callback callback) {
		
		return producer.send(marshal(event), callback);
		
	}
	
	@Override
	public void abortTransaction() throws ProducerFencedException {
		producer.abortTransaction();		
	}

	@Override
	public void beginTransaction() throws ProducerFencedException {
		producer.beginTransaction();		
	}

	@Override
	public void close() {
		producer.close();
	}

	@Override
	public void close(long arg0, TimeUnit arg1) {
		producer.close(arg0, arg1);		
	}

	@Override
	public void commitTransaction() throws ProducerFencedException {
		producer.commitTransaction();
	}

	@Override
	public void flush() {
		producer.flush();
	}

	@Override
	public void initTransactions() {
		producer.initTransactions();		
	}

	@Override
	public Map<MetricName, ? extends Metric> metrics() {
		return producer.metrics();
	}

	@Override
	public List<PartitionInfo> partitionsFor(String arg0) {
		return producer.partitionsFor(arg0);
	}

	@Override
	public void sendOffsetsToTransaction(Map<TopicPartition, OffsetAndMetadata> arg0, String arg1)
			throws ProducerFencedException {
		producer.sendOffsetsToTransaction(arg0, arg1);		
	}

}
