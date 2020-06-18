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
import io.cloudevents.core.message.MessageWriter;
import io.cloudevents.rw.CloudEventRWException;
import io.cloudevents.rw.CloudEventWriter;
import io.vertx.core.buffer.Buffer;
import io.vertx.core.http.HttpHeaders;
import io.vertx.core.http.HttpServerResponse;

public class VertxHttpServerResponseMessageWriterImpl implements MessageWriter<CloudEventWriter<HttpServerResponse>, HttpServerResponse>, CloudEventWriter<HttpServerResponse> {

    private final HttpServerResponse response;

    public VertxHttpServerResponseMessageWriterImpl(HttpServerResponse response) {
        this.response = response;
    }

    // Binary visitor factory

    @Override
    public CloudEventWriter<HttpServerResponse> create(SpecVersion version) {
        this.response.putHeader(CloudEventsHeaders.SPEC_VERSION, version.toString());
        return this;
    }

    // Binary visitor

    @Override
    public void setAttribute(String name, String value) throws CloudEventRWException {
        this.response.putHeader(CloudEventsHeaders.ATTRIBUTES_TO_HEADERS.get(name), value);
    }

    @Override
    public void setExtension(String name, String value) throws CloudEventRWException {
        this.response.putHeader("ce-" + name, value);
    }

    @Override
    public HttpServerResponse end(byte[] value) throws CloudEventRWException {
        if (this.response.ended()) {
            throw CloudEventRWException.newOther(new IllegalStateException("Cannot set the body because the response is already ended"));
        }
        this.response.end(Buffer.buffer(value));
        return this.response;
    }

    @Override
    public HttpServerResponse end() {
        if (!this.response.ended()) {
            this.response.end();
        }
        return this.response;
    }

    // Structured visitor

    @Override
    public HttpServerResponse setEvent(EventFormat format, byte[] value) throws CloudEventRWException {
        this.response.putHeader(HttpHeaders.CONTENT_TYPE, format.serializedContentType());
        this.response.end(Buffer.buffer(value));
        return this.response;
    }
}
