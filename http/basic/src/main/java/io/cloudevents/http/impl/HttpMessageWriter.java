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
 */

package io.cloudevents.http.impl;

import io.cloudevents.CloudEventData;
import io.cloudevents.SpecVersion;
import io.cloudevents.core.format.EventFormat;
import io.cloudevents.core.message.MessageWriter;
import io.cloudevents.rw.CloudEventContextWriter;
import io.cloudevents.rw.CloudEventRWException;
import io.cloudevents.rw.CloudEventWriter;

import java.util.function.BiConsumer;
import java.util.function.Consumer;

import static io.cloudevents.http.impl.CloudEventsHeaders.CONTENT_TYPE;

public class HttpMessageWriter implements CloudEventWriter<Void>, MessageWriter<HttpMessageWriter, Void> {

    private final BiConsumer<String, String> putHeader;
    private final Consumer<byte[]> putBody;

    public HttpMessageWriter(BiConsumer<String, String> putHeader, Consumer<byte[]> putBody) {
        this.putHeader = putHeader;
        this.putBody = putBody;
    }

    @Override
    public Void setEvent(EventFormat format, byte[] value) throws CloudEventRWException {
        putHeader.accept(CONTENT_TYPE, format.serializedContentType());
        putBody.accept(value);
        return null;
    }

    @Override
    public Void end(CloudEventData value) throws CloudEventRWException {
        putBody.accept(value.toBytes());
        return null;
    }

    @Override
    public Void end() {
        putBody.accept(null);
        return null;
    }

    @Override
    public CloudEventContextWriter withContextAttribute(String name, String value) throws CloudEventRWException {
        String headerName = CloudEventsHeaders.ATTRIBUTES_TO_HEADERS.get(name);
        if (headerName == null) {
            headerName = "ce-" + name;
        }
        putHeader.accept(headerName, value);
        return this;
    }

    @Override
    public HttpMessageWriter create(SpecVersion version) {
        putHeader.accept(CloudEventsHeaders.SPEC_VERSION, version.toString());
        return this;
    }
}
