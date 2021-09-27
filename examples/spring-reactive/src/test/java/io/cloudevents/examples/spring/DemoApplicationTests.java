package io.cloudevents.examples.spring;

import java.net.URI;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.context.SpringBootTest.WebEnvironment;
import org.springframework.http.MediaType;
import org.springframework.test.web.reactive.server.WebTestClient;

import io.cloudevents.CloudEvent;
import io.cloudevents.core.builder.CloudEventBuilder;
import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest(webEnvironment = WebEnvironment.RANDOM_PORT)
public class DemoApplicationTests {

    @Autowired
    private WebTestClient rest;

    @Test
    void echoWithCorrectHeaders() {

        rest.post().uri("/foos").header("ce-id", "12345") //
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
                .expectHeader().value("ce-id", value -> {
                    if (value.equals("12345"))
                        throw new IllegalStateException();
                }) //
                .expectHeader().valueEquals("ce-type", "io.spring.event.Foo") //
                .expectHeader().valueEquals("ce-source", "https://spring.io/foos") //
                .expectBody(String.class).isEqualTo("{\"value\":\"Dave\"}");

    }

    @Test
    void structuredRequestResponseEvents() {

        rest.post().uri("/event") //
                .contentType(new MediaType("application", "cloudevents+json")) //
                .bodyValue("{" //
                        + "\"id\":\"12345\"," //
                        + "\"specversion\":\"1.0\"," //
                        + "\"type\":\"io.spring.event\"," //
                        + "\"source\":\"https://spring.io/events\"," //
                        + "\"data\":{\"value\":\"Dave\"}}") //
                .exchange() //
                .expectStatus().isOk() //
                .expectHeader().exists("ce-id") //
                .expectHeader().exists("ce-source") //
                .expectHeader().exists("ce-type") //
                .expectHeader().value("ce-id", value -> {
                    if (value.equals("12345"))
                        throw new IllegalStateException();
                }) //
                .expectHeader().valueEquals("ce-type", "io.spring.event.Foo") //
                .expectHeader().valueEquals("ce-source", "https://spring.io/foos") //
                .expectBody(String.class).isEqualTo("{\"value\":\"Dave\"}");

    }

    @Test
    void structuredRequestResponseCloudEventToString() {

        rest.post().uri("/event") //
                .bodyValue(CloudEventBuilder.v1() //
                        .withId("12345") //
                        .withType("io.spring.event") //
                        .withSource(URI.create("https://spring.io/events")).withData("{\"value\":\"Dave\"}".getBytes()) //
                        .build()) //
                .exchange() //
                .expectStatus().isOk() //
                .expectHeader().exists("ce-id") //
                .expectHeader().exists("ce-source") //
                .expectHeader().exists("ce-type") //
                .expectHeader().value("ce-id", value -> {
                    if (value.equals("12345"))
                        throw new IllegalStateException();
                }) //
                .expectHeader().valueEquals("ce-type", "io.spring.event.Foo") //
                .expectHeader().valueEquals("ce-source", "https://spring.io/foos") //
                .expectBody(String.class).isEqualTo("{\"value\":\"Dave\"}");

    }

    @Test
    void structuredRequestResponseCloudEventToCloudEvent() {

        rest.post().uri("/event") //
                .accept(new MediaType("application", "cloudevents+json")) //
                .bodyValue(CloudEventBuilder.v1() //
                        .withId("12345") //
                        .withType("io.spring.event") //
                        .withSource(URI.create("https://spring.io/events")) //
                        .withData("{\"value\":\"Dave\"}".getBytes()) //
                        .build()) //
                .exchange() //
                .expectStatus().isOk() //
                .expectHeader().exists("ce-id") //
                .expectHeader().exists("ce-source") //
                .expectHeader().exists("ce-type") //
                .expectHeader().value("ce-id", value -> {
                    if (value.equals("12345"))
                        throw new IllegalStateException();
                }) //
                .expectHeader().valueEquals("ce-type", "io.spring.event.Foo") //
                .expectHeader().valueEquals("ce-source", "https://spring.io/foos") //
                .expectBody(CloudEvent.class) //
                .value(event -> assertThat(new String(event.getData().toBytes())) //
                        .isEqualTo("{\"value\":\"Dave\"}"));

    }

    @Test
    void requestResponseEvents() {

        rest.post().uri("/event").header("ce-id", "12345") //
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
                .expectHeader().value("ce-id", value -> {
                    if (value.equals("12345"))
                        throw new IllegalStateException();
                }) //
                .expectHeader().valueEquals("ce-type", "io.spring.event.Foo") //
                .expectHeader().valueEquals("ce-source", "https://spring.io/foos") //
                .expectBody(String.class).isEqualTo("{\"value\":\"Dave\"}");

    }

}
