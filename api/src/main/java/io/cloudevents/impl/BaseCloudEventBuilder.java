package io.cloudevents.impl;

import com.fasterxml.jackson.databind.JsonNode;
import io.cloudevents.Attributes;
import io.cloudevents.CloudEvent;
import io.cloudevents.Extension;

import java.net.URI;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public abstract class BaseCloudEventBuilder<B extends BaseCloudEventBuilder<B, T>, T extends Attributes> {

    // This is a little trick for enabling fluency
    private B self;

    private Object data;
    private Map<String, Object> extensions;
    private List<Extension> materializedExtensions;

    @SuppressWarnings("unchecked")
    public BaseCloudEventBuilder() {
        this.self = (B)this;
        this.extensions = new HashMap<>();
        this.materializedExtensions = new ArrayList<>();
    }

    protected abstract B withDataContentType(String contentType);

    protected abstract B withDataSchema(URI dataSchema);

    protected abstract T buildAttributes();

    //TODO builder should accept data as Object and use data codecs (that we need to implement)
    // to encode data

    public B withData(String contentType, String data) {
        return withEncodedData(contentType, (Object) data);
    }

    public B withData(String contentType, byte[] data) {
        return withEncodedData(contentType, (Object) data);
    }

    public B withData(String contentType, JsonNode data) {
        return withEncodedData(contentType, (Object) data);
    }

    public B withData(String contentType, URI dataSchema, String data) {
        return withEncodedata(contentType, dataSchema, (Object) data);
    }

    public B withData(String contentType, URI dataSchema, byte[] data) {
        return withEncodedata(contentType, dataSchema, (Object) data);
    }

    public B withData(String contentType, URI dataSchema, JsonNode data) {
        return withEncodedata(contentType, dataSchema, (Object) data);
    }

    public B withExtension(String key, String value) {
        this.extensions.put(key, value);
        return self;
    }

    public B withExtension(String key, Number value) {
        this.extensions.put(key, value);
        return self;
    }

    public B withExtension(String key, boolean value) {
        this.extensions.put(key, value);
        return self;
    }

    public B withExtension(Extension extension) {
        this.materializedExtensions.add(extension);
        return self;
    }

    public CloudEvent build() {
        CloudEvent event = new CloudEventImpl(this.buildAttributes(), data, extensions);

        // Write materialized extensions into the event
        for (Extension ext : this.materializedExtensions) {
            ext.writeToEvent(event);
        }

        return event;
    }

    private B withEncodedData(String contentType, Object data) {
        withDataContentType(contentType);
        this.data = data;
        return self;
    }

    private B withEncodedata(String contentType, URI dataSchema, Object data) {
        withDataContentType(contentType);
        withDataSchema(dataSchema);
        this.data = data;
        return self;
    }

}
