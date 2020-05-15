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

package io.cloudevents.impl;

import io.cloudevents.*;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

public abstract class BaseCloudEvent implements CloudEvent {

    private final byte[] data;
    private final Map<String, Object> extensions;

    protected BaseCloudEvent(byte[] data, Map<String, Object> extensions) {
        this.data = data;
        this.extensions = extensions != null ? extensions : new HashMap<>();
    }

    @Override
    public byte[] getData() {
        return this.data;
    }

    @Override
    public Map<String, Object> getExtensions() {
        return Collections.unmodifiableMap(extensions);
    }

    @Override
    public <T extends CloudEventVisitor<V>, V> V visit(CloudEventVisitorFactory<T, V> visitorFactory) throws CloudEventVisitException, IllegalStateException {
        CloudEventVisitor<V> visitor = visitorFactory.create(this.getSpecVersion());
        this.visitAttributes(visitor);
        this.visitExtensions(visitor);

        if (this.data != null) {
            visitor.setBody(this.data);
        }

        return visitor.end();
    }

    @Override
    public void visitExtensions(CloudEventExtensionsVisitor visitor) throws CloudEventVisitException {
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
