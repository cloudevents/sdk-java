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

import io.cloudevents.CloudEventVisitException;
import io.cloudevents.CloudEventVisitor;
import io.cloudevents.SpecVersion;
import io.cloudevents.format.EventFormat;
import io.cloudevents.message.MessageVisitor;

import javax.ws.rs.core.HttpHeaders;
import javax.ws.rs.core.MultivaluedMap;
import java.io.IOException;
import java.io.OutputStream;

public final class RestfulWSMessageVisitor implements CloudEventVisitor<Void>, MessageVisitor<RestfulWSMessageVisitor, Void> {

    private final MultivaluedMap<String, Object> httpHeaders;
    private final OutputStream entityStream;

    public RestfulWSMessageVisitor(MultivaluedMap<String, Object> httpHeaders, OutputStream entityStream) {
        this.httpHeaders = httpHeaders;
        this.entityStream = entityStream;

        // http headers could contain a content type, so let's remove it
        this.httpHeaders.remove(HttpHeaders.CONTENT_TYPE);
    }

    @Override
    public RestfulWSMessageVisitor create(SpecVersion version) {
        this.httpHeaders.add(CloudEventsHeaders.SPEC_VERSION, version.toString());
        return this;
    }

    @Override
    public void setAttribute(String name, String value) throws CloudEventVisitException {
        this.httpHeaders.add(CloudEventsHeaders.ATTRIBUTES_TO_HEADERS.get(name), value);
    }

    @Override
    public void setExtension(String name, String value) throws CloudEventVisitException {
        this.httpHeaders.add(CloudEventsHeaders.CE_PREFIX + name, value);
    }

    @Override
    public void setBody(byte[] value) throws CloudEventVisitException {
        try {
            this.entityStream.write(value);
        } catch (IOException e) {
            throw CloudEventVisitException.newOther(e);
        }
    }

    @Override
    public Void end() {
        try {
            this.entityStream.flush();
        } catch (IOException e) {
            throw CloudEventVisitException.newOther(e);
        }
        return null;
    }

    @Override
    public Void setEvent(EventFormat format, byte[] value) throws CloudEventVisitException {
        this.httpHeaders.add(HttpHeaders.CONTENT_TYPE, format.serializedContentType());
        this.setBody(value);
        return null;
    }
}
