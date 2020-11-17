/*
 * Copyright 2020-Present The CloudEvents Authors
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

package io.cloudevents.spring.http;

import java.net.URI;
import java.util.Map;
import java.util.UUID;

import io.cloudevents.spring.core.CloudEventAttributeUtils;
import io.cloudevents.spring.core.MutableCloudEventAttributes;
import org.junit.jupiter.api.Test;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.context.SpringBootTest.WebEnvironment;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.boot.web.server.LocalServerPort;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.RequestEntity;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RestController;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * @author Dave Syer
 *
 */
@SpringBootTest(webEnvironment = WebEnvironment.RANDOM_PORT)
class RestControllerTests {

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
	void structured() {

		ResponseEntity<String> response = rest.exchange(RequestEntity.post(URI.create("http://localhost:" + port + "/")) //
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

		// assertThat(headers.getFirst("ce-id")).isNotEqualTo("12345");
		assertThat(headers.getFirst("ce-type")).isEqualTo("io.spring.event.Foo");
		assertThat(headers.getFirst("ce-source")).isEqualTo("https://spring.io/foos");

	}

	@SpringBootApplication
	@RestController
	static class TestApplication {

		@PostMapping("/")
		public ResponseEntity<Foo> echo(@RequestBody Foo foo, @RequestHeader HttpHeaders headers) {
			MutableCloudEventAttributes attributes = CloudEventHttpUtils.fromHttp(headers)
					.setId(UUID.randomUUID().toString()).setSource(URI.create("https://spring.io/foos"))
					.setType("io.spring.event.Foo");
			HttpHeaders outgoing = CloudEventHttpUtils.toHttp(attributes);
			return ResponseEntity.ok().headers(outgoing).body(foo);
		}

		@PostMapping(path = "/", consumes = "application/cloudevents+json")
		public ResponseEntity<Object> structured(@RequestBody Map<String, Object> body,
				@RequestHeader HttpHeaders headers) {
			MutableCloudEventAttributes attributes = CloudEventAttributeUtils.toAttributes(body)
					.setId(UUID.randomUUID().toString()).setSource(URI.create("https://spring.io/foos"))
					.setType("io.spring.event.Foo");
			HttpHeaders outgoing = CloudEventHttpUtils.toHttp(attributes);
			return ResponseEntity.ok().headers(outgoing).body(body.get(CloudEventAttributeUtils.DATA));
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
