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
import io.cloudevents.CloudEventBuilder;
import io.vertx.core.Vertx;
import io.vertx.core.http.HttpClientRequest;
import io.vertx.core.http.HttpHeaders;
import io.vertx.core.http.HttpServer;
import io.vertx.ext.unit.Async;
import io.vertx.ext.unit.TestContext;
import io.vertx.ext.unit.junit.VertxUnitRunner;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.URI;
import java.util.logging.Logger;

import static io.cloudevents.CloudEvent.CLOUD_EVENTS_VERSION_KEY;
import static io.cloudevents.CloudEvent.EVENT_TYPE_KEY;

@RunWith(VertxUnitRunner.class)
public class VertxCloudEventsTests {

    private final static Logger logger = Logger.getLogger(VertxCloudEventsTests.class.getName());

    private HttpServer server;
    private Vertx vertx;
    private int port;

    @Before
    public void setUp(TestContext context) throws IOException {
        vertx = Vertx.vertx();
        ServerSocket socket = new ServerSocket(0);
        port = socket.getLocalPort();
        socket.close();
        server = vertx.createHttpServer();
    }

    @After
    public void tearDown(TestContext context) {
        vertx.close(context.asyncAssertSuccess());
    }

    @Test
    public void cloudEventWithPayload(TestContext context) {
        final Async async = context.async();

        // Create the actuak CloudEvents object;
        final CloudEvent<String> cloudEvent = new CloudEventBuilder<String>()
                .source(URI.create("http://knative-eventing.com"))
                .eventID("foo-bar")
                .eventType("pushevent")
                .data("{\"foo\":\"bar\"}}")
                .build();

        // set up the server and add a handler to check the values
        server.requestHandler(req -> {

            VertxCloudEvents.create().readFromRequest(req, reply -> {

                if (reply.succeeded()) {

                    final CloudEvent<?> receivedEvent = reply.result();
                    context.assertEquals(receivedEvent.getEventID(), cloudEvent.getEventID());
                    context.assertEquals(receivedEvent.getSource().toString(), cloudEvent.getSource().toString());
                    context.assertEquals(receivedEvent.getEventType(), cloudEvent.getEventType());
                    context.assertEquals(receivedEvent.getData().isPresent(), Boolean.TRUE);
                }
            });

            req.response().end();
        }).listen(port, ar -> {
            if (ar.failed()) {
                context.fail("could not start server");
            } else {
                // sending it to the test-server
                final HttpClientRequest request = vertx.createHttpClient().post(port, "localhost", "/");

                VertxCloudEvents.create().writeToHttpClientRequest(cloudEvent, request);
                request.handler(response -> {
                    context.assertEquals(response.statusCode(), 200);

                    async.complete();
                });
                request.end();
            }
        });
        logger.info("running on port: " + port);

        async.awaitSuccess(1000);
    }

    @Test
    public void cloudEventWithoutPayload(TestContext context) {
        final Async async = context.async();

        // Create the actuak CloudEvents object;
        final CloudEvent<String> cloudEvent = new CloudEventBuilder<String>()
                .source(URI.create("http://knative-eventing.com"))
                .eventID("foo-bar")
                .eventType("pushevent")
                .build();

        // set up the server and add a handler to check the values
        server.requestHandler(req -> {

            VertxCloudEvents.create().readFromRequest(req, reply -> {

                if (reply.succeeded()) {

                    final CloudEvent<?> receivedEvent = reply.result();
                    context.assertEquals(receivedEvent.getEventID(), cloudEvent.getEventID());
                    context.assertEquals(receivedEvent.getSource().toString(), cloudEvent.getSource().toString());
                    context.assertEquals(receivedEvent.getEventType(), cloudEvent.getEventType());
                    context.assertEquals(receivedEvent.getData().isPresent(), Boolean.FALSE);
                }
            });

            req.response().end();
        }).listen(port, ar -> {
            if (ar.failed()) {
                context.fail("could not start server");
            } else {
                // sending it to the test-server
                final HttpClientRequest request = vertx.createHttpClient().post(port, "localhost", "/");

                VertxCloudEvents.create().writeToHttpClientRequest(cloudEvent, request);
                request.handler(resp -> {
                    context.assertEquals(resp.statusCode(), 200);
                    async.complete();

                });
                request.end();

            }
        });
        logger.info("running on port: " + port);

        async.awaitSuccess(1000);
    }

    @Test
    public void incompleteCloudEvent(TestContext context) {
        final Async async = context.async();

        // set up the server and add a handler to check the values
        server.requestHandler(req -> {

            VertxCloudEvents.create().readFromRequest(req, reply -> {

                if (reply.succeeded()) {

                    context.fail("request was not complete");
                } else {
                    context.assertEquals(reply.failed(), Boolean.TRUE);
                }
            });

            req.response().end();
        }).listen(port, ar -> {

            if (ar.failed()) {
                context.fail("could not start server");
            } else {
                // fire the request
                // sending it to the test-server
                final HttpClientRequest request = vertx.createHttpClient().post(port, "localhost", "/");
                // create incomplete CloudEvent request
                request.putHeader(HttpHeaders.createOptimized(CLOUD_EVENTS_VERSION_KEY), HttpHeaders.createOptimized("0.1"));
                request.putHeader(HttpHeaders.createOptimized(EVENT_TYPE_KEY), HttpHeaders.createOptimized("pushevent"));
                request.putHeader(HttpHeaders.CONTENT_LENGTH, HttpHeaders.createOptimized("0"));

                request.handler(resp -> {
                    context.assertEquals(resp.statusCode(), 200);

                    async.complete();
                });
                request.end();
            }
        });
        logger.info("running on port: " + port);

        async.awaitSuccess(1000);
    }
}
