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

import io.cloudevents.impl.BaseCloudEventBuilder;

import java.net.URI;
import java.time.ZonedDateTime;


/**
 * The event builder.
 *
 * @author fabiojose
 *
 */
public final class CloudEventBuilder extends BaseCloudEventBuilder<CloudEventBuilder, AttributesImpl>  {

	public CloudEventBuilder() {
		super();
	}

	private String id;
	private URI source;

	private String type;

	private ZonedDateTime time;
	private URI schemaurl;
	private String datacontenttype;
	private String subject;

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
}
