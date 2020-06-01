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

import io.cloudevents.CloudEvent;
import io.cloudevents.Extension;
import io.cloudevents.core.builder.CloudEventBuilder;
import io.cloudevents.visitor.CloudEventVisitException;

import java.net.URI;
import java.util.HashMap;
import java.util.Map;

public abstract class BaseCloudEventBuilder<SELF extends BaseCloudEventBuilder<SELF, T>, T extends CloudEvent> implements CloudEventBuilder<SELF> {

    // This is a little trick for enabling fluency
    private SELF self;

    protected byte[] data;
    protected Map<String, Object> extensions;

    @SuppressWarnings("unchecked")
    public BaseCloudEventBuilder() {
        this.self = (SELF) this;
        this.extensions = new HashMap<>();
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

    protected abstract void setAttributes(CloudEvent event);

    protected abstract SELF withDataContentType(String contentType);

    protected abstract SELF withDataSchema(URI dataSchema);

    //TODO builder should accept data as Object and use data codecs (that we need to implement)
    // to encode data

    public SELF withData(byte[] data) {
        this.data = data;
        return this.self;
    }

    public SELF withData(String contentType, byte[] data) {
        withDataContentType(contentType);
        withData(data);
        return this.self;
    }

    public SELF withData(String contentType, URI dataSchema, byte[] data) {
        withDataContentType(contentType);
        withDataSchema(dataSchema);
        withData(data);
        return this.self;
    }

    public SELF withExtension(String key, String value) {
        this.extensions.put(key, value);
        return self;
    }

    public SELF withExtension(String key, Number value) {
        this.extensions.put(key, value);
        return self;
    }

    public SELF withExtension(String key, boolean value) {
        this.extensions.put(key, value);
        return self;
    }

    public SELF withExtension(Extension extension) {
        for (String key : extension.getKeys()) {
            Object value = extension.getValue(key);
            if (value != null) {
                this.extensions.put(key, value);
            }
        }
        return self;
    }

    @Override
    public void setExtension(String name, String value) throws CloudEventVisitException {
        this.withExtension(name, value);
    }

    @Override
    public void setExtension(String name, Number value) throws CloudEventVisitException {
        this.withExtension(name, value);
    }

    @Override
    public void setExtension(String name, Boolean value) throws CloudEventVisitException {
        this.withExtension(name, value);
    }

    @Override
    public CloudEvent end(byte[] value) throws CloudEventVisitException {
        this.data = value;
        return build();
    }

    @Override
    public CloudEvent end() {
        return build();
    }
}
