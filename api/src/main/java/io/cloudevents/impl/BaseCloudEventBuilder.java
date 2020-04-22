package io.cloudevents.impl;

import io.cloudevents.Attributes;
import io.cloudevents.CloudEvent;
import io.cloudevents.Extension;
import io.cloudevents.message.BinaryMessageVisitor;
import io.cloudevents.message.MessageVisitException;

import java.net.URI;
import java.util.HashMap;
import java.util.Map;

public abstract class BaseCloudEventBuilder<B extends BaseCloudEventBuilder<B, T>, T extends Attributes> implements BinaryMessageVisitor<CloudEvent> {

    // This is a little trick for enabling fluency
    private B self;

    private byte[] data;
    private Map<String, Object> extensions;

    @SuppressWarnings("unchecked")
    public BaseCloudEventBuilder() {
        this.self = (B)this;
        this.extensions = new HashMap<>();
    }

    @SuppressWarnings("unchecked")
    public BaseCloudEventBuilder(CloudEvent event) {
        this.self = (B)this;

        CloudEventImpl ev = (CloudEventImpl) event;
        this.setAttributes(ev.getAttributes());
        this.data = ev.getData().orElse(null);
        this.extensions = new HashMap<>(ev.getExtensions());
    }

    protected abstract void setAttributes(Attributes attributes);

    protected abstract B withDataContentType(String contentType);

    protected abstract B withDataSchema(URI dataSchema);

    protected abstract T buildAttributes();

    //TODO builder should accept data as Object and use data codecs (that we need to implement)
    // to encode data

    public B withData(byte[] data) {
        this.data = data;
        return this.self;
    }

    public B withData(String contentType, byte[] data) {
        withDataContentType(contentType);
        withData(data);
        return this.self;
    }

    public B withData(String contentType, URI dataSchema, byte[] data) {
        withDataContentType(contentType);
        withDataSchema(dataSchema);
        withData(data);
        return this.self;
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
        this.extensions.putAll(extension.asMap());
        return self;
    }

    public CloudEvent build() {
        return new CloudEventImpl(this.buildAttributes(), data, extensions);
    }

    @Override
    public void setExtension(String name, String value) throws MessageVisitException {
        this.withExtension(name, value);
    }

    @Override
    public void setExtension(String name, Number value) throws MessageVisitException {
        this.withExtension(name, value);
    }

    @Override
    public void setExtension(String name, Boolean value) throws MessageVisitException {
        this.withExtension(name, value);
    }

    @Override
    public void setBody(byte[] value) throws MessageVisitException {
        this.data = value;
    }

    @Override
    public CloudEvent end() {
        return build();
    }
}
