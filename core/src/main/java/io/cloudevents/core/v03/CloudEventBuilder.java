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
import io.cloudevents.rw.CloudEventContextReader;
import io.cloudevents.rw.CloudEventContextWriter;
import io.cloudevents.rw.CloudEventRWException;
import io.cloudevents.types.Time;

import java.net.URI;
import java.net.URISyntaxException;
import java.time.OffsetDateTime;

import static io.cloudevents.core.v03.CloudEventV03.*;

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
        super(context);
    }

    @Override
    protected void setAttributes(io.cloudevents.CloudEventContext event) {
        CloudEventContextReader contextReader = CloudEventUtils.toContextReader(event);
        if (event.getSpecVersion() == SpecVersion.V03) {
            contextReader.readContext(this);
        } else {
            contextReader.readContext(new V1ToV03AttributesConverter(this));
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
    public CloudEventContextWriter withContextAttribute(String name, String value) throws CloudEventRWException {
        requireValidAttributeWrite(name);
        switch (name) {
            case ID:
                withId(value);
                return this;
            case SOURCE:
                try {
                    withSource(new URI(value));
                } catch (URISyntaxException e) {
                    throw CloudEventRWException.newInvalidAttributeValue(SOURCE, value, e);
                }
                return this;
            case TYPE:
                withType(value);
                return this;
            case DATACONTENTTYPE:
                withDataContentType(value);
                return this;
            case DATACONTENTENCODING:
                // No-op, this information is not saved in the event because it's useful only for parsing
                return this;
            case SCHEMAURL:
                try {
                    withSchemaUrl(new URI(value));
                } catch (URISyntaxException e) {
                    throw CloudEventRWException.newInvalidAttributeValue(SCHEMAURL, value, e);
                }
                return this;
            case SUBJECT:
                withSubject(value);
                return this;
            case TIME:
                withTime(Time.parseTime(TIME, value));
                return this;
            default:
                withExtension(name, value);
                return this;
        }
    }

    @Override
    public CloudEventContextWriter withContextAttribute(String name, URI value) throws CloudEventRWException {
        requireValidAttributeWrite(name);
        switch (name) {
            case SOURCE:
                withSource(value);
                return this;
            case SCHEMAURL:
                withDataSchema(value);
                return this;
            case ID:
            case TYPE:
            case DATACONTENTTYPE:
            case DATACONTENTENCODING:
            case SUBJECT:
            case TIME:
                throw CloudEventRWException.newInvalidAttributeType(name, URI.class);
            default:
                withExtension(name, value);
                return this;
        }
    }

    @Override
    public CloudEventContextWriter withContextAttribute(String name, OffsetDateTime value) throws CloudEventRWException {
        requireValidAttributeWrite(name);
        switch (name) {
            case TIME:
                withTime(value);
                return this;
            case SCHEMAURL:
            case ID:
            case TYPE:
            case DATACONTENTTYPE:
            case DATACONTENTENCODING:
            case SUBJECT:
            case SOURCE:
                throw CloudEventRWException.newInvalidAttributeType(name, OffsetDateTime.class);
            default:
                withExtension(name, value);
                return this;
        }
    }

    @Override
    public CloudEventContextWriter withContextAttribute(String name, Number value) throws CloudEventRWException {
        requireValidAttributeWrite(name);
        switch (name) {
            case TIME:
            case SCHEMAURL:
            case ID:
            case TYPE:
            case DATACONTENTTYPE:
            case DATACONTENTENCODING:
            case SUBJECT:
            case SOURCE:
                throw CloudEventRWException.newInvalidAttributeType(name, Number.class);
            default:
                withExtension(name, value);
                return this;
        }
    }

    @Override
    public CloudEventContextWriter withContextAttribute(String name, Integer value) throws CloudEventRWException
    {
        requireValidAttributeWrite(name);
        switch (name) {
            case TIME:
            case SCHEMAURL:
            case ID:
            case TYPE:
            case DATACONTENTTYPE:
            case DATACONTENTENCODING:
            case SUBJECT:
            case SOURCE:
                throw CloudEventRWException.newInvalidAttributeType(name, Integer.class);
            default:
                withExtension(name, value);
                return this;
        }
    }

    @Override
    public CloudEventContextWriter withContextAttribute(String name, Boolean value) throws CloudEventRWException {
        requireValidAttributeWrite(name);
        switch (name) {
            case TIME:
            case SCHEMAURL:
            case ID:
            case TYPE:
            case DATACONTENTTYPE:
            case DATACONTENTENCODING:
            case SUBJECT:
            case SOURCE:
                throw CloudEventRWException.newInvalidAttributeType(name, Boolean.class);
            default:
                withExtension(name, value);
                return this;
        }
    }

    @Override
    public CloudEventContextWriter withContextAttribute(String name, byte[] value) throws CloudEventRWException {
        requireValidAttributeWrite(name);
        switch (name) {
            case TIME:
            case SCHEMAURL:
            case ID:
            case TYPE:
            case DATACONTENTTYPE:
            case DATACONTENTENCODING:
            case SUBJECT:
            case SOURCE:
                throw CloudEventRWException.newInvalidAttributeType(name, byte[].class);
            default:
                withExtension(name, value);
                return this;
        }
    }
}
