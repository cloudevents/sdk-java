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

package io.cloudevents.mock;

import io.cloudevents.SpecVersion;
import io.cloudevents.message.Message;
import io.cloudevents.message.impl.BaseBinaryMessage;
import io.cloudevents.visitor.*;

import java.net.URI;
import java.time.ZonedDateTime;
import java.util.HashMap;
import java.util.Map;

public class MockBinaryMessage extends BaseBinaryMessage implements Message, CloudEventVisitorFactory<MockBinaryMessage, MockBinaryMessage>, CloudEventVisitor<MockBinaryMessage> {

    private SpecVersion version;
    private Map<String, Object> attributes;
    private byte[] data;
    private Map<String, Object> extensions;

    public MockBinaryMessage(SpecVersion version, Map<String, Object> attributes, byte[] data, Map<String, Object> extensions) {
        this.version = version;
        this.attributes = attributes;
        this.data = data;
        this.extensions = extensions;
    }

    public MockBinaryMessage() {
        this.attributes = new HashMap<>();
        this.extensions = new HashMap<>();
    }

    @Override
    public <T extends CloudEventVisitor<V>, V> V visit(CloudEventVisitorFactory<T, V> visitorFactory) throws CloudEventVisitException, IllegalStateException {
        if (version == null) {
            throw new IllegalStateException("MockBinaryMessage is empty");
        }

        CloudEventVisitor<V> visitor = visitorFactory.create(version);
        this.visitAttributes(visitor);
        this.visitExtensions(visitor);

        if (this.data != null && this.data.length != 0) {
            return visitor.end(this.data);
        }

        return visitor.end();
    }

    @Override
    public void visitAttributes(CloudEventAttributesVisitor visitor) throws CloudEventVisitException, IllegalStateException {
        for (Map.Entry<String, Object> e : this.attributes.entrySet()) {
            if (e.getValue() instanceof String) {
                visitor.setAttribute(e.getKey(), (String) e.getValue());
            } else if (e.getValue() instanceof ZonedDateTime) {
                visitor.setAttribute(e.getKey(), (ZonedDateTime) e.getValue());
            } else if (e.getValue() instanceof URI) {
                visitor.setAttribute(e.getKey(), (URI) e.getValue());
            } else {
                // This should never happen because we build that map only through our builders
                throw new IllegalStateException("Illegal value inside attributes map: " + e);
            }
        }
    }

    @Override
    public void visitExtensions(CloudEventExtensionsVisitor visitor) throws CloudEventVisitException, IllegalStateException {
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
    public MockBinaryMessage end(byte[] value) throws CloudEventVisitException {
        this.data = value;
        return this;
    }

    @Override
    public MockBinaryMessage end() {
        return this;
    }

    @Override
    public void setAttribute(String name, String value) throws CloudEventVisitException {
        this.attributes.put(name, value);
    }

    @Override
    public void setAttribute(String name, URI value) throws CloudEventVisitException {
        this.attributes.put(name, value);
    }

    @Override
    public void setAttribute(String name, ZonedDateTime value) throws CloudEventVisitException {
        this.attributes.put(name, value);
    }

    @Override
    public void setExtension(String name, String value) throws CloudEventVisitException {
        this.extensions.put(name, value);
    }

    @Override
    public void setExtension(String name, Number value) throws CloudEventVisitException {
        this.extensions.put(name, value);
    }

    @Override
    public void setExtension(String name, Boolean value) throws CloudEventVisitException {
        this.extensions.put(name, value);
    }

    @Override
    public MockBinaryMessage create(SpecVersion version) {
        this.version = version;

        return this;
    }
}
