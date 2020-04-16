/**
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
import io.cloudevents.extensions.DistributedTracingExtension;
import io.cloudevents.extensions.ExtensionFormat;
import io.cloudevents.http.reactivex.vertx.VertxCloudEvents;
import io.cloudevents.v02.AttributesImpl;
import io.cloudevents.v02.CloudEventBuilder;
import io.vertx.core.http.HttpHeaders;
import io.vertx.junit5.Checkpoint;
import io.vertx.junit5.VertxExtension;
import io.vertx.junit5.VertxTestContext;
import io.vertx.reactivex.core.Vertx;
import io.vertx.reactivex.core.http.HttpClientRequest;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.URI;

import static org.assertj.core.api.Assertions.assertThat;

@ExtendWith(VertxExtension.class)
class VertxCloudEventsTests {

    private int port;
    @BeforeEach
    void setUp(Vertx vertx, VertxTestContext testContext) throws IOException {
        ServerSocket socket = new ServerSocket(0);
        port = socket.getLocalPort();
        socket.close();
        testContext.completeNow();
    }

    @Test
    @DisplayName("Post a 0.2 CloudEvents object with a payload")
    void cloudEventWithPayload(Vertx vertx, VertxTestContext testContext) {
        Checkpoint serverCheckpoint = testContext.checkpoint();
        Checkpoint clientCheckpoint = testContext.checkpoint();

        // given
        final CloudEvent<AttributesImpl, String> cloudEvent = 
        	CloudEventBuilder.<String>builder()
                .withSource(URI.create("http://knative-eventing.com"))
                .withId("foo-bar")
                .withType("pushevent")
                .withData("{\"foo\":\"bar\"}}")
                .withContenttype("application/json")
                .build();

        vertx.createHttpServer()
                .requestHandler(req -> VertxCloudEvents
                        .create()
                        // read the object from the server request
                        .rxReadFromRequest(req)
                        .doOnError(testContext::failNow)
                        .subscribe(event -> testContext.verify(() -> {

                            // test
                            assertThat(event.getAttributes().getId()).isEqualTo(cloudEvent.getAttributes().getId());
                            assertThat(event.getAttributes().getSource().toString()).isEqualTo(cloudEvent.getAttributes().getSource().toString());
                            assertThat(event.getAttributes().getType()).isEqualTo(cloudEvent.getAttributes().getType());
                            assertThat(event.getData()).isPresent();

                            // write the response back to the caller
                            req.response().end();
                            serverCheckpoint.flag();
                        })))
                .rxListen(port)
                .doOnError(testContext::failNow)
                .subscribe(server -> {
                    // create client to POST a CloudEvent to the server
                    final HttpClientRequest req = vertx.createHttpClient().post(server.actualPort(), "localhost", "/");
                    req.handler(resp -> testContext.verify(() -> {
                        assertThat(resp.statusCode()).isEqualTo(200);
                        clientCheckpoint.flag();
                    }));
                    VertxCloudEvents.create().writeToHttpClientRequest(cloudEvent, req);
                });
    }

    @Test
    @DisplayName("Post a 0.2 CloudEvents object without a payload")
    void cloudEventWithoutPayload(Vertx vertx, VertxTestContext testContext) {
        Checkpoint serverCheckpoint = testContext.checkpoint();
        Checkpoint clientCheckpoint = testContext.checkpoint();

        // given
        final CloudEvent<AttributesImpl, String> cloudEvent = 
        	CloudEventBuilder.<String>builder()
                .withSource(URI.create("http://knative-eventing.com"))
                .withId("foo-bar")
                .withType("pushevent")
                .withContenttype("application/json")
                .build();

        vertx.createHttpServer()
                .requestHandler(req -> VertxCloudEvents
                        .create()
                        // read the object from the server request
                        .rxReadFromRequest(req)
                        .doOnError(testContext::failNow)
                        .subscribe(event -> testContext.verify(() -> {

                            // check headers
                            assertThat(req.headers().get("ce-specversion")).isEqualTo("0.2");
                            assertThat(req.headers().get("cloudEventsVersion")).isNull();
                            assertThat(req.headers().get("ce-id")).isEqualTo("foo-bar");
                            assertThat(req.headers().get("ce-eventID")).isNull();

                            // check parsed object
                            assertThat(event.getAttributes().getId()).isEqualTo(cloudEvent.getAttributes().getId());
                            assertThat(event.getAttributes().getSource().toString()).isEqualTo(cloudEvent.getAttributes().getSource().toString());
                            assertThat(event.getAttributes().getType()).isEqualTo(cloudEvent.getAttributes().getType());
                            assertThat(event.getData()).isNotPresent();

                            // write the response back to the caller
                            req.response().end();
                            serverCheckpoint.flag();
                        })))
                .rxListen(port)
                .doOnError(testContext::failNow)
                .subscribe(server -> {
                    // create client to POST a CloudEvent to the server
                    final HttpClientRequest req = vertx.createHttpClient().post(server.actualPort(), "localhost", "/");
                    req.handler(resp -> testContext.verify(() -> {
                        assertThat(resp.statusCode()).isEqualTo(200);
                        clientCheckpoint.flag();
                    }));
                    VertxCloudEvents.create().writeToHttpClientRequest(cloudEvent, req);
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
                        // read the object from the server request
                        .rxReadFromRequest(req)
                        .subscribe((e, t) -> {
                            if (e != null) {
                                testContext.failNow(new AssertionError("request was not complete, but got: " + e));
                            } else {
                                req.response().end();
                                serverCheckpoint.flag();
                            }
                        }))
                .rxListen(port)
                .doOnError(testContext::failNow)
                .subscribe(server -> {

                    // create client to POST a CloudEvent to the server
                    final HttpClientRequest req = vertx.createHttpClient().post(server.actualPort(), "localhost", "/");
                    // create incomplete CloudEvent request
                    req.putHeader(HttpHeaders.createOptimized("ce-specversion"), HttpHeaders.createOptimized("0.2"));
                    req.putHeader(HttpHeaders.createOptimized("ce-type"), HttpHeaders.createOptimized("pushevent"));
                    req.putHeader(HttpHeaders.createOptimized("foo"), HttpHeaders.createOptimized("bar"));
                    req.putHeader(HttpHeaders.CONTENT_LENGTH, HttpHeaders.createOptimized("0"));
                    req.handler(resp -> testContext.verify(() -> {
                        assertThat(resp.statusCode()).isEqualTo(200);
                        clientCheckpoint.flag();
                    }));
                    req.end();
                });
    }

    @Test
    @DisplayName("Post a 0.2 CloudEvents object with a payload")
    void cloudEventWithExtension(Vertx vertx, VertxTestContext testContext) {
        Checkpoint serverCheckpoint = testContext.checkpoint();
        Checkpoint clientCheckpoint = testContext.checkpoint();

        final DistributedTracingExtension dte = new DistributedTracingExtension();
        dte.setTraceparent("00-0af7651916cd43dd8448eb211c80319c-b7ad6b7169203331-01");
        dte.setTracestate("congo=BleGNlZWRzIHRohbCBwbGVhc3VyZS4");
        
        final ExtensionFormat tracing = new DistributedTracingExtension.Format(dte);

        final CloudEvent<AttributesImpl, String> cloudEvent = 
        	CloudEventBuilder.<String>builder()
                .withSource(URI.create("http://knative-eventing.com"))
                .withId("foo-bar")
                .withExtension(tracing)
                .withType("pushevent")
                .withContenttype("application/json")
                .build();

        final Class[] extensions =  {DistributedTracingExtension.class};

        vertx.createHttpServer()
                .requestHandler(req -> VertxCloudEvents
                        .create()
                        // read from request with expected Extension types
                        .rxReadFromRequest(req, extensions)
                        .doOnError(testContext::failNow)
                        .subscribe(event -> testContext.verify(() -> {
                            assertThat(event.getAttributes().getId()).isEqualTo(cloudEvent.getAttributes().getId());

                            // test
                            System.out.println(">>>>>>>>>>>>" + req.headers());
                            assertThat(req.headers().get("traceparent")).isEqualTo("00-0af7651916cd43dd8448eb211c80319c-b7ad6b7169203331-01");
                            assertThat(req.headers().get("tracestate")).isEqualTo("congo=BleGNlZWRzIHRohbCBwbGVhc3VyZS4");
                            assertThat(event.getExtensions().get("distributedTracing")).isNotNull();
                            assertThat(event.getExtensions().get("distributedTracing")).extracting("traceparent", "tracestate")
                                    .contains("00-0af7651916cd43dd8448eb211c80319c-b7ad6b7169203331-01", "congo=BleGNlZWRzIHRohbCBwbGVhc3VyZS4");

                            // write the response back to the caller
                            req.response().end();
                            serverCheckpoint.flag();
                        })))
                .rxListen(port)
                .doOnError(testContext::failNow)
                .subscribe(server -> {
                    // create client to POST a CloudEvent to the server
                    final HttpClientRequest req = vertx.createHttpClient().post(server.actualPort(), "localhost", "/");
                    req.handler(resp -> testContext.verify(() -> {
                        assertThat(resp.statusCode()).isEqualTo(200);
                        clientCheckpoint.flag();
                    }));
                    VertxCloudEvents.create().writeToHttpClientRequest(cloudEvent, req);
                });
    }

    @Test
    @DisplayName("Post a 0.2 CloudEvents object without a payload")
    void structuredCloudEvent(Vertx vertx, VertxTestContext testContext) {
        Checkpoint serverCheckpoint = testContext.checkpoint();
        Checkpoint clientCheckpoint = testContext.checkpoint();

        // given
        final CloudEvent<AttributesImpl, String> cloudEvent = 
        	CloudEventBuilder.<String>builder()
                .withSource(URI.create("http://knative-eventing.com"))
                .withId("foo-bar")
                .withType("pushevent")
                .build();

        vertx.createHttpServer()
                .requestHandler(req -> VertxCloudEvents
                        .create()
                        // read the object from the server request
                        .rxReadFromRequest(req)
                        .doOnError(testContext::failNow)
                        .subscribe(event -> testContext.verify(() -> {
                            // write the response back to the caller
                            req.response().end();
                            serverCheckpoint.flag();
                        })))
                .rxListen(port)
                .doOnError(testContext::failNow)
                .subscribe(server -> {
                    // create client to POST a CloudEvent to the server
                    final HttpClientRequest req = vertx.createHttpClient().post(server.actualPort(), "localhost", "/");
                    req.handler(resp -> testContext.verify(() -> {
                        assertThat(resp.statusCode()).isEqualTo(200);
                        clientCheckpoint.flag();
                    }));
                    VertxCloudEvents.create().writeToHttpClientRequest(cloudEvent, Boolean.FALSE,req);
                });
    }
}
