package io.cloudevents.common;

import io.cloudevents.Attributes;
import io.cloudevents.CloudEvent;
import io.cloudevents.Data;

import java.util.HashMap;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;

public class CloudEventImpl implements CloudEvent {

    private final Attributes attributes;
    private final Data data;
    private final Map<String, Object> extensions;

    public CloudEventImpl(Attributes attributes, Data data, Map<String, Object> extensions) {
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
    public Optional<Data> getData() {
        return Optional.ofNullable(data);
    }

    @Override
    public Map<String, Object> getExtensions() {
        return extensions;
    }
}
