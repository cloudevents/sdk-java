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
import io.cloudevents.core.provider.EventDataCodecProvider;
import io.cloudevents.rw.*;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;

public abstract class BaseCloudEvent implements CloudEvent, CloudEventReader {

    private final Object data;
    protected final Map<String, Object> extensions;

    protected BaseCloudEvent(Object data, Map<String, Object> extensions) {
        this.data = data;
        this.extensions = extensions != null ? extensions : new HashMap<>();
    }

    @Override
    public byte[] getData() {
        return getData(byte[].class);
    }

    @Override
    public Object getRawData() {
        return this.data;
    }

    @SuppressWarnings("unchecked")
    @Override
    public <T> T getData(Class<T> c) throws IllegalArgumentException {
        if (this.data == null) {
            return null;
        }
        if (c.equals(data.getClass())) {
            return (T) this.data;
        }
        return EventDataCodecProvider.getInstance().deserialize(this.getDataContentType(), this.data, c);
    }

    @Override
    public Object getExtension(String extensionName) {
        return this.extensions.get(extensionName);
    }

    @Override
    public Set<String> getExtensionNames() {
        return this.extensions.keySet();
    }

    public <T extends CloudEventWriter<V>, V> V read(CloudEventWriterFactory<T, V> writerFactory) throws CloudEventRWException, IllegalStateException {
        CloudEventWriter<V> visitor = writerFactory.create(this.getSpecVersion());
        this.readAttributes(visitor);
        this.readExtensions(visitor);

        if (this.data != null) {
            return visitor.end(this.getDataContentType(), this.data);
        }

        return visitor.end();
    }

    public void readExtensions(CloudEventExtensionsWriter visitor) throws CloudEventRWException {
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
    }
}
