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

import io.cloudevents.CloudEvent;
import io.cloudevents.SpecVersion;
import io.cloudevents.core.CloudEventUtils;
import io.cloudevents.core.impl.BaseCloudEventBuilder;
import io.cloudevents.rw.CloudEventContextReader;
import io.cloudevents.rw.CloudEventRWException;
import io.cloudevents.types.Time;

import java.net.URI;
import java.net.URISyntaxException;
import java.time.OffsetDateTime;

/**
 * CloudEvent V1.0 builder.
 *
 * @author fabiojose
 * @author slinkydeveloper
 */
public final class CloudEventBuilder extends BaseCloudEventBuilder<CloudEventBuilder, CloudEventV1> {

    private String id;
    private URI source;
    private String type;
    private String datacontenttype;
    private URI dataschema;
    private String subject;
    private OffsetDateTime time;

    public CloudEventBuilder() {
        super();
    }

    public CloudEventBuilder(io.cloudevents.CloudEvent event) {
        super(event);
    }

    public CloudEventBuilder(io.cloudevents.CloudEventContext context) {
        super(context);
    }

    @Override
    protected void setAttributes(io.cloudevents.CloudEventContext event) {
        CloudEventContextReader contextReader = CloudEventUtils.toContextReader(event);
        if (event.getSpecVersion() == SpecVersion.V1) {
            contextReader.readAttributes(this);
        } else {
            contextReader.readAttributes(new V03ToV1AttributesConverter(this));
        }
        contextReader.readExtensions(this);
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

    public CloudEventBuilder withTime(OffsetDateTime time) {
        this.time = time;
        return this;
    }

    @Override
    public CloudEvent build() {
        if (id == null) {
            throw createMissingAttributeException(CloudEventV1.ID);
        }
        if (source == null) {
            throw createMissingAttributeException(CloudEventV1.SOURCE);
        }
        if (type == null) {
            throw createMissingAttributeException(CloudEventV1.TYPE);
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
    public CloudEventBuilder withAttribute(String name, String value) throws CloudEventRWException {
        switch (name) {
            case CloudEventV1.ID:
                withId(value);
                return this;
            case CloudEventV1.SOURCE:
                try {
                    withSource(new URI(value));
                } catch (URISyntaxException e) {
                    throw CloudEventRWException.newInvalidAttributeValue(CloudEventV1.SOURCE, value, e);
                }
                return this;
            case CloudEventV1.TYPE:
                withType(value);
                return this;
            case CloudEventV1.DATACONTENTTYPE:
                withDataContentType(value);
                return this;
            case CloudEventV1.DATASCHEMA:
                try {
                    withDataSchema(new URI(value));
                } catch (URISyntaxException e) {
                    throw CloudEventRWException.newInvalidAttributeValue(CloudEventV1.DATASCHEMA, value, e);
                }
                return this;
            case CloudEventV1.SUBJECT:
                withSubject(value);
                return this;
            case CloudEventV1.TIME:
                withTime(Time.parseTime(CloudEventV1.TIME, value));
                return this;
        }
        throw CloudEventRWException.newInvalidAttributeName(name);
    }

    @Override
    public CloudEventBuilder withAttribute(String name, URI value) throws CloudEventRWException {
        switch (name) {
            case CloudEventV1.SOURCE:
                withSource(value);
                return this;
            case CloudEventV1.DATASCHEMA:
                withDataSchema(value);
                return this;
        }
        throw CloudEventRWException.newInvalidAttributeType(name, URI.class);
    }

    @Override
    public CloudEventBuilder withAttribute(String name, OffsetDateTime value) throws CloudEventRWException {
        if (CloudEventV1.TIME.equals(name)) {
            withTime(value);
            return this;
        }
        throw CloudEventRWException.newInvalidAttributeType(name, OffsetDateTime.class);
    }
}
