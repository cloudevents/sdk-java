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

package io.cloudevents.spring.webmvc;

import java.net.URI;
import io.cloudevents.spring.webmvc.CloudeventDemoApplication.Person;

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

/**
 * @author Dave Syer
 *
 */
@SpringBootTest(webEnvironment = WebEnvironment.RANDOM_PORT)
public class CloudeventDemoApplicationTests {

    @Autowired
    private TestRestTemplate rest;

    @LocalServerPort
    private int port;

    @Test
    void testAsBinary() {
        ResponseEntity<String> response = rest.exchange(RequestEntity.post(URI.create("http://localhost:" + port + "/")) //
                .header("ce-id", "12345") //
                .header("ce-specversion", "1.0") //
                .header("ce-type", "io.spring.event") //
                .header("ce-source", "https://spring.io/events") //
                .contentType(MediaType.APPLICATION_JSON) //
                .body("{\"name\":\"Dave\"}"), String.class);

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(response.getBody()).isEqualTo("{\"name\":\"Dave\"}");

        HttpHeaders headers = response.getHeaders();

        assertThat(headers).containsKey("ce-id");
        assertThat(headers).containsKey("ce-source");
        assertThat(headers).containsKey("ce-type");

        // assertThat(headers.getFirst("ce-id")).isNotEqualTo("12345");
        assertThat(headers.getFirst("ce-type")).isEqualTo(Person.class.getName());
        assertThat(headers.getFirst("ce-source")).isEqualTo("https://spring.io/ce-webmvc/binary");

    }

    @Test
    void testAsStructured() {
        ResponseEntity<String> response = rest.exchange(RequestEntity.post(URI.create("http://localhost:" + port + "/")) //
                .contentType(new MediaType("application", "cloudevents+json")) //
                .body("{" //
                        + "\"id\":\"12345\"," //
                        + "\"specversion\":\"1.0\"," //
                        + "\"type\":\"io.spring.event\"," //
                        + "\"source\":\"https://spring.io/events\"," //
                        + "\"data\":{\"name\":\"Dave\"}}"),
                String.class);

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(response.getBody()).isEqualTo("{\"name\":\"Dave\"}");

        HttpHeaders headers = response.getHeaders();

        assertThat(headers).containsKey("ce-id");
        assertThat(headers).containsKey("ce-source");
        assertThat(headers).containsKey("ce-type");

        assertThat(headers.getFirst("ce-type")).isEqualTo(Person.class.getName());
        assertThat(headers.getFirst("ce-source")).isEqualTo("https://spring.io/ce-webmvc/structured");

    }
}
