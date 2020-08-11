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

import io.cloudevents.Extension;
import io.cloudevents.core.CloudEvent;
import io.cloudevents.core.builder.CloudEventBuilder;
import io.cloudevents.rw.CloudEventRWException;

import javax.annotation.Nonnull;
import java.net.URI;
import java.util.HashMap;
import java.util.Map;

public abstract class BaseCloudEventBuilder<SELF extends BaseCloudEventBuilder<SELF, T>, T extends CloudEvent> implements CloudEventBuilder<T> {

    // This is a little trick for enabling fluency
    private final SELF self;

    protected Object data;
    protected Map<String, Object> extensions;

    @SuppressWarnings("unchecked")
    public BaseCloudEventBuilder() {
        this.self = (SELF) this;
        this.extensions = new HashMap<>();
    }

    @SuppressWarnings("unchecked")
    public BaseCloudEventBuilder(io.cloudevents.CloudEvent event) {
        this.self = (SELF) this;

        this.setAttributes(event);
        if (event instanceof CloudEvent) {
            this.data = ((CloudEvent) event).getRawData();
        } else {
            this.data = event.getData();
        }
        this.extensions = new HashMap<>();
        for (String k : event.getExtensionNames()) {
            this.extensions.put(k, event.getExtension(k));
        }
    }

    protected abstract void setAttributes(io.cloudevents.CloudEvent event);

    public SELF withData(Object data) {
        this.data = data;
        return this.self;
    }

    public SELF withData(String dataContentType, Object data) {
        withDataContentType(dataContentType);
        withData(data);
        return this.self;
    }

    public SELF withData(String dataContentType, URI dataSchema, Object data) {
        withDataContentType(dataContentType);
        withDataSchema(dataSchema);
        withData(data);
        return this.self;
    }

    public SELF withExtension(@Nonnull String key, String value) {
        this.extensions.put(key, value);
        return self;
    }

    public SELF withExtension(@Nonnull String key, Number value) {
        this.extensions.put(key, value);
        return self;
    }

    public SELF withExtension(@Nonnull String key, boolean value) {
        this.extensions.put(key, value);
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
    public void setExtension(String name, String value) throws CloudEventRWException {
        this.withExtension(name, value);
    }

    @Override
    public void setExtension(String name, Number value) throws CloudEventRWException {
        this.withExtension(name, value);
    }

    @Override
    public void setExtension(String name, Boolean value) throws CloudEventRWException {
        this.withExtension(name, value);
    }

    @Override
    public CloudEvent end(String contentType, Object value) throws CloudEventRWException {
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
}
