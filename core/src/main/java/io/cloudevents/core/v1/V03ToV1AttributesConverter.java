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

package io.cloudevents.core.v1;

import io.cloudevents.rw.CloudEventAttributesWriter;
import io.cloudevents.rw.CloudEventRWException;
import io.cloudevents.types.Time;

import java.net.URI;
import java.net.URISyntaxException;
import java.time.OffsetDateTime;
import java.time.format.DateTimeParseException;

class V03ToV1AttributesConverter implements CloudEventAttributesWriter {

    private final CloudEventBuilder builder;

    V03ToV1AttributesConverter(CloudEventBuilder builder) {
        this.builder = builder;
    }

    @Override
    public void setAttribute(String name, String value) throws CloudEventRWException {
        switch (name) {
            case "id":
                builder.withId(value);
                return;
            case "source":
                try {
                    builder.withSource(new URI(value));
                } catch (URISyntaxException e) {
                    throw CloudEventRWException.newInvalidAttributeValue("source", value, e);
                }
                return;
            case "type":
                builder.withType(value);
                return;
            case "datacontenttype":
                builder.withDataContentType(value);
                return;
            case "schemaurl":
                try {
                    builder.withDataSchema(new URI(value));
                } catch (URISyntaxException e) {
                    throw CloudEventRWException.newInvalidAttributeValue("dataschema", value, e);
                }
                return;
            case "subject":
                builder.withSubject(value);
                return;
            case "time":
                try {
                    builder.withTime(Time.parseTime(value));
                } catch (DateTimeParseException e) {
                    throw CloudEventRWException.newInvalidAttributeValue("time", value, e);
                }
                return;
        }
        throw CloudEventRWException.newInvalidAttributeName(name);
    }

    @Override
    public void setAttribute(String name, URI value) throws CloudEventRWException {
        switch (name) {
            case "source":
                builder.withSource(value);
                return;
            case "schemaurl":
                builder.withDataSchema(value);
                return;
        }
        throw CloudEventRWException.newInvalidAttributeType(name, URI.class);
    }

    @Override
    public void setAttribute(String name, OffsetDateTime value) throws CloudEventRWException {
        if ("time".equals(name)) {
            builder.withTime(value);
            return;
        }
        throw CloudEventRWException.newInvalidAttributeType(name, OffsetDateTime.class);
    }
}
