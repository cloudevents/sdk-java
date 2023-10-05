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
import io.cloudevents.core.validator.CloudEventValidator;
import io.cloudevents.rw.CloudEventContextReader;
import io.cloudevents.rw.CloudEventContextWriter;
import io.cloudevents.rw.CloudEventRWException;
import io.cloudevents.types.Time;

import java.net.URI;
import java.net.URISyntaxException;
import java.time.OffsetDateTime;

import static io.cloudevents.core.v1.CloudEventV1.*;

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
            contextReader.readContext(this);
        } else {
            contextReader.readContext(new V03ToV1AttributesConverter(this));
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

    public CloudEventBuilder withTime(OffsetDateTime time) {
        this.time = time;
        return this;
    }

    @Override
    public CloudEvent build() {
        if (id == null) {
            throw createMissingAttributeException(ID);
        }
        if (source == null) {
            throw createMissingAttributeException(SOURCE);
        }
        if (type == null) {
            throw createMissingAttributeException(TYPE);
        }

        CloudEvent cloudEvent = new CloudEventV1(id, source, type, datacontenttype, dataschema, subject, time, this.data, this.extensions);

        String name = null;
        try{
            // check if the header.validator.class is set as a system property,
            if ((name = System.getProperty("header.validator.class")) != null){
                Class<?> dynamicClass  = Class.forName(name);
                Object dynamicObject = dynamicClass.newInstance();

                if (dynamicObject instanceof CloudEventValidator) {
                    // pluggable implementation of validation implementation
                    CloudEventValidator cloudEventValidator = (CloudEventValidator) dynamicObject;

                    cloudEventValidator.validate(cloudEvent);
                }
                else {
                    throw new IllegalArgumentException("Passed class is not an instance of CloudEventValidator");
                }
            }
        }
        catch (ClassNotFoundException | InstantiationException | IllegalAccessException e) {
            throw new IllegalArgumentException("Unable to load the header.validator.class passed as vm argument = " + name + ". Please check the classpath", e);
        }

        return cloudEvent;
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
            case DATASCHEMA:
                try {
                    withDataSchema(new URI(value));
                } catch (URISyntaxException e) {
                    throw CloudEventRWException.newInvalidAttributeValue(DATASCHEMA, value, e);
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
            case DATASCHEMA:
                withDataSchema(value);
                return this;
            case ID:
            case TYPE:
            case DATACONTENTTYPE:
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
            case DATASCHEMA:
            case ID:
            case TYPE:
            case DATACONTENTTYPE:
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
            case DATASCHEMA:
            case ID:
            case TYPE:
            case DATACONTENTTYPE:
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
            case DATASCHEMA:
            case ID:
            case TYPE:
            case DATACONTENTTYPE:
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
            case DATASCHEMA:
            case ID:
            case TYPE:
            case DATACONTENTTYPE:
            case SUBJECT:
            case SOURCE:
                throw CloudEventRWException.newInvalidAttributeType(name, Boolean.class);
            default:
                withExtension(name, value);
                return this;
        }
    }

    @Override
    public CloudEventContextWriter withContextAttribute(String name, byte[] value)
        throws CloudEventRWException {
        requireValidAttributeWrite(name);
        switch (name) {
            case TIME:
            case DATASCHEMA:
            case ID:
            case TYPE:
            case DATACONTENTTYPE:
            case SUBJECT:
            case SOURCE:
                throw CloudEventRWException.newInvalidAttributeType(name, byte[].class);
            default:
                withExtension(name, value);
                return this;
        }
    }
}
