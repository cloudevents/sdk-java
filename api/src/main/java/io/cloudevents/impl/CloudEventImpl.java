package io.cloudevents.impl;

import io.cloudevents.Attributes;
import io.cloudevents.CloudEvent;
import io.cloudevents.format.EventFormat;
import io.cloudevents.message.*;

import java.util.*;

public final class CloudEventImpl implements CloudEvent, BinaryMessage {

    private final AttributesInternal attributes;
    private final byte[] data;
    private final Map<String, Object> extensions;

    protected CloudEventImpl(Attributes attributes, byte[] data, Map<String, Object> extensions) {
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
    public Optional<byte[]> getData() {
        return Optional.ofNullable(this.data);
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

    // Message impl

    public BinaryMessage asBinaryMessage() {
        return this;
    }

    public StructuredMessage asStructuredMessage(EventFormat format) {
        CloudEvent ev = this;
        // TODO This sucks, will improve later
        return new StructuredMessage() {
            @Override
            public <T> T visit(StructuredMessageVisitor<T> visitor) throws MessageVisitException, IllegalStateException {
                return visitor.setEvent(format, format.serializeToBytes(ev));
            }
        };
    }

    @Override
    public <T extends BinaryMessageVisitor<V>, V> V visit(BinaryMessageVisitorFactory<T, V> visitorFactory) throws MessageVisitException, IllegalStateException {
        BinaryMessageVisitor<V> visitor = visitorFactory.createBinaryMessageVisitor(this.attributes.getSpecVersion());
        this.attributes.visit(visitor);

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

        if (this.data != null) {
            visitor.setBody(this.data);
        }

        return visitor.end();
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
}
