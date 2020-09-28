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

package io.cloudevents.http.restful.ws.impl;

import io.cloudevents.SpecVersion;
import io.cloudevents.core.format.EventFormat;
import io.cloudevents.core.message.MessageWriter;
import io.cloudevents.rw.CloudEventRWException;
import io.cloudevents.rw.CloudEventWriter;

import javax.ws.rs.client.ClientRequestContext;
import javax.ws.rs.core.HttpHeaders;
import javax.ws.rs.core.MediaType;

public final class RestfulWSClientMessageWriter implements CloudEventWriter<Void>, MessageWriter<RestfulWSClientMessageWriter, Void> {

    private final ClientRequestContext context;

    public RestfulWSClientMessageWriter(ClientRequestContext context) {
        this.context = context;

        // http headers could contain a content type, so let's remove it
        this.context.getHeaders().remove(HttpHeaders.CONTENT_TYPE);
        this.context.setEntity(null);
    }

    @Override
    public RestfulWSClientMessageWriter create(SpecVersion version) {
        this.context.getHeaders().add(CloudEventsHeaders.SPEC_VERSION, version.toString());
        return this;
    }

    @Override
    public RestfulWSClientMessageWriter withAttribute(String name, String value) throws CloudEventRWException {
        this.context.getHeaders().add(CloudEventsHeaders.ATTRIBUTES_TO_HEADERS.get(name), value);
        return this;
    }

    @Override
    public RestfulWSClientMessageWriter withExtension(String name, String value) throws CloudEventRWException {
        this.context.getHeaders().add(CloudEventsHeaders.CE_PREFIX + name, value);
        return this;
    }

    @Override
    public Void end(byte[] value) throws CloudEventRWException {
        this.context.setEntity(value);
        return null;
    }

    @Override
    public Void end() {
        return null;
    }

    @Override
    public Void setEvent(EventFormat format, byte[] value) throws CloudEventRWException {
        this.context.setEntity(value, null, MediaType.valueOf(format.serializedContentType()));
        return null;
    }
}
