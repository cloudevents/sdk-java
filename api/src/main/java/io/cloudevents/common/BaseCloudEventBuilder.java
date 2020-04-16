package io.cloudevents.common;

import io.cloudevents.Attributes;
import io.cloudevents.CloudEvent;
import io.cloudevents.Data;
import io.cloudevents.Extension;

import java.net.URI;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.function.Supplier;

public abstract class BaseCloudEventBuilder<B extends BaseCloudEventBuilder<B, T>, T extends Attributes> {

    // This is a little trick for enabling fluency
    private B self;

    private Data data;
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

    public B withData(String contentType, Data data) {
        withDataContentType(contentType);
        this.data = data;
        return self;
    }

    public B withData(String contentType, URI dataSchema, Data data) {
        withDataContentType(contentType);
        withDataSchema(dataSchema);
        this.data = data;
        return self;
    }

    public B withExtension(String key, Object value) {
        this.extensions.put(key, value);
        return self;
    }

    public B withExtension(Extension extension) {
        this.materializedExtensions.add(extension);
        return self;
    }

    public CloudEvent build() {
        CloudEvent event = new CloudEventImpl(this.buildAttributes(), data, extensions);

        for (Extension ext : this.materializedExtensions) {
            ext.writeToEvent(event);
        }

        return event;
    }

}
