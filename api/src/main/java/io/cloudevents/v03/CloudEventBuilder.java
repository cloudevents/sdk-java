/**
 * Copyright 2019 The CloudEvents Authors
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package io.cloudevents.v03;

import io.cloudevents.Attributes;
import io.cloudevents.CloudEvent;
import io.cloudevents.impl.BaseCloudEventBuilder;
import io.cloudevents.message.MessageVisitException;
import io.cloudevents.types.Time;

import java.net.URI;
import java.net.URISyntaxException;
import java.time.ZonedDateTime;
import java.time.format.DateTimeParseException;


/**
 * The event builder.
 *
 * @author fabiojose
 *
 */
public final class CloudEventBuilder extends BaseCloudEventBuilder<CloudEventBuilder, AttributesImpl>  {

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

    public CloudEventBuilder(CloudEvent event) {
        super(event);
    }

    @Override
    protected void setAttributes(Attributes attributes) {
        AttributesImpl attr = (AttributesImpl) attributes.toV03();
        this
            .withId(attr.getId())
            .withSource(attr.getSource())
            .withType(attr.getType());
        attr.getDataContentType().ifPresent(this::withDataContentType);
        attr.getSchemaUrl().ifPresent(this::withSchemaUrl);
        attr.getSubject().ifPresent(this::withSubject);
        attr.getTime().ifPresent(this::withTime);
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
	protected AttributesImpl buildAttributes() {
		return new AttributesImpl(id, source, type, time, schemaurl, datacontenttype, subject);
	}

	// Message impl

    @Override
    public void setAttribute(String name, String value) throws MessageVisitException {
        switch (name) {
            case "id":
                withId(value);
                return;
            case "source":
                try {
                    withSource(new URI(value));
                } catch (URISyntaxException e) {
                    throw MessageVisitException.newInvalidAttributeValue("source", value, e);
                }
                return;
            case "type":
                withType(value);
                return;
            case "datacontenttype":
                withDataContentType(value);
                return;
            case "schemaurl":
                try {
                    withSchemaUrl(new URI(value));
                } catch (URISyntaxException e) {
                    throw MessageVisitException.newInvalidAttributeValue("schemaurl", value, e);
                }
                return;
            case "subject":
                withSubject(value);
                return;
            case "time":
                try {
                    withTime(Time.parseTime(value));
                } catch (DateTimeParseException e) {
                    throw MessageVisitException.newInvalidAttributeValue("time", value, e);
                }
                return;
        }
        throw MessageVisitException.newInvalidAttributeName(name);
    }

    @Override
    public void setAttribute(String name, URI value) throws MessageVisitException {
        switch (name) {
            case "source":
                withSource(value);
                return;
            case "schemaurl":
                withDataSchema(value);
                return;
        }
        throw MessageVisitException.newInvalidAttributeType(name, URI.class);
    }

    @Override
    public void setAttribute(String name, ZonedDateTime value) throws MessageVisitException {
        if ("time".equals(name)) {
            withTime(value);
            return;
        }
        throw MessageVisitException.newInvalidAttributeType(name, ZonedDateTime.class);
    }
}
