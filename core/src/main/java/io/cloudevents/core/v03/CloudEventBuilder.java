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

import io.cloudevents.SpecVersion;
import io.cloudevents.core.CloudEventUtils;
import io.cloudevents.core.impl.BaseCloudEventBuilder;
import io.cloudevents.rw.CloudEventRWException;
import io.cloudevents.types.Time;

import java.net.URI;
import java.net.URISyntaxException;
import java.time.OffsetDateTime;

/**
 * CloudEvent V0.3 builder.
 *
 * @author fabiojose
 * @author slinkydeveloper
 */
public final class CloudEventBuilder extends BaseCloudEventBuilder<CloudEventBuilder, CloudEventV03> {

    private String id;
    private URI source;
    private String type;
    private OffsetDateTime time;
    private URI schemaurl;
    private String datacontenttype;
    private String subject;

    public CloudEventBuilder() {
        super();
    }

    public CloudEventBuilder(io.cloudevents.CloudEvent event) {
        super(event);
    }

    public CloudEventBuilder(io.cloudevents.CloudEventContext context) {
        super();
        for (String name: context.getAttributeNames()) {
            if (!name.equals(CloudEventV03.SPECVERSION)) {
                Object value = context.getAttribute(name);
                if (value instanceof String) {
                    withAttribute(name, (String) value);
                } else {
                    withAttribute(name, value.toString());
                }
            }
        }
    }

    @Override
    protected void setAttributes(io.cloudevents.CloudEvent event) {
        if (event.getSpecVersion() == SpecVersion.V03) {
            CloudEventUtils.toContextReader(event).readAttributes(this);
        } else {
            CloudEventUtils.toContextReader(event).readAttributes(new V1ToV03AttributesConverter(this));
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

    public CloudEventBuilder withTime(OffsetDateTime time) {
        this.time = time;
        return this;
    }

    public CloudEventBuilder withSubject(String subject) {
        this.subject = subject;
        return this;
    }

    @Override
    public CloudEventBuilder withDataContentType(String dataContentType) {
        this.datacontenttype = dataContentType;
        return this;
    }

    public CloudEventBuilder withSchemaUrl(URI schemaUrl) {
        this.schemaurl = schemaUrl;
        return this;
    }

    @Override
    public CloudEventBuilder withDataSchema(URI dataSchema) {
        this.schemaurl = dataSchema;
        return this;
    }

    @Override
    public CloudEventV03 build() {
        if (id == null) {
            throw createMissingAttributeException("id");
        }
        if (source == null) {
            throw createMissingAttributeException("source");
        }
        if (type == null) {
            throw createMissingAttributeException("type");
        }

        return new CloudEventV03(id, source, type, time, schemaurl, datacontenttype, subject, this.data, this.extensions);
    }

    @Override
    public CloudEventBuilder newBuilder() {
        CloudEventBuilder newBuilder = new CloudEventBuilder();
        newBuilder.id = this.id;
        newBuilder.source = this.source;
        newBuilder.type = this.type;
        newBuilder.time = this.time;
        newBuilder.schemaurl = this.schemaurl;
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
            case "id":
                withId(value);
                return this;
            case "source":
                try {
                    withSource(new URI(value));
                } catch (URISyntaxException e) {
                    throw CloudEventRWException.newInvalidAttributeValue("source", value, e);
                }
                return this;
            case "type":
                withType(value);
                return this;
            case "datacontenttype":
                withDataContentType(value);
                return this;
            case "datacontentencoding":
                // No-op, this information is not saved in the event because it's useful only for parsing
                return this;
            case "schemaurl":
                try {
                    withSchemaUrl(new URI(value));
                } catch (URISyntaxException e) {
                    throw CloudEventRWException.newInvalidAttributeValue("schemaurl", value, e);
                }
                return this;
            case "subject":
                withSubject(value);
                return this;
            case "time":
                withTime(Time.parseTime("time", value));
                return this;
        }
        throw CloudEventRWException.newInvalidAttributeName(name);
    }

    @Override
    public CloudEventBuilder withAttribute(String name, URI value) throws CloudEventRWException {
        switch (name) {
            case "source":
                withSource(value);
                return this;
            case "schemaurl":
                withDataSchema(value);
                return this;
        }
        throw CloudEventRWException.newInvalidAttributeType(name, URI.class);
    }

    @Override
    public CloudEventBuilder withAttribute(String name, OffsetDateTime value) throws CloudEventRWException {
        if ("time".equals(name)) {
            withTime(value);
            return this;
        }
        throw CloudEventRWException.newInvalidAttributeType(name, OffsetDateTime.class);
    }
}
