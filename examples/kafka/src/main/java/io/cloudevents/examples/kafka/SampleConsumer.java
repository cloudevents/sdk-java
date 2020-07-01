package io.cloudevents.examples.kafka;

import io.cloudevents.CloudEvent;
import io.cloudevents.kafka.CloudEventDeserializer;
import org.apache.kafka.clients.consumer.ConsumerConfig;
import org.apache.kafka.clients.consumer.ConsumerRecords;
import org.apache.kafka.clients.consumer.KafkaConsumer;
import org.apache.kafka.common.serialization.StringDeserializer;

import java.time.Duration;
import java.util.Collections;
import java.util.Properties;

public class SampleConsumer {

    public static void main(String[] args) {
        if (args.length < 2) {
            System.out.println("Usage: sample_consumer <bootstrap_server> <topic>");
            return;
        }

        // Basic consumer configuration
        Properties props = new Properties();
        props.put(ConsumerConfig.BOOTSTRAP_SERVERS_CONFIG, args[0]);
        props.put(ConsumerConfig.GROUP_ID_CONFIG, "sample-cloudevents-consumer");
        props.put(ConsumerConfig.KEY_DESERIALIZER_CLASS_CONFIG, StringDeserializer.class.getName());
        props.put(ConsumerConfig.VALUE_DESERIALIZER_CLASS_CONFIG, CloudEventDeserializer.class.getName());
        props.put(ConsumerConfig.AUTO_OFFSET_RESET_CONFIG, "latest");
        props.put(ConsumerConfig.ENABLE_AUTO_COMMIT_CONFIG, "true");

        // Create the consumer and subscribe to the topic
        KafkaConsumer<String, CloudEvent> consumer = new KafkaConsumer<>(props);
        consumer.subscribe(Collections.singletonList(args[1]));

        System.out.println("Consumer started");

        while (true) {
            ConsumerRecords<String, CloudEvent> consumerRecords = consumer.poll(Duration.ofMillis(100));
            consumerRecords.forEach(record -> {
                System.out.println(
                    "New record:\n" +
                        "  Record Key " + record.key() + "\n" +
                        "  Record value " + record.value() + "\n" +
                        "  Record partition " + record.partition() + "\n" +
                        "  Record offset " + record.offset() + "\n"
                );
            });
        }

    }
}
