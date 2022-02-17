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
import io.cloudevents.CloudEventContext;
import io.cloudevents.CloudEventData;
import io.cloudevents.CloudEventExtension;
import io.cloudevents.core.builder.CloudEventBuilder;
import io.cloudevents.core.data.BytesCloudEventData;
import io.cloudevents.rw.CloudEventRWException;

import javax.annotation.Nonnull;
import java.net.URI;
import java.time.OffsetDateTime;
import java.util.HashMap;
import java.util.Map;

import static io.cloudevents.core.v03.CloudEventV03.SPECVERSION;

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

    public BaseCloudEventBuilder(CloudEvent event) {
        this();
        this.setAttributes(event);
        this.data = event.getData();
    }

    protected abstract void setAttributes(CloudEventContext event);

    //TODO builder should accept data as Object and use data codecs (that we need to implement)
    // to encode data

    public SELF withData(byte[] data) {
        this.data = BytesCloudEventData.wrap(data);
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

    @Override
    public CloudEventBuilder withoutData() {
        this.data = null;
        return this.self;
    }

    @Override
    public CloudEventBuilder withoutDataSchema() {
        withDataSchema(null);
        return this.self;
    }

    @Override
    public CloudEventBuilder withoutDataContentType() {
        withDataContentType(null);
        return this.self;
    }

    public SELF withExtension(@Nonnull String key, @Nonnull String value) {
        if (!isValidExtensionName(key)) {
            throw CloudEventRWException.newInvalidExtensionName(key);
        }
        this.extensions.put(key, value);
        return self;
    }

    // @TODO - I think this method should be removed/deprecated
    // **Number** Is NOT a valid CE Context atrribute type.

    public SELF withExtension(@Nonnull String key, @Nonnull Number value) {
        if (!isValidExtensionName(key)) {
            throw CloudEventRWException.newInvalidExtensionName(key);
        }
        this.extensions.put(key, value);
        return self;
    }

    public SELF withExtension(@Nonnull String key, @Nonnull Integer value) {
        if (!isValidExtensionName(key)) {
            throw CloudEventRWException.newInvalidExtensionName(key);
        }
        this.extensions.put(key, value);
        return self;
    }

    public SELF withExtension(@Nonnull String key, @Nonnull Boolean value) {
        if (!isValidExtensionName(key)) {
            throw CloudEventRWException.newInvalidExtensionName(key);
        }
        this.extensions.put(key, value);
        return self;
    }

    @Override
    public SELF withExtension(@Nonnull String key, @Nonnull URI value) {
        if (!isValidExtensionName(key)) {
            throw CloudEventRWException.newInvalidExtensionName(key);
        }
        this.extensions.put(key, value);
        return self;
    }

    @Override
    public SELF withExtension(@Nonnull String key, @Nonnull OffsetDateTime value) {
        if (!isValidExtensionName(key)) {
            throw CloudEventRWException.newInvalidExtensionName(key);
        }
        this.extensions.put(key, value);
        return self;
    }

    @Override
    public CloudEventBuilder withExtension(@Nonnull String key, @Nonnull byte[] value) {
        if (!isValidExtensionName(key)) {
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
    public SELF withoutExtension(@Nonnull CloudEventExtension extension) {
        extension.getKeys().forEach(this::withoutExtension);
        return self;
    }

    public SELF withExtension(@Nonnull CloudEventExtension extension) {
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
     * Validates the extension name as defined in  CloudEvents spec.
     *
     * @param name the extension name
     * @return true if extension name is valid, false otherwise
     * @see <a href="https://github.com/cloudevents/spec/blob/main/cloudevents/spec.md#naming-conventions">attribute-naming-conventions</a>
     */
    private static boolean isValidExtensionName(String name) {
        for (int i = 0; i < name.length(); i++) {
            if (!isValidChar(name.charAt(i))) {
                return false;
            }
        }
        return true;
    }

    private static boolean isValidChar(char c) {
        return (c >= 'a' && c <= 'z') || (c >= '0' && c <= '9');
    }

    protected void requireValidAttributeWrite(String name) {
        if (name.equals(SPECVERSION)) {
            throw new IllegalArgumentException("You should not set the specversion attribute through withContextAttribute methods");
        }
    }
}
