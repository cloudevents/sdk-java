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
import io.cloudevents.rw.*;

public class CloudEventReaderAdapter implements CloudEventReader {

    private final CloudEvent event;

    CloudEventReaderAdapter(CloudEvent event) {
        this.event = event;
    }

    @Override
    public <V extends CloudEventWriter<R>, R> R read(CloudEventWriterFactory<V, R> writerFactory) throws RuntimeException {
        CloudEventWriter<R> visitor = writerFactory.create(event.getSpecVersion());
        this.readAttributes(visitor);
        this.readExtensions(visitor);

        if (event.getData() != null) {
            return visitor.end(event.getDataContentType(), event.getData());
        }

        return visitor.end();
    }

    @Override
    public void readAttributes(CloudEventAttributesWriter writer) throws RuntimeException {
        writer.setAttribute("id", event.getId());
        writer.setAttribute("source", event.getSource());
        writer.setAttribute("type", event.getType());
        if (event.getDataContentType() != null) {
            writer.setAttribute("datacontenttype", event.getDataContentType());
        }
        if (event.getDataSchema() != null) {
            writer.setAttribute("dataschema", event.getDataSchema());
        }
        if (event.getSubject() != null) {
            writer.setAttribute("subject", event.getSubject());
        }
        if (event.getTime() != null) {
            writer.setAttribute("time", event.getTime());
        }
    }

    @Override
    public void readExtensions(CloudEventExtensionsWriter visitor) throws RuntimeException {
        for (String key : event.getExtensionNames()) {
            Object value = event.getExtension(key);
            if (value instanceof String) {
                visitor.setExtension(key, (String) value);
            } else if (value instanceof Number) {
                visitor.setExtension(key, (Number) value);
            } else if (value instanceof Boolean) {
                visitor.setExtension(key, (Boolean) value);
            } else {
                // This should never happen because we build that map only through our builders
                throw new IllegalStateException("Illegal value inside extensions map: " + key + " " + value);
            }
        }
        ;
    }

}
