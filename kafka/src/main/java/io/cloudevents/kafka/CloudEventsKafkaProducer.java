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

import static java.util.AbstractMap.SimpleEntry;

import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.Set;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;
import java.util.function.Supplier;
import java.util.stream.Collectors;

import org.apache.kafka.clients.consumer.OffsetAndMetadata;
import org.apache.kafka.clients.producer.Callback;
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

import io.cloudevents.Attributes;
import io.cloudevents.CloudEvent;
import io.cloudevents.format.BinaryMarshaller.EventStep;
import io.cloudevents.format.Wire;

/**
 * 
 * @author fabiojose
 *
 * @param <K> The key type
 * @param <A> The attributes type
 * @param <T> The 'data' type
 */
public class CloudEventsKafkaProducer<K, A extends Attributes, T> implements 
	Producer<K, CloudEvent<A, T>> {

	private final Producer<K, byte[]> producer;
	private EventStep<A, T, byte[]> builder;
	
	/**
	 * Instantiate a producer to emit structured events
	 * @param producer To delegate the actual producer methods call
	 */
	public CloudEventsKafkaProducer(Producer<K, byte[]> producer) {
		Objects.requireNonNull(producer);
		
		this.producer = producer;
	}
	
	/**
	 * Instantiate a producer to emit binary events
	 * @param producer To delegate the actual producer methods call
	 * @param builder The builder to build the kafka records value
	 */
	public CloudEventsKafkaProducer(Producer<K, byte[]> producer, 
			EventStep<A, T, byte[]> builder) {
		this(producer);
		Objects.requireNonNull(builder);
		
		this.builder = builder;
	}
	
	private Wire<byte[], String, Object> marshal(Supplier<CloudEvent<A, T>> event) {
		
		return 
			Optional.ofNullable(builder)
				.map(step -> step.withEvent(event))
				.map(marshaller -> marshaller.marshal())
				.get();
		
		//TODO treat the case of structured one
		
	}
	
	/**
	 * Casts the Object value of header into byte[]. This is
	 * guaranteed by the HeaderMapper implementation
	 * 
	 * @param headers
	 * @return
	 */
	private Set<Header> marshal(Map<String, Object> headers) {
		
		return 
		  headers.entrySet()
			.stream()
			.map(header -> 
				new SimpleEntry<>(header.getKey(), (byte[])header.getValue()))
			.map(header -> new RecordHeader(header.getKey(), header.getValue()))
			.collect(Collectors.toSet());
		
	}
	
	@Override
	public Future<RecordMetadata> send(ProducerRecord<K, CloudEvent<A, T>>
			event) {
		
		Wire<byte[], String, Object> wire = marshal(() -> event.value());
		Set<Header> headers = marshal(wire.getHeaders());
		
		ProducerRecord<K, byte[]> record =
			wire.getPayload()
				.map(payload -> 
				  new ProducerRecord<K, byte[]>(
					event.topic(),
					event.partition(),
					event.timestamp(),
					event.key(),
					payload,
					headers))
				.orElse(
				  new ProducerRecord<K, byte[]>(
					event.topic(),
					event.partition(),
					event.timestamp(),
					event.key(),
					null,
					headers));
		
		return producer.send(record);
	}

	@Override
	public Future<RecordMetadata> send(ProducerRecord<K, CloudEvent<A, T>> 
			event, Callback callback) {
		
		// TODO Auto-generated method stub
		return null;
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
