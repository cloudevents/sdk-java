package io.cloudevents.impl;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import io.cloudevents.Attributes;
import io.cloudevents.CloudEvent;
import io.cloudevents.DataConversionException;
import io.cloudevents.format.EventFormat;
import io.cloudevents.format.json.CloudEventDeserializer;
import io.cloudevents.format.json.CloudEventSerializer;
import io.cloudevents.format.json.JsonFormat;
import io.cloudevents.message.*;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.*;

@JsonSerialize(using = CloudEventSerializer.class)
@JsonDeserialize(using = CloudEventDeserializer.class)
public final class CloudEventImpl implements CloudEvent, BinaryMessage {

    private final AttributesInternal attributes;
    private final Object data;
    private final Map<String, Object> extensions;

    protected CloudEventImpl(Attributes attributes, Object data, Map<String, Object> extensions) {
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
    public Optional<String> getDataAsString() {
        if (data != null) {
            if (data instanceof String) {
                return Optional.of((String)data);
            }
            if (data instanceof byte[]) {
                return Optional.of(new String((byte[]) this.data, StandardCharsets.UTF_8));
            }
            if (data instanceof JsonNode) {
                JsonNode d = (JsonNode) this.data;
                try {
                    return Optional.of(JsonFormat.MAPPER.writeValueAsString(data));
                } catch (JsonProcessingException e) {
                    throw new DataConversionException("JsonNode", "String", e);
                }
            }
            throw new IllegalStateException("CloudEventImpl contains an illegal data of class " + data.getClass().getCanonicalName());
        }
        return Optional.empty();
    }

    @Override
    public Optional<byte[]> getDataAsBytes() {
        if (data != null) {
            if (data instanceof String) {
                return Optional.of(((String)data).getBytes());
            }
            if (data instanceof byte[]) {
                return Optional.of((byte[])this.data);
            }
            if (data instanceof JsonNode) {
                JsonNode d = (JsonNode) this.data;
                try {
                    return Optional.of(JsonFormat.MAPPER.writeValueAsBytes(data));
                } catch (JsonProcessingException e) {
                    throw new DataConversionException("JsonNode", "byte[]", e);
                }
            }
            throw new IllegalStateException("CloudEventImpl contains an illegal data of class " + data.getClass().getCanonicalName());
        }
        return Optional.empty();
    }

    @Override
    public Optional<JsonNode> getDataAsJson() {
        if (data != null) {
            if (data instanceof String) {
                try {
                    return Optional.of(JsonFormat.MAPPER.readTree((String)data));
                } catch (IOException e) {
                    throw new DataConversionException("String", "JsonNode", e);
                }
            }
            if (data instanceof byte[]) {
                try {
                    return Optional.of(JsonFormat.MAPPER.readTree((byte[]) data));
                } catch (IOException e) {
                    throw new DataConversionException("[]byte", "JsonNode", e);
                }
            }
            if (data instanceof JsonNode) {
                return Optional.of((JsonNode)this.data);
            }
            throw new IllegalStateException("CloudEventImpl contains an illegal data of class " + data.getClass().getCanonicalName());
        }
        return Optional.empty();
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

    protected Object getRawData() {
        return data;
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

        // TODO to be improved to remove the allocation of useless optional
        if (this.data != null) {
            visitor.setBody(this.getDataAsBytes().get());
        }

        return visitor.end();
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        CloudEventImpl that = (CloudEventImpl) o;
        return Objects.equals(attributes, that.attributes) &&
            Objects.equals(data, that.data) &&
            Objects.equals(extensions, that.extensions);
    }

    @Override
    public int hashCode() {
        return Objects.hash(attributes, data, extensions);
    }
}
