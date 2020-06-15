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

package io.cloudevents.http.vertx.impl;

import io.cloudevents.SpecVersion;
import io.cloudevents.core.format.EventFormat;
import io.cloudevents.http.vertx.VertxHttpClientRequestMessageWriter;
import io.cloudevents.rw.CloudEventRWException;
import io.vertx.core.buffer.Buffer;
import io.vertx.core.http.HttpClientRequest;
import io.vertx.core.http.HttpHeaders;

public class VertxHttpClientRequestMessageWriterImpl implements VertxHttpClientRequestMessageWriter {

    private final HttpClientRequest request;

    public VertxHttpClientRequestMessageWriterImpl(HttpClientRequest request) {
        this.request = request;
    }

    // Binary visitor factory

    @Override
    public VertxHttpClientRequestMessageWriter create(SpecVersion version) {
        this.request.putHeader(CloudEventsHeaders.SPEC_VERSION, version.toString());
        return this;
    }

    // Binary visitor

    @Override
    public void setAttribute(String name, String value) throws CloudEventRWException {
        this.request.putHeader(CloudEventsHeaders.ATTRIBUTES_TO_HEADERS.get(name), value);
    }

    @Override
    public void setExtension(String name, String value) throws CloudEventRWException {
        this.request.putHeader("ce-" + name, value);
    }

    @Override
    public HttpClientRequest end(byte[] value) throws CloudEventRWException {
        this.request.end(Buffer.buffer(value));
        return this.request;
    }

    @Override
    public HttpClientRequest end() {
        this.request.end();
        return this.request;
    }

    // Structured visitor

    @Override
    public HttpClientRequest setEvent(EventFormat format, byte[] value) throws CloudEventRWException {
        this.request.putHeader(HttpHeaders.CONTENT_TYPE, format.serializedContentType());
        this.request.end(Buffer.buffer(value));
        return this.request;
    }
}
