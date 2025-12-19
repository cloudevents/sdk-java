/*
 * Copyright 2018-Present The CloudEvents Authors
 * <p>
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * <p>
 * http://www.apache.org/licenses/LICENSE-2.0
 * <p>
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 *
 */

package io.cloudevents.http.restful.ws.spring;

import io.cloudevents.CloudEvent;
import io.cloudevents.core.mock.CSVFormat;
import io.cloudevents.core.provider.EventFormatProvider;
import io.cloudevents.core.test.Data;
import io.cloudevents.jackson.JsonFormat;
import io.cloudevents.spring.mvc.CloudEventHttpMessageConverter;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.server.LocalServerPort;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.web.client.RestClient;

import static org.assertj.core.api.Assertions.assertThat;

@ExtendWith(SpringExtension.class)
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
class TestSpringBootMvc {
    @LocalServerPort
    private int port;
    private RestClient target;

    @BeforeEach
    void beforeEach() {
        target = RestClient.builder()
            .configureMessageConverters(builder -> builder.addCustomConverter(new CloudEventHttpMessageConverter()))
            .baseUrl(String.format("http://localhost:%d/", port))
            .build();
    }

    @BeforeAll
    static void beforeAll() {
        EventFormatProvider.getInstance().registerFormat(CSVFormat.INSTANCE);
        EventFormatProvider.getInstance().registerFormat(new JsonFormat());
    }

    @Test
    void contextLoads() {
    }

    @Test
    void getMinEvent() {
        ResponseEntity<byte[]> res = target.get().uri("getMinEvent").retrieve().toEntity(byte[].class);

        assertThat(res.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(res.getHeaders().toSingleValueMap()).containsEntry("ce-specversion", "1.0");
    }

    @Test
    void getStructuredEventCsv() {
        ResponseEntity<CloudEvent> res = target.get().uri("getStructuredEventCsv").retrieve().toEntity(CloudEvent.class);

        assertThat(res.getBody()).isEqualTo(Data.V1_MIN);
        assertThat(res.getHeaders().getContentType()).isEqualTo(MediaType.valueOf(CSVFormat.INSTANCE.serializedContentType()));
    }

    @Test
    void getStructuredEventJson() {
        ResponseEntity<CloudEvent> res = target.get().uri("getStructuredEventJson").retrieve().toEntity(CloudEvent.class);

        assertThat(res.getBody()).isEqualTo(Data.V1_MIN);
        assertThat(res.getHeaders().getContentType()).isEqualTo(MediaType.valueOf(JsonFormat.CONTENT_TYPE));
    }

    @Test
    void getEvent() {
        ResponseEntity<CloudEvent> res = target.get().uri("getEvent").retrieve().toEntity(CloudEvent.class);

        assertThat(res.getBody()).isEqualTo(Data.V1_WITH_JSON_DATA_WITH_EXT_STRING);
    }

    @Test
    void postEventStructuredCsv() {
        ResponseEntity<Void> res = target.post()
            .uri("postEventWithoutBody")
            .contentType(MediaType.valueOf("application/cloudevents+csv"))
            .body(Data.V1_MIN)
            .retrieve()
            .toBodilessEntity();

        assertThat(res.getStatusCode()).isEqualTo(HttpStatus.OK);
    }

    @Test
    void postEventStructuredJson() {
        ResponseEntity<Void> res = target.post()
            .uri("postEventWithoutBody")
            .contentType(MediaType.valueOf("application/cloudevents+json"))
            .body(Data.V1_MIN)
            .retrieve()
            .toBodilessEntity();

        assertThat(res.getStatusCode()).isEqualTo(HttpStatus.OK);
    }
}
