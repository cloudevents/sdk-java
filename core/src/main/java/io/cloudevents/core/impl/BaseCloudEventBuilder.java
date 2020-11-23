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

package io.cloudevents.core.impl;

import java.net.URI;
import java.time.OffsetDateTime;
import java.util.HashMap;
import java.util.Map;

import javax.annotation.Nonnull;

import io.cloudevents.CloudEvent;
import io.cloudevents.CloudEventContext;
import io.cloudevents.CloudEventData;
import io.cloudevents.Extension;
import io.cloudevents.core.builder.CloudEventBuilder;
import io.cloudevents.core.data.BytesCloudEventData;
import io.cloudevents.rw.CloudEventRWException;

public abstract class BaseCloudEventBuilder<SELF extends BaseCloudEventBuilder<SELF, T>, T extends CloudEvent> implements CloudEventBuilder {

    // This is a little trick for enabling fluency
    private final SELF self;

    protected CloudEventData data;
    protected Map<String, Object> extensions = new HashMap<>();

    @SuppressWarnings("unchecked")
    public BaseCloudEventBuilder() {
        this.self = (SELF) this;
    }

    public BaseCloudEventBuilder(CloudEventContext context) {
        this();
        setAttributes(context);
    }

    @SuppressWarnings("unchecked")
    public BaseCloudEventBuilder(CloudEvent event) {
        this.self = (SELF) this;

        this.setAttributes(event);
        this.data = event.getData();
        this.extensions = new HashMap<>();
        for (String k : event.getExtensionNames()) {
            this.extensions.put(k, event.getExtension(k));
        }
    }

    protected abstract void setAttributes(CloudEventContext event);

    //TODO builder should accept data as Object and use data codecs (that we need to implement)
    // to encode data

    public SELF withData(byte[] data) {
        this.data = new BytesCloudEventData(data);
        return this.self;
    }

    public SELF withData(String dataContentType, byte[] data) {
        withDataContentType(dataContentType);
        withData(data);
        return this.self;
    }

    public SELF withData(String dataContentType, URI dataSchema, byte[] data) {
        withDataContentType(dataContentType);
        withDataSchema(dataSchema);
        withData(data);
        return this.self;
    }

    public SELF withData(CloudEventData data) {
        this.data = data;
        return this.self;
    }

    public SELF withData(String dataContentType, CloudEventData data) {
        withDataContentType(dataContentType);
        withData(data);
        return this.self;
    }

    public SELF withData(String dataContentType, URI dataSchema, CloudEventData data) {
        withDataContentType(dataContentType);
        withDataSchema(dataSchema);
        withData(data);
        return this.self;
    }

    public SELF withExtension(@Nonnull String key, String value) {
        if(!isValidExtensionName(key)){
            throw CloudEventRWException.newInvalidExtensionName(key);
        }
        this.extensions.put(key, value);
        return self;
    }

    public SELF withExtension(@Nonnull String key, Number value) {
        if(!isValidExtensionName(key)){
            throw CloudEventRWException.newInvalidExtensionName(key);
        }
        this.extensions.put(key, value);
        return self;
    }

    public SELF withExtension(@Nonnull String key, Boolean value) {
        if(!isValidExtensionName(key)){
            throw CloudEventRWException.newInvalidExtensionName(key);
        }
        this.extensions.put(key, value);
        return self;
    }

    @Override
    public SELF withoutExtension(@Nonnull String key) {
        this.extensions.remove(key);
        return self;
    }

    @Override
    public SELF withoutExtension(@Nonnull Extension extension) {
        extension.getKeys().forEach(this::withoutExtension);
        return self;
    }

    public SELF withExtension(@Nonnull Extension extension) {
        for (String key : extension.getKeys()) {
            Object value = extension.getValue(key);
            if (value != null) {
                this.extensions.put(key, value);
            }
        }
        return self;
    }

    @Override
    public CloudEvent end(CloudEventData value) throws CloudEventRWException {
        this.data = value;
        return build();
    }

    @Override
    public CloudEvent end() {
        try {
            return build();
        } catch (Exception e) {
            throw CloudEventRWException.newOther(e);
        }
    }

    protected static IllegalStateException createMissingAttributeException(String attributeName) {
        return new IllegalStateException("Attribute '" + attributeName + "' cannot be null");
    }
    /**
     * Validates the extension name as defined in  CloudEvents spec
     * See <a href="https://github.com/cloudevents/spec/blob/master/spec.md#attribute-naming-convention">attribute-naming-convention</a>
     * @param name the extension name
     * @return true if extension name is valid, false otherwise
     */
    private static boolean isValidExtensionName(String name) {
        if(name.length() > 20){
            return false;
        }
        char[] chars = name.toCharArray();
        for (char c: chars)
        if (!isValidChar(c)) {
            return false;
        }
        return true;
    }

    private static boolean isValidChar(char c) {
        return (c >= 'a' && c <= 'z') || (c >= '0' && c <= '9');
    }
}
