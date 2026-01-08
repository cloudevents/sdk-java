package io.cloudevents.examples.spring;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.context.SpringBootTest.WebEnvironment;
import org.springframework.boot.test.web.server.LocalServerPort;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.client.ExchangeResult;
import org.springframework.test.web.servlet.client.RestTestClient;

import java.util.Map;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest(webEnvironment = WebEnvironment.RANDOM_PORT)
@Disabled("ContentType behaves odd after upgrading from deprecated TestRestTemplate")
class DemoApplicationTests {
    private static final String BODY = "{\"value\":\"Dave\"}";
	private RestTestClient rest;

	@LocalServerPort
	private int port;

    @BeforeEach
    void setUp() {
        rest = RestTestClient
            .bindToServer()
            .baseUrl(String.format("http://localhost:%d", port))
            .build();
    }

	@Test
	void echoWithCorrectHeaders() {
        ExchangeResult response = rest.post()
            .uri("/foos")
            .header("ce-id", "12345")
            .header("ce-specversion", "1.0")
            .header("ce-type", "io.spring.event")
            .header("ce-source", "https://spring.io/events")
            .contentType(MediaType.APPLICATION_JSON)
            .body(BODY)
            .exchange()
            .returnResult(String.class);

        assertThat(response.getStatus()).isEqualTo(HttpStatus.OK);
        assertThat(response.getResponseBodyContent()).isEqualTo(BODY.getBytes());

		Map<String, String> headers = response.getResponseHeaders().toSingleValueMap();

        assertThat(headers)
            .containsKey("ce-id")
            .containsKey("ce-source")
            .containsKey("ce-type")
            .doesNotContainEntry("ce-id", "12345")
            .containsEntry("ce-type", "io.spring.event.Foo")
            .containsEntry("ce-source", "https://spring.io/foos");
	}

	@Test
	void structuredRequestResponseEvents() {
        ExchangeResult response = rest.post()
            .uri("/event")
            .contentType(new MediaType("application", "cloudevents+json"))
            .body("""
                {
                    "id": "12345",
                    "specversion": "1.0",
                    "type": "io.spring.event",
                    "source": "https://spring.io/events",
                    "data": %s
                }
                """.formatted(BODY))
            .exchange()
            .returnResult(String.class);

        assertThat(response.getStatus()).isEqualTo(HttpStatus.OK);
        assertThat(response.getResponseBodyContent()).isEqualTo(BODY.getBytes());

        Map<String, String> headers = response.getResponseHeaders().toSingleValueMap();

        assertThat(headers)
            .containsKey("ce-id")
            .containsKey("ce-source")
            .containsKey("ce-type")
            .doesNotContainEntry("ce-id", "12345")
            .containsEntry("ce-type", "io.spring.event.Foo")
            .containsEntry("ce-source", "https://spring.io/foos");
	}

	@Test
	void requestResponseEvents() {
        ExchangeResult response = rest.post()
            .uri("/event")
            .header("ce-id", "12345")
            .header("ce-specversion", "1.0")
            .header("ce-type", "io.spring.event")
            .header("ce-source", "https://spring.io/events")
            .contentType(MediaType.APPLICATION_JSON)
            .body(BODY)
            .exchange()
            .returnResult(String.class);

        assertThat(response.getStatus()).isEqualTo(HttpStatus.OK);
        assertThat(response.getResponseBodyContent()).isEqualTo(BODY.getBytes());

        Map<String, String> headers = response.getResponseHeaders().toSingleValueMap();

        assertThat(headers)
            .containsKey("ce-id")
            .containsKey("ce-source")
            .containsKey("ce-type")
            .doesNotContainEntry("ce-id", "12345")
            .containsEntry("ce-type", "io.spring.event.Foo")
            .containsEntry("ce-source", "https://spring.io/foos");
	}

}
