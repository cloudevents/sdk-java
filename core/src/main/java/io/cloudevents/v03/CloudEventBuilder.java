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
package io.cloudevents.v03;

import io.cloudevents.CloudEventVisitException;
import io.cloudevents.SpecVersion;
import io.cloudevents.impl.BaseCloudEventBuilder;
import io.cloudevents.impl.CloudEventUtils;
import io.cloudevents.types.Time;

import java.net.URI;
import java.net.URISyntaxException;
import java.time.ZonedDateTime;
import java.time.format.DateTimeParseException;

/**
 * The event builder.
 *
 * @author fabiojose
 */
public final class CloudEventBuilder extends BaseCloudEventBuilder<CloudEventBuilder, CloudEventV03> {

    private String id;
    private URI source;
    private String type;
    private ZonedDateTime time;
    private URI schemaurl;
    private String datacontenttype;
    private String subject;

    public CloudEventBuilder() {
        super();
    }

    public CloudEventBuilder(io.cloudevents.CloudEvent event) {
        super(event);
    }

    @Override
    protected void setAttributes(io.cloudevents.CloudEvent event) {
        if (event.getSpecVersion() == SpecVersion.V03) {
            CloudEventUtils.toVisitable(event).visitAttributes(this);
        } else {
            CloudEventUtils.toVisitable(event).visitAttributes(new V1ToV03AttributesConverter(this));
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

    public CloudEventBuilder withTime(ZonedDateTime time) {
        this.time = time;
        return this;
    }

    public CloudEventBuilder withSubject(String subject) {
        this.subject = subject;
        return this;
    }

    @Override
    public CloudEventBuilder withDataContentType(String contentType) {
        this.datacontenttype = contentType;
        return this;
    }

    public CloudEventBuilder withSchemaUrl(URI schemaUrl) {
        this.schemaurl = schemaUrl;
        return this;
    }

    @Override
    protected CloudEventBuilder withDataSchema(URI dataSchema) {
        this.schemaurl = dataSchema;
        return this;
    }

    @Override
    public CloudEventV03 build() {
        return new CloudEventV03(id, source, type, time, schemaurl, datacontenttype, subject, this.data, this.extensions);
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
            case "datacontentencoding":
                // No-op, this information is not saved in the event because it's useful only for parsing
                return;
            case "schemaurl":
                try {
                    withSchemaUrl(new URI(value));
                } catch (URISyntaxException e) {
                    throw CloudEventVisitException.newInvalidAttributeValue("schemaurl", value, e);
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
            case "schemaurl":
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
