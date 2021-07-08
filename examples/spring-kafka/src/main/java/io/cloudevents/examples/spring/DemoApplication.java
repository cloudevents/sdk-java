package io.cloudevents.examples.spring;

import java.net.URI;
import java.util.UUID;

import org.apache.kafka.clients.admin.NewTopic;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.kafka.config.TopicBuilder;
import org.springframework.messaging.handler.annotation.SendTo;

import io.cloudevents.CloudEvent;
import io.cloudevents.core.builder.CloudEventBuilder;
import io.cloudevents.spring.kafka.CloudEventRecordMessageConverter;

@SpringBootApplication
public class DemoApplication {

    public static void main(String[] args) throws Exception {
        SpringApplication.run(DemoApplication.class, args);
    }

    @KafkaListener(id = "listener", topics = "in", clientIdPrefix = "demo")
    @SendTo("out")
    public CloudEvent listen(CloudEvent event) {
        System.err.println("Echo: " + event);
        return CloudEventBuilder.from(event).withId(UUID.randomUUID().toString())
                .withSource(URI.create("https://spring.io/foos")).withType("io.spring.event.Foo")
                .withData(event.getData().toBytes()).build();
    }

    @Bean
    public NewTopic topicOut() {
        return TopicBuilder.name("out").build();
    }

    @Bean
    public NewTopic topicIn() {
        return TopicBuilder.name("in").build();
    }

    @Configuration
    public static class CloudEventMessageConverterConfiguration {
        /**
         * Configure a RecordMessageConverter for Spring Kafka to pick up and use to
         * convert to and from CloudEvent and Message.
         */
        @Bean
        public CloudEventRecordMessageConverter recordMessageConverter() {
            return new CloudEventRecordMessageConverter();
        }

    }

}
