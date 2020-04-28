/*
 * Copyright 2020 The CloudEvents Authors
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
import io.cloudevents.message.*;

import java.net.URI;
import java.time.ZonedDateTime;
import java.util.HashMap;
import java.util.Map;

public class MockBinaryMessage implements Message, BinaryMessageVisitorFactory<MockBinaryMessage, MockBinaryMessage>, BinaryMessageVisitor<MockBinaryMessage> {

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
    public Encoding getEncoding() {
        return Encoding.BINARY;
    }

    @Override
    public <T extends BinaryMessageVisitor<V>, V> V visit(BinaryMessageVisitorFactory<T, V> visitorFactory) throws MessageVisitException, IllegalStateException {
        if (version == null) {
            throw new IllegalStateException("MockBinaryMessage is empty");
        }

        BinaryMessageVisitor<V> visitor = visitorFactory.createBinaryMessageVisitor(version);
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

        visitor.setBody(this.data);

        return visitor.end();
    }

    @Override
    public <T> T visit(StructuredMessageVisitor<T> visitor) throws MessageVisitException, IllegalStateException {
        throw Encoding.WRONG_ENCODING_EXCEPTION;
    }

    @Override
    public void setBody(byte[] value) throws MessageVisitException {
        this.data = value;
    }

    @Override
    public MockBinaryMessage end() {
        return this;
    }

    @Override
    public void setAttribute(String name, String value) throws MessageVisitException {
        this.attributes.put(name, value);
    }

    @Override
    public void setAttribute(String name, URI value) throws MessageVisitException {
        this.attributes.put(name, value);
    }

    @Override
    public void setAttribute(String name, ZonedDateTime value) throws MessageVisitException {
        this.attributes.put(name, value);
    }

    @Override
    public void setExtension(String name, String value) throws MessageVisitException {
        this.extensions.put(name, value);
    }

    @Override
    public void setExtension(String name, Number value) throws MessageVisitException {
        this.extensions.put(name, value);
    }

    @Override
    public void setExtension(String name, Boolean value) throws MessageVisitException {
        this.extensions.put(name, value);
    }

    @Override
    public MockBinaryMessage createBinaryMessageVisitor(SpecVersion version) {
        this.version = version;

        return this;
    }
}
