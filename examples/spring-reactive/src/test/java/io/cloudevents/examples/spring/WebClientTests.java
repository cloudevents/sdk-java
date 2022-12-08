package io.cloudevents.examples.spring;

import static org.assertj.core.api.Assertions.assertThat;

import java.net.URI;
import java.nio.charset.StandardCharsets;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.context.SpringBootTest.WebEnvironment;
import org.springframework.boot.web.server.LocalServerPort;
import org.springframework.web.reactive.function.client.WebClient;

import io.cloudevents.CloudEvent;
import io.cloudevents.core.builder.CloudEventBuilder;
import reactor.core.publisher.Mono;

/**
 * Test case to show example usage of WebClient and CloudEvent. The actual
 * content of the request and response are asserted separately in
 * {@link DemoApplicationTests}.
 */
@SpringBootTest(webEnvironment = WebEnvironment.RANDOM_PORT)
public class WebClientTests {

    @Autowired
    private WebClient.Builder rest;

    @LocalServerPort
    private int port;

    private CloudEvent event;

    @BeforeEach
    void setUp() {
        event = CloudEventBuilder.v1() //
                .withId("12345") //
                .withSource(URI.create("https://spring.io/events")) //
                .withType("io.spring.event") //
                .withData("{\"value\":\"Dave\"}".getBytes(StandardCharsets.UTF_8)) //
                .build();
    }

    @Test
    void echoWithCorrectHeaders() {

        Mono<CloudEvent> result = rest.build() //
                .post() //
                .uri("http://localhost:" + port + "/event") //
                .bodyValue(event) //
                .exchangeToMono(response -> response.bodyToMono(CloudEvent.class));

        assertThat(result.block().getData()).isEqualTo(event.getData());

    }

}
