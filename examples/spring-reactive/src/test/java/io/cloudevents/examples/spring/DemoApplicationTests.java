package io.cloudevents.examples.spring;

import io.cloudevents.CloudEvent;
import io.cloudevents.core.builder.CloudEventBuilder;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.context.SpringBootTest.WebEnvironment;
import org.springframework.boot.webtestclient.autoconfigure.AutoConfigureWebTestClient;
import org.springframework.http.MediaType;
import org.springframework.test.web.reactive.server.WebTestClient;

import java.net.URI;
import java.nio.charset.StandardCharsets;

import static org.assertj.core.api.Assertions.assertThat;

@AutoConfigureWebTestClient
@SpringBootTest(webEnvironment = WebEnvironment.RANDOM_PORT)
class DemoApplicationTests {

    @Autowired
    private WebTestClient testClient;

    @Test
    void echoWithCorrectHeaders() {
        testClient.post() //
            .uri("/foos")
            .header("ce-id", "12345") //
            .header("ce-specversion", "1.0") //
            .header("ce-type", "io.spring.event") //
            .header("ce-source", "https://spring.io/events") //
            .contentType(MediaType.APPLICATION_JSON) //
            .bodyValue("{\"value\":\"Dave\"}") //
            .exchange() //
            .expectStatus().isOk() //
            .expectHeader().exists("ce-id") //
            .expectHeader().exists("ce-source") //
            .expectHeader().exists("ce-type") //
            .expectHeader().value("ce-id", value -> assertThat(value).isNotEqualTo("12345")) //
            .expectHeader().valueEquals("ce-type", "io.spring.event.Foo") //
            .expectHeader().valueEquals("ce-source", "https://spring.io/foos") //
            .expectBody(String.class).isEqualTo("{\"value\":\"Dave\"}");
    }

    @Test
    void structuredRequestResponseEvents() {
        testClient.post() //
            .uri("/event") //
            .contentType(new MediaType("application", "cloudevents+json")) //
            .bodyValue("""
                {
                "id":"12345",
                "specversion":"1.0",
                "type":"io.spring.event",
                "source":"https://spring.io/events",
                "data":{"value":"Dave"}}""") //
            .exchange() //
            .expectStatus().isOk() //
            .expectHeader().exists("ce-id") //
            .expectHeader().exists("ce-source") //
            .expectHeader().exists("ce-type") //
            .expectHeader().value("ce-id", value -> assertThat(value).isNotEqualTo("12345")) //
            .expectHeader().valueEquals("ce-type", "io.spring.event.Foo") //
            .expectHeader().valueEquals("ce-source", "https://spring.io/foos") //
            .expectBody(String.class).isEqualTo("{\"value\":\"Dave\"}");
    }

    @Test
    void structuredRequestResponseCloudEventToString() {
        testClient.post() //
            .uri("/event") //
            .bodyValue(CloudEventBuilder.v1() //
                .withId("12345") //
                .withType("io.spring.event") //
                .withSource(URI.create("https://spring.io/events")).withData("{\"value\":\"Dave\"}".getBytes(StandardCharsets.UTF_8)) //
                .build()) //
            .exchange() //
            .expectStatus().isOk() //
            .expectHeader().exists("ce-id") //
            .expectHeader().exists("ce-source") //
            .expectHeader().exists("ce-type") //
            .expectHeader().value("ce-id", value -> assertThat(value).isNotEqualTo("12345")) //
            .expectHeader().valueEquals("ce-type", "io.spring.event.Foo") //
            .expectHeader().valueEquals("ce-source", "https://spring.io/foos") //
            .expectBody(String.class).isEqualTo("{\"value\":\"Dave\"}");
    }

    @Test
    void structuredRequestResponseCloudEventToCloudEvent() {
        testClient.post() //
            .uri("/event") //
            .accept(new MediaType("application", "cloudevents+json")) //
            .bodyValue(CloudEventBuilder.v1() //
                .withId("12345") //
                .withType("io.spring.event") //
                .withSource(URI.create("https://spring.io/events")) //
                .withData("{\"value\":\"Dave\"}".getBytes(StandardCharsets.UTF_8)) //
                .build()) //
            .exchange() //
            .expectStatus().isOk() //
            .expectHeader().exists("ce-id") //
            .expectHeader().exists("ce-source") //
            .expectHeader().exists("ce-type") //
            .expectHeader().value("ce-id", value -> assertThat(value).isNotEqualTo("12345")) //
            .expectHeader().valueEquals("ce-type", "io.spring.event.Foo") //
            .expectHeader().valueEquals("ce-source", "https://spring.io/foos") //
            .expectBody(CloudEvent.class) //
            .value(event -> assertThat(new String(event.getData().toBytes())) //
                .isEqualTo("{\"value\":\"Dave\"}"));
    }

    @Test
    void requestResponseEvents() {
        testClient.post() //
            .uri("/event") //
            .header("ce-id", "12345") //
            .header("ce-specversion", "1.0") //
            .header("ce-type", "io.spring.event") //
            .header("ce-source", "https://spring.io/events") //
            .contentType(MediaType.APPLICATION_JSON) //
            .bodyValue("{\"value\":\"Dave\"}") //
            .exchange() //
            .expectStatus().isOk() //
            .expectHeader().exists("ce-id") //
            .expectHeader().exists("ce-source") //
            .expectHeader().exists("ce-type") //
            .expectHeader().value("ce-id", value -> assertThat(value).isNotEqualTo("12345")) //
            .expectHeader().valueEquals("ce-type", "io.spring.event.Foo") //
            .expectHeader().valueEquals("ce-source", "https://spring.io/foos") //
            .expectBody(String.class).isEqualTo("{\"value\":\"Dave\"}");
    }
}
