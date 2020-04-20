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
import io.cloudevents.SpecVersion;

import java.net.URI;
import java.time.ZonedDateTime;
import java.util.Optional;

/**
 * The event attributes implementation for v0.3
 *
 * @author fabiojose
 * @author slinkydeveloper
 *
 */
public class AttributesImpl implements Attributes {

	private final String id;
	private final URI source;
	private final String type;
	private final ZonedDateTime time;
	private final URI schemaurl;
	private final String datacontenttype;
	private final String subject;

	public AttributesImpl(String id, URI source, String type,
                          ZonedDateTime time, URI schemaurl,
                          String datacontenttype, String subject) {
		this.id = id;
		this.source = source;
		this.type = type;

		this.time = time;
		this.schemaurl = schemaurl;
		this.datacontenttype = datacontenttype;
		this.subject = subject;
	}

	public String getId() {
		return id;
	}
	public URI getSource() {
		return source;
	}
	public SpecVersion getSpecVersion() {
		return SpecVersion.V03;
	}
	public String getType() {
		return type;
	}
	public Optional<ZonedDateTime> getTime() {
		return Optional.ofNullable(time);
	}

    @Override
    public Attributes toV03() {
        return this;
    }

    @Override
    public Attributes toV1() {
        return new io.cloudevents.v1.AttributesImpl(
            this.id,
            this.source,
            this.type,
            this.datacontenttype,
            this.schemaurl,
            this.subject,
            this.time
        );
    }

    public Optional<URI> getDataSchema() {
        return getSchemaUrl();
    }
	public Optional<URI> getSchemaUrl() {
		return Optional.ofNullable(schemaurl);
	}
	public Optional<String> getDataContentType() {
        return Optional.ofNullable(datacontenttype);
	}
    public Optional<String> getSubject() {
		return Optional.ofNullable(subject);
	}

	@Override
	public String toString() {
		return "AttributesImpl [id=" + id + ", source=" + source
				+ ", specversion=" + SpecVersion.V03 + ", type=" + type
				+ ", time=" + time + ", schemaurl=" + schemaurl
				+ ", datacontenttype=" + datacontenttype + ", subject="
				+ subject + "]";
	}
//
//	/**
//	 * Used by the Jackson framework to unmarshall.
//	 */
//	@JsonCreator
//	public static AttributesImpl build(
//			@JsonProperty("id") String id,
//			@JsonProperty("source") URI source,
//			@JsonProperty("type") String type,
//			@JsonProperty("time") ZonedDateTime time,
//			@JsonProperty("schemaurl") URI schemaurl,
//			@JsonProperty("datacontenttype") String datacontenttype,
//			@JsonProperty("subject") String subject) {
//
//		return new AttributesImpl(id, source, type, time,
//				schemaurl, datacontenttype, subject);
//	}
//
//	/**
//	 * Creates the marshaller instance to marshall {@link AttributesImpl} as
//	 * a {@link Map} of strings
//	 */
//	public static Map<String, String> marshal(AttributesImpl attributes) {
//		Objects.requireNonNull(attributes);
//
//		Map<String, String> result = new HashMap<>();
//
//		result.put(ContextAttributes.TYPE.name(),
//				attributes.getType());
//		result.put(ContextAttributes.SPECVERSION.name(),
//				attributes.getSpecVersion());
//		result.put(ContextAttributes.SOURCE.name(),
//				attributes.getSource().toString());
//		result.put(ContextAttributes.ID.name(),
//				attributes.getId());
//
//		attributes.getTime().ifPresent((value) -> result.put(ContextAttributes.TIME.name(),
//														 value.format(DateTimeFormatter.ISO_OFFSET_DATE_TIME)));
//		attributes.getSchemaurl().ifPresent((schema) -> result.put(ContextAttributes.SCHEMAURL.name(),
//															   schema.toString()));
//		attributes.getDatacontenttype().ifPresent((ct) -> result.put(ContextAttributes.DATACONTENTTYPE.name(), ct));
//		attributes.getDatacontentencoding().ifPresent(dce -> result.put(ContextAttributes.DATACONTENTENCODING.name(), dce));
//		attributes.getSubject().ifPresent(subject -> result.put(ContextAttributes.SUBJECT.name(), subject));
//
//		return result;
//	}
//
//	/**
//	 * The attribute unmarshaller for the binary format, that receives a
//	 * {@code Map} with attributes names as String and value as String.
//	 */
//	public static AttributesImpl unmarshal(Map<String, String> attributes) {
//		String type = attributes.get(ContextAttributes.TYPE.name());
//		ZonedDateTime time =
//			Optional.ofNullable(attributes.get(ContextAttributes.TIME.name()))
//			.map((t) -> ZonedDateTime.parse(t,
//					ISO_ZONED_DATE_TIME))
//			.orElse(null);
//
//		String specversion = attributes.get(ContextAttributes.SPECVERSION.name());
//		URI source = URI.create(attributes.get(ContextAttributes.SOURCE.name()));
//
//		URI schemaurl =
//			Optional.ofNullable(attributes.get(ContextAttributes.SCHEMAURL.name()))
//			.map(URI::create)
//			.orElse(null);
//
//		String id = attributes.get(ContextAttributes.ID.name());
//
//		String datacontenttype =
//			attributes.get(ContextAttributes.DATACONTENTTYPE.name());
//
//		String datacontentencoding =
//			attributes.get(ContextAttributes.DATACONTENTENCODING.name());
//
//		String subject = attributes.get(ContextAttributes.SUBJECT.name());
//
//		return AttributesImpl.build(id, source, specversion, type,
//				time, schemaurl, datacontentencoding,
//				datacontenttype, subject);
//	}
}
