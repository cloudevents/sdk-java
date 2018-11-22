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
import io.vertx.core.AsyncResult;
import io.vertx.core.Future;
import io.vertx.core.Handler;
import io.vertx.core.MultiMap;
import io.vertx.core.buffer.Buffer;
import io.vertx.core.http.HttpClientRequest;
import io.vertx.core.http.HttpHeaders;
import io.vertx.core.http.HttpServerRequest;

import java.net.URI;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Map;
import java.util.stream.Collectors;

import static io.cloudevents.CloudEvent.CLOUD_EVENTS_VERSION_KEY;
import static io.cloudevents.CloudEvent.EVENT_ID_KEY;
import static io.cloudevents.CloudEvent.EVENT_TIME_KEY;
import static io.cloudevents.CloudEvent.EVENT_TYPE_KEY;
import static io.cloudevents.CloudEvent.EVENT_TYPE_VERSION_KEY;
import static io.cloudevents.CloudEvent.HEADER_PREFIX;
import static io.cloudevents.CloudEvent.SCHEMA_URL_KEY;
import static io.cloudevents.CloudEvent.SOURCE_KEY;

public final class CeVertx {

    private CeVertx() {
        // no-op
    }

    public static void writeToHttpClientRequest(final CloudEvent<?> ce, final HttpClientRequest request) {

        // setting the right content-length:
        if (ce.getData().isPresent()) {
            request.putHeader(HttpHeaders.CONTENT_LENGTH, HttpHeaders.createOptimized(String.valueOf(ce.getData().get().toString().length())));
        } else {
            request.putHeader(HttpHeaders.CONTENT_LENGTH, HttpHeaders.createOptimized("0"));
        }

        // read required headers
        request
                .putHeader(HttpHeaders.CONTENT_TYPE, HttpHeaders.createOptimized("application/json"))
                .putHeader(HttpHeaders.createOptimized(CLOUD_EVENTS_VERSION_KEY), HttpHeaders.createOptimized(ce.getCloudEventsVersion()))
                .putHeader(HttpHeaders.createOptimized(EVENT_TYPE_KEY), HttpHeaders.createOptimized(ce.getEventType()))
                .putHeader(HttpHeaders.createOptimized(SOURCE_KEY), HttpHeaders.createOptimized(ce.getSource().toString()))
                .putHeader(HttpHeaders.createOptimized(EVENT_ID_KEY), HttpHeaders.createOptimized(ce.getEventID()));

        // read optional headers
        ce.getEventTypeVersion().ifPresent(eventTypeVersion -> {
            request.putHeader(HttpHeaders.createOptimized(EVENT_TYPE_VERSION_KEY), HttpHeaders.createOptimized(eventTypeVersion));
        });

        ce.getEventTime().ifPresent(eventTime -> {
            request.putHeader(HttpHeaders.createOptimized(EVENT_TIME_KEY), HttpHeaders.createOptimized(eventTime.format(DateTimeFormatter.ISO_OFFSET_DATE_TIME)));
        });

        ce.getSchemaURL().ifPresent(schemaUrl -> {
            request.putHeader(HttpHeaders.createOptimized(SCHEMA_URL_KEY), HttpHeaders.createOptimized(schemaUrl.toString()));
        });

        ce.getData().ifPresent(data -> {
            request.write(data.toString());
        });
    }

    public static void readFromRequest(final HttpServerRequest request, final Handler<AsyncResult<CloudEvent>> resultHandler) {

        final MultiMap headers = request.headers();
        final CloudEventBuilder builder = new CloudEventBuilder();

        try {
            // just check, no need to set the version
            readRequiredHeaderValue(headers, CLOUD_EVENTS_VERSION_KEY);

            builder
                    // set required values
                    .eventType(readRequiredHeaderValue(headers, EVENT_TYPE_KEY))
                    .source(URI.create(readRequiredHeaderValue(headers ,SOURCE_KEY)))
                    .eventID(readRequiredHeaderValue(headers, EVENT_ID_KEY))

                    // set optional values
                    .eventTypeVersion(headers.get(EVENT_TYPE_VERSION_KEY))
                    .contentType(headers.get(HttpHeaders.CONTENT_TYPE));

            final String eventTime = headers.get(EVENT_TIME_KEY);
            if (eventTime != null) {
                builder.eventTime(ZonedDateTime.parse(eventTime, DateTimeFormatter.ISO_OFFSET_DATE_TIME));
            }

            final String schemaURL = headers.get(SCHEMA_URL_KEY);
            if (schemaURL != null) {
                builder.schemaURL(URI.create(schemaURL));
            }

            // get the extensions
            final Map<String, String> extensions =
                    headers.entries().stream()
                            .filter(header -> header.getKey().startsWith(HEADER_PREFIX))
                            .collect(Collectors.toMap(h -> h.getKey(), h -> h.getValue()));

            builder.extensions(extensions);
            request.bodyHandler((Buffer buff) -> {

                if (buff.length()>0) {
                    builder.data(buff.toJsonObject().toString());
                }
                resultHandler.handle(Future.succeededFuture(builder.build()));
            });
        } catch (Exception e) {
            resultHandler.handle(Future.failedFuture(e));
        }
    }

    private static String readRequiredHeaderValue(final MultiMap headers, final String headerName) {
        return requireNonNull(headers.get(headerName));
    }

    private static String requireNonNull(final String val) {
        if (val == null) {
            throw new IllegalArgumentException();
        } else {
            return val;
        }
    }
}
