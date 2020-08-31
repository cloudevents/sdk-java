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
import io.cloudevents.SpecVersion;
import io.cloudevents.core.impl.CloudEventUtils;
import io.cloudevents.core.message.MessageReader;
import io.cloudevents.core.message.impl.BaseBinaryMessageReader;
import io.cloudevents.rw.*;

import java.net.URI;
import java.time.OffsetDateTime;
import java.util.HashMap;
import java.util.Map;

public class MockBinaryMessageWriter extends BaseBinaryMessageReader implements MessageReader, CloudEventWriterFactory<MockBinaryMessageWriter, MockBinaryMessageWriter>, CloudEventWriter<MockBinaryMessageWriter> {

    private SpecVersion version;
    private Map<String, Object> attributes;
    private byte[] data;
    private Map<String, Object> extensions;

    public MockBinaryMessageWriter(SpecVersion version, Map<String, Object> attributes, byte[] data, Map<String, Object> extensions) {
        this.version = version;
        this.attributes = attributes;
        this.data = data;
        this.extensions = extensions;
    }

    public MockBinaryMessageWriter() {
        this.attributes = new HashMap<>();
        this.extensions = new HashMap<>();
    }

    public MockBinaryMessageWriter(CloudEvent event) {
        this();
        CloudEventUtils
            .toVisitable(event)
            .read(this);
    }

    @Override
    public <T extends CloudEventWriter<V>, V> V read(CloudEventWriterFactory<T, V> writerFactory) throws CloudEventRWException, IllegalStateException {
        if (version == null) {
            throw new IllegalStateException("MockBinaryMessage is empty");
        }

        CloudEventWriter<V> visitor = writerFactory.create(version);
        this.readAttributes(visitor);
        this.readExtensions(visitor);

        if (this.data != null && this.data.length != 0) {
            return visitor.end(this.data);
        }

        return visitor.end();
    }

    @Override
    public void readAttributes(CloudEventAttributesWriter writer) throws CloudEventRWException, IllegalStateException {
        for (Map.Entry<String, Object> e : this.attributes.entrySet()) {
            if (e.getValue() instanceof String) {
                writer.setAttribute(e.getKey(), (String) e.getValue());
            } else if (e.getValue() instanceof OffsetDateTime) {
                writer.setAttribute(e.getKey(), (OffsetDateTime) e.getValue());
            } else if (e.getValue() instanceof URI) {
                writer.setAttribute(e.getKey(), (URI) e.getValue());
            } else {
                // This should never happen because we build that map only through our builders
                throw new IllegalStateException("Illegal value inside attributes map: " + e);
            }
        }
    }

    @Override
    public void readExtensions(CloudEventExtensionsWriter visitor) throws CloudEventRWException, IllegalStateException {
        for (Map.Entry<String, Object> entry : this.extensions.entrySet()) {
            if (entry.getValue() instanceof String) {
                visitor.setExtension(entry.getKey(), (String) entry.getValue());
            } else if (entry.getValue() instanceof Number) {
                visitor.setExtension(entry.getKey(), (Number) entry.getValue());
            } else if (entry.getValue() instanceof Boolean) {
                visitor.setExtension(entry.getKey(), (Boolean) entry.getValue());
            } else {
                // This should never happen because we build that map only through our builders
                throw new IllegalStateException("Illegal value inside extensions map: " + entry);
            }
        }
    }

    @Override
    public MockBinaryMessageWriter end(byte[] value) throws CloudEventRWException {
        this.data = value;
        return this;
    }

    @Override
    public MockBinaryMessageWriter end() {
        return this;
    }

    @Override
    public void setAttribute(String name, String value) throws CloudEventRWException {
        this.attributes.put(name, value);
    }

    @Override
    public void setAttribute(String name, URI value) throws CloudEventRWException {
        this.attributes.put(name, value);
    }

    @Override
    public void setAttribute(String name, OffsetDateTime value) throws CloudEventRWException {
        this.attributes.put(name, value);
    }

    @Override
    public void setExtension(String name, String value) throws CloudEventRWException {
        this.extensions.put(name, value);
    }

    @Override
    public void setExtension(String name, Number value) throws CloudEventRWException {
        this.extensions.put(name, value);
    }

    @Override
    public void setExtension(String name, Boolean value) throws CloudEventRWException {
        this.extensions.put(name, value);
    }

    @Override
    public MockBinaryMessageWriter create(SpecVersion version) {
        this.version = version;

        return this;
    }
}
