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

import static java.util.stream.Collectors.groupingBy;
import static org.apache.kafka.clients.consumer.ConsumerConfig.VALUE_DESERIALIZER_CLASS_CONFIG;
import static java.util.Optional.ofNullable;

import java.time.Duration;
import java.util.AbstractMap.SimpleEntry;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Objects;
import java.util.Properties;
import java.util.Set;
import java.util.concurrent.TimeUnit;
import java.util.regex.Pattern;
import java.util.stream.Collectors;
import java.util.stream.StreamSupport;

import org.apache.kafka.clients.consumer.Consumer;
import org.apache.kafka.clients.consumer.ConsumerRebalanceListener;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.apache.kafka.clients.consumer.ConsumerRecords;
import org.apache.kafka.clients.consumer.KafkaConsumer;
import org.apache.kafka.clients.consumer.OffsetAndMetadata;
import org.apache.kafka.clients.consumer.OffsetAndTimestamp;
import org.apache.kafka.clients.consumer.OffsetCommitCallback;
import org.apache.kafka.common.Metric;
import org.apache.kafka.common.MetricName;
import org.apache.kafka.common.PartitionInfo;
import org.apache.kafka.common.TopicPartition;
import org.apache.kafka.common.header.Header;
import org.apache.kafka.common.header.Headers;
import org.apache.kafka.common.serialization.ByteArrayDeserializer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import io.cloudevents.Attributes;
import io.cloudevents.CloudEvent;
import io.cloudevents.format.builder.HeadersStep;

/**
 * 
 * @author fabiojose
 * 
 * @param <K> The key type
 * @param <A> The attributes type
 * @param <T> The CloudEvent 'data' type
 */
public class CloudEventsKafkaConsumer<K, A extends Attributes, T> 
	implements Consumer<K, CloudEvent<A, T>>{
	
	private static final Logger log = 
		LoggerFactory.getLogger(CloudEventsKafkaConsumer.class);
	
	private static final String CE_CONTENT_TYPE = "application/cloudevents+";
	private static final String CONTENT_TYPE_HEADER = "content-type";
	
	private final Consumer<K, byte[]> consumer;

	private HeadersStep<A, T, byte[]> builder;
	
	/**
	 * Instantiate a consumer prepared to unmarshal the events from Kafka
	 * 
	 * @param configuration To build the {@link KafkaConsumer} for delegation
	 * @param builder The builder to build the CloudEvent
	 */
	public CloudEventsKafkaConsumer(Properties configuration, 
			HeadersStep<A, T, byte[]> builder) {
		Objects.requireNonNull(configuration);
		Objects.requireNonNull(builder);
		
		ofNullable(configuration.get(VALUE_DESERIALIZER_CLASS_CONFIG))
			.map(config -> config.toString())
			.filter(config -> 
				!config.contains(ByteArrayDeserializer.class.getName()))
			.ifPresent(wrong -> {
				log.warn("Fixing the wrong deserializer {}", wrong);
				configuration.put(VALUE_DESERIALIZER_CLASS_CONFIG,
						ByteArrayDeserializer.class);
			});
		
		this.builder = builder;
		this.consumer = new KafkaConsumer<>(configuration);
	}
	
	/**
	 * Turns Kafka headers into {@code Map<String, Object>}
	 * @param kafkaHeaders
	 * @return A {@link Map} with {@link Header} mapped as Object 
	 */
	private static Map<String, Object> asMap(Headers kafkaHeaders) {
		return 
		StreamSupport.stream(kafkaHeaders.spliterator(), Boolean.FALSE)
			.map(header -> 
				new SimpleEntry<String, Object>(header.key(), header.value()))
			.collect(Collectors.toMap(Entry::getKey, Entry::getValue));
	}
	
	private ConsumerRecords<K, CloudEvent<A, T>> 
		build(ConsumerRecords<K, byte[]> records) {
		
		List<ConsumerRecord<K, CloudEvent<A, T>>> newRecords = 
			new ArrayList<>();
		
		records.forEach(record -> {
			// TODO check header to see: Binary or Structured
			
			CloudEvent<A, T> value = 
				builder
					.withHeaders(() -> asMap(record.headers()))
					.withPayload(() -> record.value())
					.unmarshal();
			
			newRecords.add(
				new ConsumerRecord<K, CloudEvent<A, T>>(
					record.topic(),
					record.partition(),
					record.offset(), 
					record.timestamp(), 
					record.timestampType(), 
					record.checksum(), 
					record.serializedKeySize(), 
					record.serializedValueSize(), 
					record.key(), 
					value, 
					record.headers())
			);
		});
		
		final Map<TopicPartition, List<ConsumerRecord<K, CloudEvent<A, T>>>>
		result = 		
			newRecords.stream()
				.collect(groupingBy(record -> 
					new TopicPartition(record.topic(), record.partition())));
		
		return new ConsumerRecords<K, CloudEvent<A, T>>(result);
	}
	
	@Override
	@Deprecated
	public ConsumerRecords<K, CloudEvent<A, T>> poll(long timeout) {
		return build(consumer.poll(timeout));
	}

	@Override
	public ConsumerRecords<K, CloudEvent<A, T>> poll(Duration duration) {
		return build(consumer.poll(duration));
	}

	@Override
	public void assign(Collection<TopicPartition> arg0) {
		consumer.assign(arg0);		
	}

	@Override
	public Set<TopicPartition> assignment() {
		return consumer.assignment();
	}

	@Override
	public Map<TopicPartition, Long> beginningOffsets(Collection<TopicPartition> arg0) {
		return consumer.beginningOffsets(arg0);
	}

	@Override
	public Map<TopicPartition, Long> beginningOffsets(Collection<TopicPartition> arg0, Duration arg1) {
		return consumer.beginningOffsets(arg0, arg1);
	}
	
	@Override
	public void close() {
		consumer.close();		
	}

	@Override
	public void close(Duration arg0) {
		consumer.close(arg0);
	}

	@Override
	@Deprecated
	public void close(long arg0, TimeUnit arg1) {
		consumer.close(arg0, arg1);		
	}

	@Override
	public void commitAsync() {
		consumer.commitAsync();
	}

	@Override
	public void commitAsync(OffsetCommitCallback arg0) {
		consumer.commitAsync(arg0);		
	}

	@Override
	public void commitAsync(Map<TopicPartition, OffsetAndMetadata> arg0, OffsetCommitCallback arg1) {
		consumer.commitAsync(arg0, arg1);
	}

	@Override
	public void commitSync() {
		consumer.commitSync();		
	}

	@Override
	public void commitSync(Duration arg0) {
		consumer.commitSync();		
	}

	@Override
	public void commitSync(Map<TopicPartition, OffsetAndMetadata> arg0) {
		consumer.commitSync(arg0);		
	}

	@Override
	public void commitSync(Map<TopicPartition, OffsetAndMetadata> arg0, Duration arg1) {
		consumer.commitSync(arg0, arg1);		
	}

	@Override
	public OffsetAndMetadata committed(TopicPartition arg0) {
		return consumer.committed(arg0);
	}

	@Override
	public OffsetAndMetadata committed(TopicPartition arg0, Duration arg1) {
		return consumer.committed(arg0, arg1);
	}

	@Override
	public Map<TopicPartition, Long> endOffsets(Collection<TopicPartition> arg0) {
		return consumer.endOffsets(arg0);
	}

	@Override
	public Map<TopicPartition, Long> endOffsets(Collection<TopicPartition> arg0, Duration arg1) {
		return consumer.endOffsets(arg0, arg1);
	}

	@Override
	public Map<String, List<PartitionInfo>> listTopics() {
		return consumer.listTopics();
	}

	@Override
	public Map<String, List<PartitionInfo>> listTopics(Duration arg0) {
		return consumer.listTopics(arg0);
	}

	@Override
	public Map<MetricName, ? extends Metric> metrics() {
		return consumer.metrics();
	}

	@Override
	public Map<TopicPartition, OffsetAndTimestamp> offsetsForTimes(Map<TopicPartition, Long> arg0) {
		return consumer.offsetsForTimes(arg0);
	}

	@Override
	public Map<TopicPartition, OffsetAndTimestamp> offsetsForTimes(Map<TopicPartition, Long> arg0, Duration arg1) {
		return consumer.offsetsForTimes(arg0, arg1);
	}

	@Override
	public List<PartitionInfo> partitionsFor(String arg0) {
		return consumer.partitionsFor(arg0);
	}

	@Override
	public List<PartitionInfo> partitionsFor(String arg0, Duration arg1) {
		return consumer.partitionsFor(arg0, arg1);
	}

	@Override
	public void pause(Collection<TopicPartition> arg0) {
		consumer.pause(arg0);
	}

	@Override
	public Set<TopicPartition> paused() {
		return consumer.paused();
	}

	@Override
	public long position(TopicPartition arg0) {
		return consumer.position(arg0);
	}

	@Override
	public long position(TopicPartition arg0, Duration arg1) {
		return consumer.position(arg0, arg1);
	}

	@Override
	public void resume(Collection<TopicPartition> arg0) {
		consumer.resume(arg0);
	}

	@Override
	public void seek(TopicPartition arg0, long arg1) {
		consumer.seek(arg0, arg1);
	}

	@Override
	public void seekToBeginning(Collection<TopicPartition> arg0) {
		consumer.seekToBeginning(arg0);		
	}

	@Override
	public void seekToEnd(Collection<TopicPartition> arg0) {
		consumer.seekToEnd(arg0);
	}

	@Override
	public void subscribe(Collection<String> arg0) {
		consumer.subscribe(arg0);		
	}

	@Override
	public void subscribe(Pattern arg0) {
		consumer.subscribe(arg0);
	}

	@Override
	public void subscribe(Collection<String> arg0, ConsumerRebalanceListener arg1) {
		consumer.subscribe(arg0, arg1);
	}

	@Override
	public void subscribe(Pattern arg0, ConsumerRebalanceListener arg1) {
		consumer.subscribe(arg0, arg1);	
	}

	@Override
	public Set<String> subscription() {
		return consumer.subscription();
	}

	@Override
	public void unsubscribe() {
		consumer.unsubscribe();
	}

	@Override
	public void wakeup() {
		consumer.wakeup();		
	}

}
