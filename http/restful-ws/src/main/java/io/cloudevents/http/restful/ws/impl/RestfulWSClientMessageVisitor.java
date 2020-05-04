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
import io.cloudevents.format.EventFormat;
import io.cloudevents.message.BinaryMessageVisitor;
import io.cloudevents.message.MessageVisitException;
import io.cloudevents.message.MessageVisitor;

import javax.ws.rs.client.ClientRequestContext;
import javax.ws.rs.core.HttpHeaders;
import javax.ws.rs.core.MediaType;

public final class RestfulWSClientMessageVisitor implements BinaryMessageVisitor<Void>, MessageVisitor<RestfulWSClientMessageVisitor, Void> {

    private final ClientRequestContext context;

    public RestfulWSClientMessageVisitor(ClientRequestContext context) {
        this.context = context;

        // http headers could contain a content type, so let's remove it
        this.context.getHeaders().remove(HttpHeaders.CONTENT_TYPE);
        this.context.setEntity(null);
    }

    @Override
    public RestfulWSClientMessageVisitor createBinaryMessageVisitor(SpecVersion version) {
        this.context.getHeaders().add(CloudEventsHeaders.SPEC_VERSION, version.toString());
        return this;
    }

    @Override
    public void setAttribute(String name, String value) throws MessageVisitException {
        this.context.getHeaders().add(CloudEventsHeaders.ATTRIBUTES_TO_HEADERS.get(name), value);
    }

    @Override
    public void setExtension(String name, String value) throws MessageVisitException {
        this.context.getHeaders().add(CloudEventsHeaders.CE_PREFIX + name, value);
    }

    @Override
    public void setBody(byte[] value) throws MessageVisitException {
        this.context.setEntity(value);
    }

    @Override
    public Void end() {
        return null;
    }

    @Override
    public Void setEvent(EventFormat format, byte[] value) throws MessageVisitException {
        this.context.setEntity(value, null, MediaType.valueOf(format.serializedContentType()));
        return null;
    }
}
