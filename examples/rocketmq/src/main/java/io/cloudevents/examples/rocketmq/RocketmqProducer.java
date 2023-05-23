package io.cloudevents.examples.rocketmq;

import io.cloudevents.CloudEvent;
import io.cloudevents.core.v1.CloudEventBuilder;
import io.cloudevents.rocketmq.RocketMqMessageFactory;
import io.cloudevents.types.Time;
import java.io.IOException;
import java.net.URI;
import java.nio.charset.StandardCharsets;
import java.util.HashMap;
import java.util.Map;
import org.apache.rocketmq.client.apis.ClientConfiguration;
import org.apache.rocketmq.client.apis.ClientException;
import org.apache.rocketmq.client.apis.ClientServiceProvider;
import org.apache.rocketmq.client.apis.message.Message;
import org.apache.rocketmq.client.apis.producer.Producer;
import org.apache.rocketmq.client.apis.producer.SendReceipt;
import org.apache.rocketmq.shaded.com.google.gson.Gson;

public class RocketmqProducer {
    private RocketmqProducer() {
    }

    public static void main(String[] args) throws ClientException, IOException {
        if (args.length < 2) {
            System.out.println("Usage: rocketmq_producer <endpoints> <topic>");
            return;
        }
        final ClientServiceProvider provider = ClientServiceProvider.loadService();
        String endpoints = args[0];
        ClientConfiguration clientConfiguration = ClientConfiguration.newBuilder()
            .setEndpoints(endpoints)
            .build();
        String topic = args[1];

        // Create the RocketMQ Producer.
        final Producer producer = provider.newProducerBuilder()
            .setClientConfiguration(clientConfiguration)
            .setTopics(topic)
            .build();
        final Gson gson = new Gson();
        Map<String, String> payload = new HashMap<>();
        payload.put("foo", "bar");
        final CloudEvent event = new CloudEventBuilder()
            .withId("client-id")
            .withSource(URI.create("http://127.0.0.1/rocketmq-client"))
            .withType("com.foobar")
            .withTime(Time.parseTime("2022-11-09T21:47:12.032198+00:00"))
            .withData(gson.toJson(payload).getBytes(StandardCharsets.UTF_8))
            .build();
        // Transform event into message.
        final Message message = RocketMqMessageFactory.createWriter(topic).writeBinary(event);
        try {
            // Send the message.
            final SendReceipt sendReceipt = producer.send(message);
            System.out.println("Send message successfully, messageId=" + sendReceipt.getMessageId());
        } catch (Exception e) {
            System.out.println("Failed to send message");
            e.printStackTrace();
        }
        // Close the producer when you don't need it anymore.
        producer.close();
    }
}
