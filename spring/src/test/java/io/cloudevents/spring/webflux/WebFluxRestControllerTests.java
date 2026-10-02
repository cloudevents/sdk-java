/*
 * Copyright 2019-2019 the original author or authors.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      https://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package io.cloudevents.spring.webflux;

import io.cloudevents.CloudEvent;
import io.cloudevents.core.builder.CloudEventBuilder;
import io.cloudevents.spring.http.CloudEventHttpUtils;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.context.SpringBootTest.WebEnvironment;
import org.springframework.boot.test.web.server.LocalServerPort;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.http.codec.ServerCodecConfigurer;
import org.springframework.test.web.reactive.server.WebTestClient;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.reactive.config.WebFluxConfigurer;
import reactor.core.publisher.Mono;

import java.net.URI;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * @author Dave Syer
 *
 */
@SpringBootTest(webEnvironment = WebEnvironment.RANDOM_PORT, properties = "spring.main.web-application-type=REACTIVE")
class WebFluxRestControllerTests {
    private static final String BODY = "{\"value\":\"Dave\"}";

    private WebTestClient testClient;

    @LocalServerPort
    private int port;

    @BeforeEach
    void setUp() {
        testClient = WebTestClient
            .bindToServer()
            .baseUrl(String.format("http://localhost:%d/", port))
            .build();
    }

    @Test
    void echoWithCorrectHeaders() {
        testClient.post()
            .header("ce-id", "12345")
            .header("ce-specversion", "1.0")
            .header("ce-type", "io.spring.event")
            .header("ce-source", "https://spring.io/events")
            .contentType(MediaType.APPLICATION_JSON)
            .bodyValue(BODY)
            .exchange()
            .expectStatus().isOk()
            .expectHeader().exists("ce-id")
            .expectHeader().exists("ce-source")
            .expectHeader().exists("ce-type")
            .expectHeader().valueEquals("ce-type", "io.spring.event.Foo")
            .expectHeader().valueEquals("ce-source", "https://spring.io/foos")
            .expectHeader().value("ce-id", value -> assertThat(value).isNotEqualTo("12345"))
            .expectBody(String.class).isEqualTo(BODY);
    }

    @Test
    void structuredRequestResponseEvents() {
        testClient.post()
            .uri("event")
            .contentType(new MediaType("application", "cloudevents+json"))
            .bodyValue("""
                    {
                        "id": "12345",
                        "specversion": "1.0",
                        "type": "io.spring.event",
                        "source": "https://spring.io/events",
                        "data": %s
                    }
                    """.formatted(BODY))
            .exchange()
            .expectStatus().isOk()
            .expectHeader().exists("ce-id")
            .expectHeader().exists("ce-source")
            .expectHeader().exists("ce-type")
            .expectHeader().valueEquals("ce-type", "io.spring.event.Foo")
            .expectHeader().valueEquals("ce-source", "https://spring.io/foos")
            .expectHeader().value("ce-id", value -> assertThat(value).isNotEqualTo("12345"))
            .expectBody(String.class).isEqualTo(BODY);
    }

    @Test
    void requestResponseEvents() {
        testClient.post()
            .uri("event")
            .header("ce-id", "12345")
            .header("ce-specversion", "1.0")
            .header("ce-type", "io.spring.event")
            .header("ce-source", "https://spring.io/events")
            .contentType(MediaType.APPLICATION_JSON)
            .bodyValue(BODY)
            .exchange()
            .expectStatus().isOk()
            .expectHeader().exists("ce-id")
            .expectHeader().exists("ce-source")
            .expectHeader().exists("ce-type")
            .expectHeader().valueEquals("ce-type", "io.spring.event.Foo")
            .expectHeader().valueEquals("ce-source", "https://spring.io/foos")
            .expectHeader().value("ce-id", value -> assertThat(value).isNotEqualTo("12345"))
            .expectBody(String.class).isEqualTo(BODY);
    }

    @SpringBootApplication
    @RestController
    static class TestApplication {

        @PostMapping("/")
        public ResponseEntity<Foo> echo(@RequestBody Foo foo, @RequestHeader HttpHeaders headers) {
            CloudEvent attributes = CloudEventHttpUtils.fromHttp(headers).withId(UUID.randomUUID().toString())
                .withSource(URI.create("https://spring.io/foos")).withType("io.spring.event.Foo").build();
            HttpHeaders outgoing = CloudEventHttpUtils.toHttp(attributes);
            return ResponseEntity.ok().headers(outgoing).body(foo);
        }

        @PostMapping("/event")
        public Mono<CloudEvent> event(@RequestBody Mono<CloudEvent> body) {
            return body.map(event -> CloudEventBuilder.from(event).withId(UUID.randomUUID().toString())
                .withSource(URI.create("https://spring.io/foos")).withType("io.spring.event.Foo")
                .withData(event.getData().toBytes()).build());
        }

        @Configuration
        public static class CloudEventHandlerConfiguration implements WebFluxConfigurer {

            @Override
            public void configureHttpMessageCodecs(ServerCodecConfigurer configurer) {
                configurer.customCodecs().register(new CloudEventHttpMessageReader());
                configurer.customCodecs().register(new CloudEventHttpMessageWriter());
            }
        }
    }
}

record Foo(
    String value
) {}
