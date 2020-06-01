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
import io.cloudevents.core.impl.BaseCloudEventBuilder;
import io.cloudevents.core.impl.CloudEventUtils;
import io.cloudevents.types.Time;
import io.cloudevents.visitor.CloudEventVisitException;

import java.net.URI;
import java.net.URISyntaxException;
import java.time.ZonedDateTime;
import java.time.format.DateTimeParseException;

/**
 * @author fabiojose
 * @author slinkydeveloper
 * @version 1.0
 */
public final class CloudEventBuilder extends BaseCloudEventBuilder<CloudEventBuilder, CloudEventV1> {

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
            CloudEventUtils.toVisitable(event).visitAttributes(this);
        } else {
            CloudEventUtils.toVisitable(event).visitAttributes(new V03ToV1AttributesConverter(this));
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
    public CloudEvent build() {
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

    // Message impl

    @Override
    public void setAttribute(String name, String value) throws CloudEventVisitException {
        switch (name) {
            case "id":
                withId(value);
                return;
            case "source":
                try {
                    withSource(new URI(value));
                } catch (URISyntaxException e) {
                    throw CloudEventVisitException.newInvalidAttributeValue("source", value, e);
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
                    throw CloudEventVisitException.newInvalidAttributeValue("dataschema", value, e);
                }
                return;
            case "subject":
                withSubject(value);
                return;
            case "time":
                try {
                    withTime(Time.parseTime(value));
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
                withSource(value);
                return;
            case "dataschema":
                withDataSchema(value);
                return;
        }
        throw CloudEventVisitException.newInvalidAttributeType(name, URI.class);
    }

    @Override
    public void setAttribute(String name, ZonedDateTime value) throws CloudEventVisitException {
        if ("time".equals(name)) {
            withTime(value);
            return;
        }
        throw CloudEventVisitException.newInvalidAttributeType(name, ZonedDateTime.class);
    }
}
