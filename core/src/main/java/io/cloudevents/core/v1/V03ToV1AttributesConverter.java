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

import io.cloudevents.types.Time;
import io.cloudevents.visitor.CloudEventAttributesVisitor;
import io.cloudevents.visitor.CloudEventVisitException;

import java.net.URI;
import java.net.URISyntaxException;
import java.time.ZonedDateTime;
import java.time.format.DateTimeParseException;

class V03ToV1AttributesConverter implements CloudEventAttributesVisitor {

    private final CloudEventBuilder builder;

    V03ToV1AttributesConverter(CloudEventBuilder builder) {
        this.builder = builder;
    }

    @Override
    public void setAttribute(String name, String value) throws CloudEventVisitException {
        switch (name) {
            case "id":
                builder.withId(value);
                return;
            case "source":
                try {
                    builder.withSource(new URI(value));
                } catch (URISyntaxException e) {
                    throw CloudEventVisitException.newInvalidAttributeValue("source", value, e);
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
                    throw CloudEventVisitException.newInvalidAttributeValue("dataschema", value, e);
                }
                return;
            case "subject":
                builder.withSubject(value);
                return;
            case "time":
                try {
                    builder.withTime(Time.parseTime(value));
                } catch (DateTimeParseException e) {
                    throw CloudEventVisitException.newInvalidAttributeValue("time", value, e);
                }
                return;
        }
        throw CloudEventVisitException.newInvalidAttributeName(name);
    }

    @Override
    public void setAttribute(String name, URI value) throws CloudEventVisitException {
        switch (name) {
            case "source":
                builder.withSource(value);
                return;
            case "schemaurl":
                builder.withDataSchema(value);
                return;
        }
        throw CloudEventVisitException.newInvalidAttributeType(name, URI.class);
    }

    @Override
    public void setAttribute(String name, ZonedDateTime value) throws CloudEventVisitException {
        if ("time".equals(name)) {
            builder.withTime(value);
            return;
        }
        throw CloudEventVisitException.newInvalidAttributeType(name, ZonedDateTime.class);
    }
}
