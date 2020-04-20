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
package io.cloudevents.v1;

import io.cloudevents.Attributes;
import io.cloudevents.SpecVersion;
import io.cloudevents.impl.AttributesInternal;
import io.cloudevents.message.BinaryMessageAttributesVisitor;
import io.cloudevents.message.MessageVisitException;

import java.net.URI;
import java.time.ZonedDateTime;
import java.util.Optional;

/**
 *
 * @author fabiojose
 * @author slinkydeveloper
 * @version 1.0
 */
public final class AttributesImpl implements AttributesInternal {

	private final String id;
	private final URI source;
	private final String type;
	private final String datacontenttype;
	private final URI dataschema;
	private final String subject;
	private final ZonedDateTime time;

	public AttributesImpl(String id, URI source,
			String type, String datacontenttype,
			URI dataschema, String subject, ZonedDateTime time) {

		this.id = id;
		this.source = source;
		this.type = type;
		this.datacontenttype = datacontenttype;
		this.dataschema = dataschema;
		this.subject = subject;
		this.time = time;
	}

    public SpecVersion getSpecVersion() {
        return SpecVersion.V1;
    }

    public String getId() {
		return id;
	}

	public URI getSource() {
		return source;
	}

	public String getType() {
		return type;
	}

    @Override
    public Optional<String> getDataContentType() {
        return Optional.ofNullable(datacontenttype);
    }

    @Override
    public Optional<URI> getDataSchema() {
        return Optional.ofNullable(dataschema);
    }

	public Optional<String> getSubject() {
		return Optional.ofNullable(subject);
	}

	public Optional<ZonedDateTime> getTime() {
		return Optional.ofNullable(time);
	}

    @Override
    public Attributes toV03() {
        return new io.cloudevents.v03.AttributesImpl(
            this.id,
            this.source,
            this.type,
            this.time,
            this.dataschema,
            this.datacontenttype,
            this.subject
        );
    }

    @Override
    public Attributes toV1() {
        return this;
    }

    @Override
    public void visit(BinaryMessageAttributesVisitor visitor) throws MessageVisitException {
        visitor.setAttribute(
            ContextAttributes.ID.name().toLowerCase(),
            this.id
        );
        visitor.setAttribute(
            ContextAttributes.SOURCE.name().toLowerCase(),
            this.source
        );
        visitor.setAttribute(
            ContextAttributes.TYPE.name().toLowerCase(),
            this.type
        );
        if (this.datacontenttype != null) {
            visitor.setAttribute(
                ContextAttributes.DATACONTENTTYPE.name().toLowerCase(),
                this.datacontenttype
            );
        }
        if (this.dataschema != null) {
            visitor.setAttribute(
                ContextAttributes.DATASCHEMA.name().toLowerCase(),
                this.dataschema
            );
        }
        if (this.subject != null) {
            visitor.setAttribute(
                ContextAttributes.SUBJECT.name().toLowerCase(),
                this.subject
            );
        }
        if (this.time != null) {
            visitor.setAttribute(
                ContextAttributes.TYPE.name().toLowerCase(),
                this.time
            );
        }
    }

    @Override
    public String toString() {
        return "Attibutes V1.0 [id=" + id + ", source=" + source
            + ", type=" + type
            + ", datacontenttype=" + datacontenttype + ", dataschema="
            + dataschema + ", subject=" + subject
            + ", time=" + time + "]";
    }

//	/**
//	 * Used by the Jackson framework to unmarshall.
//	 */
//	@JsonCreator
//	public static AttributesImpl build(
//			@JsonProperty("id") String id,
//			@JsonProperty("source") URI source,
//			@JsonProperty("type") String type,
//			@JsonProperty("datacontenttype") String datacontenttype,
//			@JsonProperty("dataschema") URI dataschema,
//			@JsonProperty("subject") String subject,
//			@JsonProperty("time") ZonedDateTime time) {
//
//		return new AttributesImpl(id, source, type,
//				datacontenttype, dataschema, subject, time);
//	}
//
//	/**
//	 * Creates the marshaller instance to marshall {@link AttributesImpl} as
//	 * a {@link Map} of strings
//	 */
//	public static Map<String, String> marshal(AttributesImpl attributes) {
//		Objects.requireNonNull(attributes);
//		Map<String, String> result = new HashMap<>();
//
//		result.put(ContextAttributes.ID.name(),
//				attributes.getId());
//		result.put(ContextAttributes.SOURCE.name(),
//				attributes.getSource().toString());
//		result.put(ContextAttributes.TYPE.name(),
//				attributes.getType());
//
//		attributes.getDatacontenttype().ifPresent(dct -> result.put(ContextAttributes.DATACONTENTTYPE.name(), dct));
//		attributes.getDataschema().ifPresent(dataschema -> result.put(ContextAttributes.DATASCHEMA.name(),
//																  dataschema.toString()));
//		attributes.getSubject().ifPresent(subject -> result.put(ContextAttributes.SUBJECT.name(), subject));
//		attributes.getTime().ifPresent(time -> result.put(ContextAttributes.TIME.name(),
//													  time.format(DateTimeFormatter.ISO_OFFSET_DATE_TIME)));
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
//		URI dataschema =
//			Optional.ofNullable(attributes.get(ContextAttributes.DATASCHEMA.name()))
//			.map(URI::create)
//			.orElse(null);
//
//		String id = attributes.get(ContextAttributes.ID.name());
//
//		String datacontenttype =
//			attributes.get(ContextAttributes.DATACONTENTTYPE.name());
//
//		String subject = attributes.get(ContextAttributes.SUBJECT.name());
//
//		return AttributesImpl.build(id, source, type,
//				datacontenttype, dataschema, subject, time);
//	}
}
