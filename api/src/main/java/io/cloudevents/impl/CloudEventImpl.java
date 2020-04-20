package io.cloudevents.impl;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import io.cloudevents.Attributes;
import io.cloudevents.CloudEvent;
import io.cloudevents.DataConversionException;
import io.cloudevents.format.json.CloudEventDeserializer;
import io.cloudevents.format.json.CloudEventSerializer;
import io.cloudevents.json.Json;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.*;

@JsonSerialize(using = CloudEventSerializer.class)
@JsonDeserialize(using = CloudEventDeserializer.class)
public final class CloudEventImpl implements CloudEvent {

    private final Attributes attributes;
    private final Object data;
    private final Map<String, Object> extensions;

    public CloudEventImpl(Attributes attributes, Object data, Map<String, Object> extensions) {
        Objects.requireNonNull(attributes);
        this.attributes = attributes;
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
                    return Optional.of(Json.MAPPER.writeValueAsString(data));
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
                    return Optional.of(Json.MAPPER.writeValueAsBytes(data));
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
                    return Optional.of(Json.MAPPER.readTree((String)data));
                } catch (IOException e) {
                    throw new DataConversionException("String", "JsonNode", e);
                }
            }
            if (data instanceof byte[]) {
                try {
                    return Optional.of(Json.MAPPER.readTree((byte[]) data));
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
}
