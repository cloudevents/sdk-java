package io.cloudevents.examples.spring;

import java.net.URI;

import org.junit.jupiter.api.Test;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.context.SpringBootTest.WebEnvironment;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.boot.web.server.LocalServerPort;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.RequestEntity;
import org.springframework.http.ResponseEntity;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest(webEnvironment = WebEnvironment.RANDOM_PORT)
public class DemoApplicationTests {

	@Autowired
	private TestRestTemplate rest;

	@LocalServerPort
	private int port;

	@Test
	void echoWithCorrectHeaders() {

		ResponseEntity<String> response = rest
				.exchange(RequestEntity.post(URI.create("http://localhost:" + port + "/foos")) //
						.header("ce-id", "12345") //
						.header("ce-specversion", "1.0") //
						.header("ce-type", "io.spring.event") //
						.header("ce-source", "https://spring.io/events") //
						.contentType(MediaType.APPLICATION_JSON) //
						.body("{\"value\":\"Dave\"}"), String.class);

		assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
		assertThat(response.getBody()).isEqualTo("{\"value\":\"Dave\"}");

		HttpHeaders headers = response.getHeaders();

		assertThat(headers).containsKey("ce-id");
		assertThat(headers).containsKey("ce-source");
		assertThat(headers).containsKey("ce-type");

		assertThat(headers.getFirst("ce-id")).isNotEqualTo("12345");
		assertThat(headers.getFirst("ce-type")).isEqualTo("io.spring.event.Foo");
		assertThat(headers.getFirst("ce-source")).isEqualTo("https://spring.io/foos");

	}

	@Test
	void structuredRequestResponseEvents() {

		ResponseEntity<String> response = rest
				.exchange(RequestEntity.post(URI.create("http://localhost:" + port + "/event")) //
						.contentType(new MediaType("application", "cloudevents+json")) //
						.body("{" //
								+ "\"id\":\"12345\"," //
								+ "\"specversion\":\"1.0\"," //
								+ "\"type\":\"io.spring.event\"," //
								+ "\"source\":\"https://spring.io/events\"," //
								+ "\"data\":{\"value\":\"Dave\"}}"),
						String.class);

		assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
		assertThat(response.getBody()).isEqualTo("{\"value\":\"Dave\"}");

		HttpHeaders headers = response.getHeaders();

		assertThat(headers).containsKey("ce-id");
		assertThat(headers).containsKey("ce-source");
		assertThat(headers).containsKey("ce-type");

		assertThat(headers.getFirst("ce-id")).isNotEqualTo("12345");
		assertThat(headers.getFirst("ce-type")).isEqualTo("io.spring.event.Foo");
		assertThat(headers.getFirst("ce-source")).isEqualTo("https://spring.io/foos");

	}

	@Test
	void requestResponseEvents() {

		ResponseEntity<String> response = rest
				.exchange(RequestEntity.post(URI.create("http://localhost:" + port + "/event")) //
						.header("ce-id", "12345") //
						.header("ce-specversion", "1.0") //
						.header("ce-type", "io.spring.event") //
						.header("ce-source", "https://spring.io/events") //
						.contentType(MediaType.APPLICATION_JSON) //
						.body("{\"value\":\"Dave\"}"), String.class);

		assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
		assertThat(response.getBody()).isEqualTo("{\"value\":\"Dave\"}");

		HttpHeaders headers = response.getHeaders();

		assertThat(headers).containsKey("ce-id");
		assertThat(headers).containsKey("ce-source");
		assertThat(headers).containsKey("ce-type");

		assertThat(headers.getFirst("ce-id")).isNotEqualTo("12345");
		assertThat(headers.getFirst("ce-type")).isEqualTo("io.spring.event.Foo");
		assertThat(headers.getFirst("ce-source")).isEqualTo("https://spring.io/foos");

	}

}
