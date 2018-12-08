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
package io.cloudevents.http.vertx.impl;

import io.cloudevents.CloudEvent;
import io.cloudevents.CloudEventBuilder;
import io.cloudevents.Extension;
import io.cloudevents.SpecVersion;
import io.cloudevents.extensions.DistributedTracingExtension;
import io.cloudevents.http.HttpTransportAttributes;
import io.cloudevents.http.V02HttpTransportMappers;
import io.cloudevents.http.vertx.VertxCloudEvents;
import io.vertx.core.AsyncResult;
import io.vertx.core.Future;
import io.vertx.core.Handler;
import io.vertx.core.MultiMap;
import io.vertx.core.buffer.Buffer;
import io.vertx.core.http.HttpClientRequest;
import io.vertx.core.http.HttpHeaders;
import io.vertx.core.http.HttpServerRequest;
import io.vertx.core.json.Json;
import io.vertx.core.json.JsonObject;

import java.io.Serializable;
import java.lang.reflect.Field;
import java.net.URI;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Map;

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
        this.readFromRequest(request, null, resultHandler);

    }

    @Override
    public <T> void readFromRequest(HttpServerRequest request, Class[] extensions, Handler<AsyncResult<CloudEvent<T>>> resultHandler) {

        final MultiMap headers = request.headers();
        final CloudEventBuilder builder = new CloudEventBuilder();


        final HttpTransportAttributes httpTransportKeys;
        {
            if (headers.contains(V02HttpTransportMappers.SPEC_VERSION_KEY)) {
                httpTransportKeys = HttpTransportAttributes.getHttpAttributesForSpec(SpecVersion.V_02);
            } else {
                httpTransportKeys = HttpTransportAttributes.getHttpAttributesForSpec(SpecVersion.V_01);
            }
        }

        try {
            builder
                    // set required values
                    .specVersion(readRequiredHeaderValue(headers, httpTransportKeys.specVersionKey()))
                    .type(readRequiredHeaderValue(headers, httpTransportKeys.typeKey()))
                    .source(URI.create(readRequiredHeaderValue(headers ,httpTransportKeys.sourceKey())))
                    .id(readRequiredHeaderValue(headers, httpTransportKeys.idKey()))

                    // set optional values
                    .contentType(headers.get(HttpHeaders.CONTENT_TYPE));

            final String eventTime = headers.get(httpTransportKeys.timeKey());
            if (eventTime != null) {
                builder.time(ZonedDateTime.parse(eventTime, DateTimeFormatter.ISO_OFFSET_DATE_TIME));
            }

            final String schemaURL = headers.get(httpTransportKeys.schemaUrlKey());
            if (schemaURL != null) {
                builder.schemaURL(URI.create(schemaURL));
            }


            if (extensions != null && extensions.length > 0) {

                // move this out
                Arrays.asList(extensions).forEach(ext -> {

                    try {
                        Object extObj  = ext.newInstance();
                        final JsonObject extension = new JsonObject();
                        Field[] fields = ext.getDeclaredFields();

                        for (Field field : fields) {
                            boolean accessible = field.isAccessible();
                            field.setAccessible(true);
                            field.set(extObj, request.headers().get(field.getName()));
                            field.setAccessible(accessible);
                        }
                        builder.extension((Extension) extObj);
                    } catch (InstantiationException e) {
                        e.printStackTrace();
                    } catch (IllegalAccessException e) {
                        e.printStackTrace();
                    }
                });
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

        HttpTransportAttributes httpTransportAttributes = HttpTransportAttributes.getHttpAttributesForSpec(SpecVersion.fromVersion(ce.getSepcVersion()));

        // read required headers
        request
                .putHeader(HttpHeaders.CONTENT_TYPE, HttpHeaders.createOptimized("application/json"))
                .putHeader(HttpHeaders.createOptimized(httpTransportAttributes.specVersionKey()), HttpHeaders.createOptimized(ce.getSepcVersion()))
                .putHeader(HttpHeaders.createOptimized(httpTransportAttributes.typeKey()), HttpHeaders.createOptimized(ce.getType()))
                .putHeader(HttpHeaders.createOptimized(httpTransportAttributes.sourceKey()), HttpHeaders.createOptimized(ce.getSource().toString()))
                .putHeader(HttpHeaders.createOptimized(httpTransportAttributes.idKey()), HttpHeaders.createOptimized(ce.getId()));

        // read optional headers
        ce.getTime().ifPresent(eventTime -> {
            request.putHeader(HttpHeaders.createOptimized(httpTransportAttributes.timeKey()), HttpHeaders.createOptimized(eventTime.format(DateTimeFormatter.ISO_OFFSET_DATE_TIME)));
        });

        ce.getSchemaURL().ifPresent(schemaUrl -> {
            request.putHeader(HttpHeaders.createOptimized(httpTransportAttributes.schemaUrlKey()), HttpHeaders.createOptimized(schemaUrl.toString()));
        });


        ce.getExtensions().ifPresent(extensions -> {

            extensions.forEach(ext -> {
                JsonObject.mapFrom(ext).forEach(extEntry -> {
                    request.putHeader(HttpHeaders.createOptimized(extEntry.getKey()), HttpHeaders.createOptimized(extEntry.getValue().toString()));
                });
            });
        });


        ce.getData().ifPresent(data -> {
            request.write(data.toString());
        });


    }
}
