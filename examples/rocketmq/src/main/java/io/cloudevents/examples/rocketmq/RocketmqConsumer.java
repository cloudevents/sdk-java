package io.cloudevents.examples.rocketmq;

import io.cloudevents.CloudEvent;
import io.cloudevents.core.message.MessageReader;
import io.cloudevents.rocketmq.RocketMqMessageFactory;
import java.io.IOException;
import java.util.Collections;
import org.apache.rocketmq.client.apis.ClientConfiguration;
import org.apache.rocketmq.client.apis.ClientException;
import org.apache.rocketmq.client.apis.ClientServiceProvider;
import org.apache.rocketmq.client.apis.consumer.ConsumeResult;
import org.apache.rocketmq.client.apis.consumer.FilterExpression;
import org.apache.rocketmq.client.apis.consumer.PushConsumer;

public class RocketmqConsumer {
    private RocketmqConsumer() {
    }

    public static void main(String[] args) throws InterruptedException, ClientException, IOException {
        if (args.length < 3) {
            System.out.println("Usage: rocketmq_consumer <endpoints> <topic> <consumer_group>");
            return;
        }
        final ClientServiceProvider provider = ClientServiceProvider.loadService();
        String endpoints = args[0];
        ClientConfiguration clientConfiguration = ClientConfiguration.newBuilder()
            .setEndpoints(endpoints)
            .build();
        FilterExpression filterExpression = new FilterExpression();
        String topic = args[1];
        String consumerGroup = args[2];

        // Create the RocketMQ Consumer.
        PushConsumer pushConsumer = provider.newPushConsumerBuilder()
            .setClientConfiguration(clientConfiguration)
            .setConsumerGroup(consumerGroup)
            .setSubscriptionExpressions(Collections.singletonMap(topic, filterExpression))
            .setMessageListener(messageView -> {
                final MessageReader reader = RocketMqMessageFactory.createReader(messageView);
                final CloudEvent event = reader.toEvent();
                System.out.println("Received event=" + event + ", messageId=" + messageView.getMessageId());
                return ConsumeResult.SUCCESS;
            })
            .build();
        // Block the main thread, no need for production environment.
        Thread.sleep(Long.MAX_VALUE);
        // Close the push consumer when you don't need it anymore.
        pushConsumer.close();
    }
}
