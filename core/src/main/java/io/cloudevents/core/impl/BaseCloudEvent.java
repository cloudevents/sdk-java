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
import io.cloudevents.CloudEventData;
import io.cloudevents.rw.*;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;

public abstract class BaseCloudEvent implements CloudEvent, CloudEventReader, CloudEventContextReader {

    private final CloudEventData data;
    protected final Map<String, Object> extensions;

    protected BaseCloudEvent(CloudEventData data, Map<String, Object> extensions) {
        this.data = data;
        this.extensions = extensions != null ? extensions : new HashMap<>();
    }

    @Override
    public CloudEventData getData() {
        return this.data;
    }

    @Override
    public Object getExtension(String extensionName) {
        return this.extensions.get(extensionName);
    }

    @Override
    public Set<String> getExtensionNames() {
        return this.extensions.keySet();
    }

    @Override
    public <T extends CloudEventWriter<V>, V> V read(CloudEventWriterFactory<T, V> writerFactory, CloudEventDataMapper<? extends CloudEventData> mapper) throws CloudEventRWException, IllegalStateException {
        CloudEventWriter<V> visitor = writerFactory.create(this.getSpecVersion());
        this.readAttributes(visitor);
        this.readExtensions(visitor);

        if (this.data != null) {
            return visitor.end(mapper.map(this.data));
        }

        return visitor.end();
    }

    public void readExtensions(CloudEventExtensionsWriter writer) throws CloudEventRWException {
        // TODO to be improved
        for (Map.Entry<String, Object> entry : this.extensions.entrySet()) {
            if (entry.getValue() instanceof String) {
                writer.withExtension(entry.getKey(), (String) entry.getValue());
            } else if (entry.getValue() instanceof Number) {
                writer.withExtension(entry.getKey(), (Number) entry.getValue());
            } else if (entry.getValue() instanceof Boolean) {
                writer.withExtension(entry.getKey(), (Boolean) entry.getValue());
            } else {
                // This should never happen because we build that map only through our builders
                throw new IllegalStateException("Illegal value inside extensions map: " + entry);
            }
        }
    }
}
