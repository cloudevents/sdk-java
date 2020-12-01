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

package io.cloudevents.core.mock;

import io.cloudevents.CloudEvent;
import io.cloudevents.CloudEventData;
import io.cloudevents.SpecVersion;
import io.cloudevents.core.CloudEventUtils;
import io.cloudevents.core.data.BytesCloudEventData;
import io.cloudevents.core.message.MessageReader;
import io.cloudevents.core.message.impl.BaseBinaryMessageReader;
import io.cloudevents.rw.*;

import java.net.URI;
import java.time.OffsetDateTime;
import java.util.HashMap;
import java.util.Map;

public class MockBinaryMessageWriter extends BaseBinaryMessageReader implements MessageReader, CloudEventContextReader, CloudEventWriterFactory<MockBinaryMessageWriter, MockBinaryMessageWriter>, CloudEventWriter<MockBinaryMessageWriter> {

    private SpecVersion version;
    private Map<String, Object> context;
    private CloudEventData data;

    public MockBinaryMessageWriter(SpecVersion version, Map<String, Object> context, CloudEventData data) {
        this.version = version;
        this.context = context;
        this.data = data;
    }

    public MockBinaryMessageWriter(SpecVersion version, Map<String, Object> context, byte[] data) {
        this(version, context, BytesCloudEventData.wrap(data));
    }

    public MockBinaryMessageWriter() {
        this.context = new HashMap<>();
    }

    public MockBinaryMessageWriter(CloudEvent event) {
        this();
        CloudEventUtils
            .toReader(event)
            .read(this);
    }

    @Override
    public <T extends CloudEventWriter<V>, V> V read(CloudEventWriterFactory<T, V> writerFactory, CloudEventDataMapper<? extends CloudEventData> mapper) throws CloudEventRWException, IllegalStateException {
        if (version == null) {
            throw new IllegalStateException("MockBinaryMessage is empty");
        }

        CloudEventWriter<V> writer = writerFactory.create(version);
        this.readContext(writer);

        if (this.data != null) {
            return writer.end(mapper.map(this.data));
        }

        return writer.end();
    }

    @Override
    public MockBinaryMessageWriter end(CloudEventData value) throws CloudEventRWException {
        this.data = value;
        return this;
    }

    @Override
    public MockBinaryMessageWriter end() {
        return this;
    }

    @Override
    public MockBinaryMessageWriter create(SpecVersion version) {
        this.version = version;

        return this;
    }

    @Override
    public void readContext(CloudEventContextWriter writer) throws CloudEventRWException {
        for (Map.Entry<String, Object> entry : this.context.entrySet()) {
            if (entry.getValue() instanceof String) {
                writer.withContextAttribute(entry.getKey(), (String) entry.getValue());
            } else if (entry.getValue() instanceof OffsetDateTime) {
                writer.withContextAttribute(entry.getKey(), (OffsetDateTime) entry.getValue());
            } else if (entry.getValue() instanceof URI) {
                writer.withContextAttribute(entry.getKey(), (URI) entry.getValue());
            } else if (entry.getValue() instanceof Number) {
                writer.withContextAttribute(entry.getKey(), (Number) entry.getValue());
            } else if (entry.getValue() instanceof Boolean) {
                writer.withContextAttribute(entry.getKey(), (Boolean) entry.getValue());
            } else {
                // This should never happen because we build that map only through our builders
                throw new IllegalStateException("Illegal value inside context map: " + entry);
            }
        }
    }

    @Override
    public CloudEventContextWriter withContextAttribute(String name, String value) throws CloudEventRWException {
        this.context.put(name, value);
        return this;
    }

    @Override
    public CloudEventContextWriter withContextAttribute(String name, URI value) throws CloudEventRWException {
        this.context.put(name, value);
        return this;
    }

    @Override
    public CloudEventContextWriter withContextAttribute(String name, OffsetDateTime value) throws CloudEventRWException {
        this.context.put(name, value);
        return this;
    }

    @Override
    public CloudEventContextWriter withContextAttribute(String name, Number value) throws CloudEventRWException {
        this.context.put(name, value);
        return this;
    }

    @Override
    public CloudEventContextWriter withContextAttribute(String name, Boolean value) throws CloudEventRWException {
        this.context.put(name, value);
        return this;
    }
}
