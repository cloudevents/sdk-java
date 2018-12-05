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
package io.cloudevents.http.vertx.impl;

import io.cloudevents.CloudEvent;
import io.cloudevents.CloudEventBuilder;
import io.cloudevents.http.vertx.VertxCloudEvents;
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

import static io.cloudevents.CloudEvent.SPECVERSION_KEY;
import static io.cloudevents.CloudEvent.EVENT_ID_KEY;
import static io.cloudevents.CloudEvent.EVENT_TIME_KEY;
import static io.cloudevents.CloudEvent.EVENT_TYPE_KEY;
import static io.cloudevents.CloudEvent.SCHEMA_URL_KEY;
import static io.cloudevents.CloudEvent.SOURCE_KEY;

public final class VertxCloudEventsImpl implements VertxCloudEvents {

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

    @Override
    public <T> void readFromRequest(HttpServerRequest request, Handler<AsyncResult<CloudEvent<T>>> resultHandler) {

        final MultiMap headers = request.headers();
        final CloudEventBuilder builder = new CloudEventBuilder();

        try {
            // just check, no need to set the version
            readRequiredHeaderValue(headers, SPECVERSION_KEY);

            builder
                    // set required values
                    .type(readRequiredHeaderValue(headers, EVENT_TYPE_KEY))
                    .source(URI.create(readRequiredHeaderValue(headers ,SOURCE_KEY)))
                    .id(readRequiredHeaderValue(headers, EVENT_ID_KEY))

                    // set optional values
                    .contentType(headers.get(HttpHeaders.CONTENT_TYPE));

            final String eventTime = headers.get(EVENT_TIME_KEY);
            if (eventTime != null) {
                builder.time(ZonedDateTime.parse(eventTime, DateTimeFormatter.ISO_OFFSET_DATE_TIME));
            }

            final String schemaURL = headers.get(SCHEMA_URL_KEY);
            if (schemaURL != null) {
                builder.schemaURL(URI.create(schemaURL));
            }

            request.bodyHandler((Buffer buff) -> {

                if (buff.length()>0) {
                    builder.data(buff.toString());
                }
                resultHandler.handle(Future.succeededFuture(builder.build()));
            });
        } catch (Exception e) {
            resultHandler.handle(Future.failedFuture(e));
        }
    }

    @Override
    public <T> void writeToHttpClientRequest(CloudEvent<T> ce, HttpClientRequest request) {

        // setting the right content-length:
        if (ce.getData().isPresent()) {
            request.putHeader(HttpHeaders.CONTENT_LENGTH, HttpHeaders.createOptimized(String.valueOf(ce.getData().get().toString().length())));
        } else {
            request.putHeader(HttpHeaders.CONTENT_LENGTH, HttpHeaders.createOptimized("0"));
        }

        // read required headers
        request
                .putHeader(HttpHeaders.CONTENT_TYPE, HttpHeaders.createOptimized("application/json"))
                .putHeader(HttpHeaders.createOptimized(SPECVERSION_KEY), HttpHeaders.createOptimized(ce.getSepcVersion()))
                .putHeader(HttpHeaders.createOptimized(EVENT_TYPE_KEY), HttpHeaders.createOptimized(ce.getType()))
                .putHeader(HttpHeaders.createOptimized(SOURCE_KEY), HttpHeaders.createOptimized(ce.getSource().toString()))
                .putHeader(HttpHeaders.createOptimized(EVENT_ID_KEY), HttpHeaders.createOptimized(ce.getId()));

        // read optional headers
        ce.getTime().ifPresent(eventTime -> {
            request.putHeader(HttpHeaders.createOptimized(EVENT_TIME_KEY), HttpHeaders.createOptimized(eventTime.format(DateTimeFormatter.ISO_OFFSET_DATE_TIME)));
        });

        ce.getSchemaURL().ifPresent(schemaUrl -> {
            request.putHeader(HttpHeaders.createOptimized(SCHEMA_URL_KEY), HttpHeaders.createOptimized(schemaUrl.toString()));
        });

        ce.getData().ifPresent(data -> {
            request.write(data.toString());
        });
    }
}
