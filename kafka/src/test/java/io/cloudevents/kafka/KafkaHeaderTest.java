/**
 * Copyright 2018 The CloudEvents Authors
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

import io.cloudevents.CloudEvent;
import io.cloudevents.CloudEventBuilder;
import io.cloudevents.SpecVersion;
import io.debezium.kafka.KafkaCluster;
import io.debezium.util.Testing;
import org.apache.kafka.clients.consumer.ConsumerConfig;
import org.apache.kafka.clients.consumer.ConsumerRecords;
import org.apache.kafka.clients.consumer.KafkaConsumer;
import org.apache.kafka.clients.consumer.OffsetResetStrategy;
import org.apache.kafka.clients.producer.KafkaProducer;
import org.apache.kafka.clients.producer.ProducerConfig;
import org.apache.kafka.clients.producer.ProducerRecord;
import org.apache.kafka.common.header.Headers;
import org.apache.kafka.common.serialization.StringDeserializer;
import org.apache.kafka.common.serialization.StringSerializer;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.io.File;
import java.net.URI;
import java.nio.charset.StandardCharsets;
import java.time.Duration;
import java.util.Arrays;
import java.util.Map;
import java.util.Properties;
import java.util.UUID;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;

import static org.assertj.core.api.Assertions.assertThat;

public class KafkaHeaderTest {

    private static final KafkaTransportHeaders transportHeaders = new KafkaTransportHeaders();
    private KafkaCluster kafkaCluster;
    private File dataDir;

    @BeforeEach
    public void beforeEach() {
        dataDir = Testing.Files.createTestingDirectory("cluster");
        kafkaCluster = new KafkaCluster().usingDirectory(dataDir)
                .deleteDataPriorToStartup(true)
                .deleteDataUponShutdown(true)
                .withPorts(2181, 9092);
    }

    @AfterEach
    public void afterEach() {
        kafkaCluster.shutdown();
        Testing.Files.delete(dataDir);
    }

    @Test
    public void testSendingCloudEvent() throws Exception {

        final String topicName = "topicA";
        KafkaConsumer consumer;
        KafkaProducer producer;

        // Start a cluster and create a topic ...
        kafkaCluster.addBrokers(1).startup();
        kafkaCluster.createTopics(topicName);

        final Properties consumerProperties = kafkaCluster.useTo().getConsumerProperties(topicName, topicName, OffsetResetStrategy.EARLIEST);
        consumerProperties.put(ConsumerConfig.KEY_DESERIALIZER_CLASS_CONFIG, StringDeserializer.class);
        consumerProperties.put(ConsumerConfig.VALUE_DESERIALIZER_CLASS_CONFIG, StringDeserializer.class);
        consumer = new KafkaConsumer(consumerProperties);
        consumer.subscribe(Arrays.asList(topicName));

        final Properties producerProperties = kafkaCluster.useTo().getProducerProperties(topicName);
        producerProperties.put(ProducerConfig.KEY_SERIALIZER_CLASS_CONFIG, StringSerializer.class);
        producerProperties.put(ProducerConfig.VALUE_SERIALIZER_CLASS_CONFIG, StringSerializer.class);
        producer = new KafkaProducer<>(producerProperties);


        final CloudEvent<String> cloudEvent = new CloudEventBuilder<String>()
                .type("My.Type")
                .id(UUID.randomUUID().toString())
                .source(URI.create("/foo"))
                .data("Hello") // does need a SerDe ...
                .build();

        final Headers headers = KafkaTransportUtil.extractRecordHeaders(cloudEvent);

        final ProducerRecord record = new ProducerRecord(topicName, null, null, "my_key1", cloudEvent.getData().get(), headers);
        producer.send(record);

        final CountDownLatch latch = new CountDownLatch(1);
        final ConsumerRecords<String, String> records = consumer.poll(Duration.ofMillis(1_000));
        records.forEach(rec -> {

            // raw Kafka stuff
            Headers receivedHeaders = rec.headers();
            assertThat(receivedHeaders.lastHeader(transportHeaders.typeKey()).value()).isEqualTo("My.Type".getBytes(StandardCharsets.UTF_8));
            assertThat(rec.value()).isEqualTo("Hello");


            // converted:
            final CloudEvent<Map<String, String>> receivedCloudEvent = KafkaTransportUtil.createFromConsumerRecord(rec);
            assertThat(receivedCloudEvent.getType()).isEqualTo("My.Type");
            assertThat(receivedCloudEvent.getSpecVersion()).isEqualTo(SpecVersion.V_02.toString());
            assertThat(receivedCloudEvent.getContentType().get()).isEqualTo("application/kafka");

            assertThat(receivedCloudEvent.getData().isPresent()).isTrue();
            assertThat(receivedCloudEvent.getData().get().get("my_key1")).isEqualTo("Hello");

            latch.countDown();
        } );

        final boolean done = latch.await(2, TimeUnit.SECONDS);
        assertThat(done).isTrue();
    }
}
