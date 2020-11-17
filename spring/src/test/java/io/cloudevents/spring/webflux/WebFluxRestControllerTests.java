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

import java.net.URI;
import java.util.UUID;

import io.cloudevents.CloudEvent;
import io.cloudevents.core.builder.CloudEventBuilder;
import io.cloudevents.spring.http.CloudEventHttpUtils;
import org.junit.jupiter.api.Test;
import reactor.core.publisher.Mono;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.context.SpringBootTest.WebEnvironment;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.boot.web.codec.CodecCustomizer;
import org.springframework.boot.web.server.LocalServerPort;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.RequestEntity;
import org.springframework.http.ResponseEntity;
import org.springframework.http.codec.CodecConfigurer;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RestController;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * @author Dave Syer
 *
 */
@SpringBootTest(webEnvironment = WebEnvironment.RANDOM_PORT, properties = "spring.main.web-application-type=REACTIVE")
class WebFluxRestControllerTests {

	@Autowired
	private TestRestTemplate rest;

	@LocalServerPort
	private int port;

	@Test
	void echoWithCorrectHeaders() {

		ResponseEntity<String> response = rest.exchange(RequestEntity.post(URI.create("http://localhost:" + port + "/")) //
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

		// assertThat(headers.getFirst("ce-id")).isNotEqualTo("12345");
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

		// assertThat(headers.getFirst("ce-id")).isNotEqualTo("12345");
		assertThat(headers.getFirst("ce-type")).isEqualTo("io.spring.event.Foo");
		assertThat(headers.getFirst("ce-source")).isEqualTo("https://spring.io/foos");

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
		public static class CloudEventHandlerConfiguration implements CodecCustomizer {

			@Override
			public void customize(CodecConfigurer configurer) {
				configurer.customCodecs().register(new CloudEventHttpMessageReader());
				configurer.customCodecs().register(new CloudEventHttpMessageWriter());
			}

		}

	}

}

class Foo {

	private String value;

	public Foo() {
	}

	public Foo(String value) {
		this.value = value;
	}

	public String getValue() {
		return this.value;
	}

	public void setValue(String value) {
		this.value = value;
	}

	@Override
	public String toString() {
		return "Foo [value=" + this.value + "]";
	}

}