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

package io.cloudevents.core.v03;

import io.cloudevents.rw.CloudEventContextWriter;
import io.cloudevents.rw.CloudEventRWException;
import io.cloudevents.types.Time;

import java.net.URI;
import java.net.URISyntaxException;
import java.time.OffsetDateTime;

import static io.cloudevents.core.v1.CloudEventV1.*;

class V1ToV03AttributesConverter implements CloudEventContextWriter {

    private final CloudEventBuilder builder;

    V1ToV03AttributesConverter(CloudEventBuilder builder) {
        this.builder = builder;
    }

    @Override
    public CloudEventContextWriter withContextAttribute(String name, String value) throws CloudEventRWException {
        switch (name) {
            case ID:
                builder.withId(value);
                return this;
            case SOURCE:
                try {
                    builder.withSource(new URI(value));
                } catch (URISyntaxException e) {
                    throw CloudEventRWException.newInvalidAttributeValue(SOURCE, value, e);
                }
                return this;
            case TYPE:
                builder.withType(value);
                return this;
            case DATACONTENTTYPE:
                builder.withDataContentType(value);
                return this;
            case DATASCHEMA:
                try {
                    builder.withSchemaUrl(new URI(value));
                } catch (URISyntaxException e) {
                    throw CloudEventRWException.newInvalidAttributeValue(DATASCHEMA, value, e);
                }
                return this;
            case SUBJECT:
                builder.withSubject(value);
                return this;
            case TIME:
                builder.withTime(Time.parseTime(TIME, value));
                return this;
            default:
                builder.withExtension(name, value);
                return this;
        }
    }

    @Override
    public CloudEventContextWriter withContextAttribute(String name, URI value) throws CloudEventRWException {
        switch (name) {
            case SOURCE:
                builder.withSource(value);
                return this;
            case DATASCHEMA:
                builder.withSchemaUrl(value);
                return this;
            case ID:
            case TYPE:
            case DATACONTENTTYPE:
            case SUBJECT:
            case TIME:
                throw CloudEventRWException.newInvalidAttributeType(name, URI.class);
            default:
                builder.withExtension(name, value);
                return this;
        }
    }

    @Override
    public CloudEventContextWriter withContextAttribute(String name, OffsetDateTime value) throws CloudEventRWException {
        switch (name) {
            case TIME:
                builder.withTime(value);
                return this;
            case SOURCE:
            case DATASCHEMA:
            case ID:
            case TYPE:
            case DATACONTENTTYPE:
            case SUBJECT:
                throw CloudEventRWException.newInvalidAttributeType(name, OffsetDateTime.class);
            default:
                builder.withExtension(name, value);
                return this;
        }
    }

    @Override
    public CloudEventContextWriter withContextAttribute(String name, Number value) throws CloudEventRWException {
        switch (name) {
            case TIME:
            case SOURCE:
            case DATASCHEMA:
            case ID:
            case TYPE:
            case DATACONTENTTYPE:
            case SUBJECT:
                throw CloudEventRWException.newInvalidAttributeType(name, Number.class);
            default:
                builder.withExtension(name, value);
                return this;
        }
    }

    @Override
    public CloudEventContextWriter withContextAttribute(String name, Boolean value) throws CloudEventRWException {
        switch (name) {
            case TIME:
            case SOURCE:
            case DATASCHEMA:
            case ID:
            case TYPE:
            case DATACONTENTTYPE:
            case SUBJECT:
                throw CloudEventRWException.newInvalidAttributeType(name, Boolean.class);
            default:
                builder.withExtension(name, value);
                return this;
        }
    }
}
