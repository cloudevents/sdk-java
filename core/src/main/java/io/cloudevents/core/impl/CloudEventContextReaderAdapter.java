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

import io.cloudevents.CloudEventContext;
import io.cloudevents.rw.CloudEventContextReader;
import io.cloudevents.rw.CloudEventContextWriter;
import io.cloudevents.rw.CloudEventRWException;

import java.net.URI;
import java.time.OffsetDateTime;

public class CloudEventContextReaderAdapter implements CloudEventContextReader {

    private final CloudEventContext event;

    public CloudEventContextReaderAdapter(CloudEventContext event) {
        this.event = event;
    }

    public void readAttributes(CloudEventContextWriter writer) throws RuntimeException {
        writer.withContextAttribute("id", event.getId());
        writer.withContextAttribute("source", event.getSource());
        writer.withContextAttribute("type", event.getType());
        if (event.getDataContentType() != null) {
            writer.withContextAttribute("datacontenttype", event.getDataContentType());
        }
        if (event.getDataSchema() != null) {
            writer.withContextAttribute("dataschema", event.getDataSchema());
        }
        if (event.getSubject() != null) {
            writer.withContextAttribute("subject", event.getSubject());
        }
        if (event.getTime() != null) {
            writer.withContextAttribute("time", event.getTime());
        }
    }

    public void readExtensions(CloudEventContextWriter writer) throws RuntimeException {
        for (String key : event.getExtensionNames()) {
            Object value = event.getExtension(key);
            if (value instanceof String string) {
                writer.withContextAttribute(key, string);
            } else if (value instanceof Number number) {
                writer.withContextAttribute(key, number);
            } else if (value instanceof Boolean boolean1) {
                writer.withContextAttribute(key, boolean1);
            } else if (value instanceof URI rI) {
                writer.withContextAttribute(key, rI);
            } else if (value instanceof OffsetDateTime time) {
                writer.withContextAttribute(key, time);
            } else {
                // This should never happen because we build that map only through our builders
                throw new IllegalStateException("Illegal value inside extensions map: " + key + " " + value);
            }
        }
    }

    @Override
    public void readContext(CloudEventContextWriter writer) throws CloudEventRWException {
        this.readAttributes(writer);
        this.readExtensions(writer);
    }
}
