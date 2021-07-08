package io.cloudevents.examples.spring;

import static org.assertj.core.api.Assertions.assertThat;

import java.net.URI;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.TimeUnit;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.boot.test.util.TestPropertyValues;
import org.springframework.context.ApplicationContextInitializer;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.kafka.support.KafkaHeaders;
import org.springframework.messaging.Message;
import org.springframework.messaging.MessageHeaders;
import org.springframework.messaging.support.MessageBuilder;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.util.MimeType;
import org.testcontainers.containers.KafkaContainer;
import org.testcontainers.utility.DockerImageName;

import io.cloudevents.CloudEvent;
import io.cloudevents.core.builder.CloudEventBuilder;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.NONE, properties = "spring.kafka.consumer.auto-offset-reset=earliest")
@ContextConfiguration(initializers = DemoApplicationTests.Initializer.class)
public class DemoApplicationTests {

    @Autowired
    private KafkaTemplate<byte[], byte[]> kafka;

    @Autowired
    private KafkaListenerConfiguration listener;

    @BeforeEach
    public void clear() {
        listener.queue.clear();
    }

    @Test
    void echoWithKafkaStyleHeaders() throws Exception {

        kafka.send(MessageBuilder.withPayload("{\"value\":\"Dave\"}".getBytes()) //
                        .setHeader(KafkaHeaders.TOPIC, "in") //
                        .setHeader("ce_id", "12345") //
                        .setHeader("ce_specversion", "1.0") //
                        .setHeader("ce_type", "io.spring.event") //
                        .setHeader("ce_source", "https://spring.io/events") //
                        .setHeader("ce_datacontenttype", MimeType.valueOf("application/json")) //
                        .build());

        Message<byte[]> response = listener.queue.poll(2000, TimeUnit.MILLISECONDS);

        assertThat(response).isNotNull();
        assertThat(response.getPayload()).isEqualTo("{\"value\":\"Dave\"}".getBytes());

        MessageHeaders headers = response.getHeaders();

        assertThat(headers.get("ce-id")).isNotNull();
        assertThat(headers.get("ce-source")).isNotNull();
        assertThat(headers.get("ce-type")).isNotNull();

        assertThat(headers.get("ce-id")).isNotEqualTo("12345");
        assertThat(headers.get("ce-type")).isEqualTo("io.spring.event.Foo");
        assertThat(headers.get("ce-source")).isEqualTo("https://spring.io/foos");

    }

    @Test
    void echoWithCanonicalHeaders() throws Exception {

        kafka.send(MessageBuilder.withPayload("{\"value\":\"Dave\"}".getBytes()) //
                        .setHeader(KafkaHeaders.TOPIC, "in") //
                        .setHeader("ce-id", "12345") //
                        .setHeader("ce-specversion", "1.0") //
                        .setHeader("ce-type", "io.spring.event") //
                        .setHeader("ce-source", "https://spring.io/events") //
                        .setHeader("ce-datacontenttype", MimeType.valueOf("application/json")) //
                        .build());

        Message<byte[]> response = listener.queue.poll(2000, TimeUnit.MILLISECONDS);

        assertThat(response).isNotNull();
        assertThat(response.getPayload()).isEqualTo("{\"value\":\"Dave\"}".getBytes());

        MessageHeaders headers = response.getHeaders();

        assertThat(headers.get("ce-id")).isNotNull();
        assertThat(headers.get("ce-source")).isNotNull();
        assertThat(headers.get("ce-type")).isNotNull();

        assertThat(headers.get("ce-id")).isNotEqualTo("12345");
        assertThat(headers.get("ce-type")).isEqualTo("io.spring.event.Foo");
        assertThat(headers.get("ce-source")).isEqualTo("https://spring.io/foos");

    }

    @Test
    void echoWithStructured() throws Exception {

        CloudEvent event = CloudEventBuilder.v1() //
        .withId("12345") //
        .withSource(URI.create("https://spring.io/events")) //
        .withType("io.spring.event") //
        .withDataContentType("application/json") //
        .withData("{\"value\":\"Dave\"}".getBytes()).build();

        kafka.send(MessageBuilder.withPayload(event) //
                        .setHeader(KafkaHeaders.TOPIC, "in") //
                        .setHeader("contentType", MimeType.valueOf("application/cloudevents+json")) //
                        .build());

        Message<byte[]> response = listener.queue.poll(2000, TimeUnit.MILLISECONDS);

        assertThat(response).isNotNull();
        assertThat(response.getPayload()).isEqualTo("{\"value\":\"Dave\"}".getBytes());

        MessageHeaders headers = response.getHeaders();

        assertThat(headers.get("ce-id")).isNotNull();
        assertThat(headers.get("ce-source")).isNotNull();
        assertThat(headers.get("ce-type")).isNotNull();

        assertThat(headers.get("ce-id")).isNotEqualTo("12345");
        assertThat(headers.get("ce-type")).isEqualTo("io.spring.event.Foo");
        assertThat(headers.get("ce-source")).isEqualTo("https://spring.io/foos");

    }

    @TestConfiguration
    static class KafkaListenerConfiguration {

        private ArrayBlockingQueue<Message<byte[]>> queue = new ArrayBlockingQueue<>(1);

        @KafkaListener(id = "test", topics = "out", clientIdPrefix = "test")
        public void listen(Message<byte[]> message) {
            System.err.println(message);
            queue.add(message);
        }

    }

    public static class Initializer implements ApplicationContextInitializer<ConfigurableApplicationContext> {

        private static KafkaContainer kafka;

        static {
            kafka = new KafkaContainer(DockerImageName.parse("confluentinc/cp-kafka").withTag("5.4.3")) //
                    .withNetwork(null); // .withReuse(true);
            kafka.start();
        }

        @Override
        public void initialize(ConfigurableApplicationContext context) {
            TestPropertyValues.of("spring.kafka.bootstrap-servers=" + kafka.getBootstrapServers()).applyTo(context);
        }

    }
}
