/*
 * Copyright 2018 The CloudEvents Authors
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package io.cloudevents.http.vertx;

import io.cloudevents.CloudEvent;
import io.cloudevents.CloudEventBuilder;
import io.cloudevents.http.reactivex.vertx.VertxCloudEvents;
import io.vertx.core.http.HttpHeaders;
import io.vertx.junit5.Checkpoint;
import io.vertx.junit5.VertxExtension;
import io.vertx.junit5.VertxTestContext;
import io.vertx.reactivex.core.Vertx;
import io.vertx.reactivex.core.http.HttpClientRequest;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;

import java.net.URI;
import java.util.logging.Logger;

import static io.cloudevents.CloudEvent.CLOUD_EVENTS_VERSION_KEY;
import static io.cloudevents.CloudEvent.EVENT_TYPE_KEY;
import static org.assertj.core.api.Assertions.assertThat;

@ExtendWith(VertxExtension.class)
class VertxCloudEventsTests {

    private final static Logger logger = Logger.getLogger(VertxCloudEventsTests.class.getName());

    @Test
    @DisplayName("Post a cloud event with a payload")
    void cloudEventWithPayload(Vertx vertx, VertxTestContext testContext) {
        Checkpoint serverCheckpoint = testContext.checkpoint();
        Checkpoint clientCheckpoint = testContext.checkpoint();

        CloudEvent<String> cloudEvent = new CloudEventBuilder<String>()
                .source(URI.create("http://knative-eventing.com"))
                .eventID("foo-bar")
                .eventType("pushevent")
                .data("{\"foo\":\"bar\"}}")
                .build();

        vertx.createHttpServer()
                .requestHandler(req -> VertxCloudEvents
                        .create()
                        .rxReadFromRequest(req)
                        .doOnError(testContext::failNow)
                        .subscribe(event -> testContext.verify(() -> {
                            assertThat(event.getEventID()).isEqualTo(cloudEvent.getEventID());
                            assertThat(event.getSource().toString()).isEqualTo(cloudEvent.getSource().toString());
                            assertThat(event.getEventType()).isEqualTo(cloudEvent.getEventType());
                            assertThat(event.getData()).isPresent();
                            req.response().end();
                            serverCheckpoint.flag();
                        })))
                .rxListen()
                .doOnError(testContext::failNow)
                .subscribe(server -> {
                    HttpClientRequest req = vertx.createHttpClient().post(server.actualPort(), "localhost", "/");
                    req.handler(resp -> testContext.verify(() -> {
                        assertThat(resp.statusCode()).isEqualTo(200);
                        clientCheckpoint.flag();
                    }));
                    VertxCloudEvents.create().writeToHttpClientRequest(cloudEvent, req);
                    req.end();
                });
    }

    @Test
    @DisplayName("Post a cloud event without a payload")
    void cloudEventWithoutPayload(Vertx vertx, VertxTestContext testContext) {
        Checkpoint serverCheckpoint = testContext.checkpoint();
        Checkpoint clientCheckpoint = testContext.checkpoint();

        CloudEvent<String> cloudEvent = new CloudEventBuilder<String>()
                .source(URI.create("http://knative-eventing.com"))
                .eventID("foo-bar")
                .eventType("pushevent")
                .build();

        vertx.createHttpServer()
                .requestHandler(req -> VertxCloudEvents
                        .create()
                        .rxReadFromRequest(req)
                        .doOnError(testContext::failNow)
                        .subscribe(event -> testContext.verify(() -> {
                            assertThat(event.getEventID()).isEqualTo(cloudEvent.getEventID());
                            assertThat(event.getSource().toString()).isEqualTo(cloudEvent.getSource().toString());
                            assertThat(event.getEventType()).isEqualTo(cloudEvent.getEventType());
                            assertThat(event.getData()).isNotPresent();
                            req.response().end();
                            serverCheckpoint.flag();
                        })))
                .rxListen()
                .doOnError(testContext::failNow)
                .subscribe(server -> {
                    HttpClientRequest req = vertx.createHttpClient().post(server.actualPort(), "localhost", "/");
                    req.handler(resp -> testContext.verify(() -> {
                        assertThat(resp.statusCode()).isEqualTo(200);
                        clientCheckpoint.flag();
                    }));
                    VertxCloudEvents.create().writeToHttpClientRequest(cloudEvent, req);
                    req.end();
                });
    }

    @Test
    @DisplayName("Post an incomplete cloud event")
    void incompleteCloudEvent(Vertx vertx, VertxTestContext testContext) {
        Checkpoint serverCheckpoint = testContext.checkpoint();
        Checkpoint clientCheckpoint = testContext.checkpoint();

        vertx.createHttpServer()
                .requestHandler(req -> VertxCloudEvents
                        .create()
                        .rxReadFromRequest(req)
                        .subscribe((e, t) -> {
                            if (e != null) {
                                testContext.failNow(new AssertionError("request was not complete, but got: " + e));
                            } else {
                                req.response().end();
                                serverCheckpoint.flag();
                            }
                        }))
                .rxListen()
                .doOnError(testContext::failNow)
                .subscribe(server -> {
                    HttpClientRequest req = vertx.createHttpClient().post(server.actualPort(), "localhost", "/");
                    // create incomplete CloudEvent request
                    req.putHeader(HttpHeaders.createOptimized(CLOUD_EVENTS_VERSION_KEY), HttpHeaders.createOptimized("0.1"));
                    req.putHeader(HttpHeaders.createOptimized(EVENT_TYPE_KEY), HttpHeaders.createOptimized("pushevent"));
                    req.putHeader(HttpHeaders.CONTENT_LENGTH, HttpHeaders.createOptimized("0"));
                    req.handler(resp -> testContext.verify(() -> {
                        assertThat(resp.statusCode()).isEqualTo(200);
                        clientCheckpoint.flag();
                    }));
                    req.end();
                });
    }
}
