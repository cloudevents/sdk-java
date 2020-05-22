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

package io.cloudevents.impl;

import io.cloudevents.*;

import java.nio.charset.StandardCharsets;
import java.util.*;

public final class CloudEventImpl implements CloudEvent {

    private final AttributesInternal attributes;
    private final byte[] data;
    private final Map<String, Object> extensions;

    public CloudEventImpl(Attributes attributes, byte[] data, Map<String, Object> extensions) {
        Objects.requireNonNull(attributes);
        this.attributes = (AttributesInternal) attributes;
        this.data = data;
        this.extensions = extensions != null ? extensions : new HashMap<>();
    }

    @Override
    public Attributes getAttributes() {
        return attributes;
    }

    @Override
    public byte[] getData() {
        return this.data;
    }

    @Override
    public Map<String, Object> getExtensions() {
        return Collections.unmodifiableMap(extensions);
    }

    @Override
    public CloudEvent toV03() {
        return new CloudEventImpl(
            attributes.toV03(),
            data,
            extensions
        );
    }

    @Override
    public CloudEvent toV1() {
        return new CloudEventImpl(
            attributes.toV1(),
            data,
            extensions
        );
    }

    @Override
    public <T extends CloudEventVisitor<V>, V> V visit(CloudEventVisitorFactory<T, V> visitorFactory) throws CloudEventVisitException, IllegalStateException {
        CloudEventVisitor<V> visitor = visitorFactory.create(this.attributes.getSpecVersion());
        this.attributes.visitAttributes(visitor);
        this.visitExtensions(visitor);

        if (this.data != null) {
            visitor.setBody(this.data);
        }

        return visitor.end();
    }

    @Override
    public void visitAttributes(CloudEventAttributesVisitor visitor) throws CloudEventVisitException {
        this.attributes.visitAttributes(visitor);
    }

    @Override
    public void visitExtensions(CloudEventExtensionsVisitor visitor) throws CloudEventVisitException {
        // TODO to be improved
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
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        CloudEventImpl that = (CloudEventImpl) o;
        return Objects.equals(attributes, that.attributes) &&
            Arrays.equals(data, that.data) &&
            Objects.equals(extensions, that.extensions);
    }

    @Override
    public int hashCode() {
        return Objects.hash(attributes, data, extensions);
    }

    @Override
    public String toString() {
        return "CloudEvent{" +
            "attributes=" + attributes +
            ((this.data != null) ? ", data=" + new String(this.data, StandardCharsets.UTF_8) : "") +
            ", extensions=" + extensions +
            '}';
    }
}
