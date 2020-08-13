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

import io.cloudevents.SpecVersion;
import io.cloudevents.core.impl.BaseCloudEventBuilder;
import io.cloudevents.core.impl.CloudEventUtils;
import io.cloudevents.rw.CloudEventRWException;
import io.cloudevents.types.Time;

import java.net.URI;
import java.net.URISyntaxException;
import java.time.ZonedDateTime;
import java.time.format.DateTimeParseException;

/**
 * CloudEvent V1.0 builder.
 *
 * @author fabiojose
 * @author slinkydeveloper
 */
public final class CloudEventBuilder extends BaseCloudEventBuilder<CloudEventBuilder> {

    private String id;
    private URI source;
    private String type;
    private String datacontenttype;
    private URI dataschema;
    private String subject;
    private ZonedDateTime time;

    public CloudEventBuilder() {
        super();
    }

    public CloudEventBuilder(io.cloudevents.CloudEvent event) {
        super(event);
    }

    @Override
    protected void setAttributes(io.cloudevents.CloudEvent event) {
        if (event.getSpecVersion() == SpecVersion.V1) {
            CloudEventUtils.toVisitable(event).readAttributes(this);
        } else {
            CloudEventUtils.toVisitable(event).readAttributes(new V03ToV1AttributesConverter(this));
        }
    }

    public CloudEventBuilder withId(String id) {
        this.id = id;
        return this;
    }

    public CloudEventBuilder withSource(URI source) {
        this.source = source;
        return this;
    }

    public CloudEventBuilder withType(String type) {
        this.type = type;
        return this;
    }

    public CloudEventBuilder withDataSchema(URI dataSchema) {
        this.dataschema = dataSchema;
        return this;
    }

    public CloudEventBuilder withDataContentType(
        String dataContentType) {
        this.datacontenttype = dataContentType;
        return this;
    }

    public CloudEventBuilder withSubject(
        String subject) {
        this.subject = subject;
        return this;
    }

    public CloudEventBuilder withTime(ZonedDateTime time) {
        this.time = time;
        return this;
    }

    @Override
    public CloudEventV1 build() {
        if (id == null) {
            throw createMissingAttributeException("id");
        }
        if (source == null) {
            throw createMissingAttributeException("source");
        }
        if (type == null) {
            throw createMissingAttributeException("type");
        }

        return new CloudEventV1(id, source, type, datacontenttype, dataschema, subject, time, this.data, this.extensions);
    }

    @Override
    public CloudEventBuilder newBuilder() {
        CloudEventBuilder newBuilder = new CloudEventBuilder();
        newBuilder.id = this.id;
        newBuilder.source = this.source;
        newBuilder.type = this.type;
        newBuilder.time = this.time;
        newBuilder.dataschema = this.dataschema;
        newBuilder.datacontenttype = this.datacontenttype;
        newBuilder.subject = this.subject;
        newBuilder.data = this.data;
        newBuilder.extensions.putAll(this.extensions);
        return newBuilder;
    }

    // Message impl

    @Override
    public void setAttribute(String name, String value) throws CloudEventRWException {
        switch (name) {
            case "id":
                withId(value);
                return;
            case "source":
                try {
                    withSource(new URI(value));
                } catch (URISyntaxException e) {
                    throw CloudEventRWException.newInvalidAttributeValue("source", value, e);
                }
                return;
            case "type":
                withType(value);
                return;
            case "datacontenttype":
                withDataContentType(value);
                return;
            case "dataschema":
                try {
                    withDataSchema(new URI(value));
                } catch (URISyntaxException e) {
                    throw CloudEventRWException.newInvalidAttributeValue("dataschema", value, e);
                }
                return;
            case "subject":
                withSubject(value);
                return;
            case "time":
                try {
                    withTime(Time.parseTime(value));
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
                withSource(value);
                return;
            case "dataschema":
                withDataSchema(value);
                return;
        }
        throw CloudEventRWException.newInvalidAttributeType(name, URI.class);
    }

    @Override
    public void setAttribute(String name, ZonedDateTime value) throws CloudEventRWException {
        if ("time".equals(name)) {
            withTime(value);
            return;
        }
        throw CloudEventRWException.newInvalidAttributeType(name, ZonedDateTime.class);
    }
}
