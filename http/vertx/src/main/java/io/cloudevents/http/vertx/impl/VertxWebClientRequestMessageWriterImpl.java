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

import io.cloudevents.CloudEventData;
import io.cloudevents.SpecVersion;
import io.cloudevents.core.format.EventFormat;
import io.cloudevents.core.message.MessageWriter;
import io.cloudevents.rw.CloudEventRWException;
import io.cloudevents.rw.CloudEventWriter;
import io.vertx.core.Future;
import io.vertx.core.buffer.Buffer;
import io.vertx.core.http.HttpHeaders;
import io.vertx.ext.web.client.HttpRequest;
import io.vertx.ext.web.client.HttpResponse;

public class VertxWebClientRequestMessageWriterImpl implements MessageWriter<CloudEventWriter<Future<HttpResponse<Buffer>>>, Future<HttpResponse<Buffer>>>, CloudEventWriter<Future<HttpResponse<Buffer>>> {

    private final HttpRequest<Buffer> request;

    public VertxWebClientRequestMessageWriterImpl(HttpRequest<Buffer> request) {
        this.request = request;
    }

    // Binary visitor factory

    @Override
    public CloudEventWriter<Future<HttpResponse<Buffer>>> create(SpecVersion version) {
        this.request.headers().add(CloudEventsHeaders.SPEC_VERSION, version.toString());
        return this;
    }

    // Binary visitor

    @Override
    public VertxWebClientRequestMessageWriterImpl withContextAttribute(String name, String value) throws CloudEventRWException {
        CharSequence headerName = CloudEventsHeaders.ATTRIBUTES_TO_HEADERS.get(name);
        if (headerName == null) {
            headerName = "ce-" + name;
        }
        this.request.headers().add(headerName, value);
        return this;
    }

    @Override
    public Future<HttpResponse<Buffer>> end(CloudEventData value) throws CloudEventRWException {
        return this.request.sendBuffer(Buffer.buffer(value.toBytes()));
    }

    @Override
    public Future<HttpResponse<Buffer>> end() {
        return this.request.send();
    }

    // Structured visitor

    @Override
    public Future<HttpResponse<Buffer>> setEvent(EventFormat format, byte[] value) throws CloudEventRWException {
        this.request.headers().add(HttpHeaders.CONTENT_TYPE, format.serializedContentType());
        return this.request.sendBuffer(Buffer.buffer(value));
    }
}
