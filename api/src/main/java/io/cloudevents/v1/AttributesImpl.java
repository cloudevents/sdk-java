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

import static java.time.format.DateTimeFormatter.ISO_ZONED_DATE_TIME;

import java.net.URI;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Pattern;
import javax.validation.constraints.Size;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonInclude.Include;
import com.fasterxml.jackson.annotation.JsonProperty;

import io.cloudevents.Attributes;
import io.cloudevents.format.DateTimeFormat;

/**
 * 
 * @author fabiojose
 * @author dturanski
 * @version 1.0
 */
@JsonInclude(value = Include.NON_ABSENT)
public class AttributesImpl implements Attributes {
	
	@NotBlank
	private final String id;
	
	@NotNull
	private final URI source;
	
	@NotBlank
	@Pattern(regexp = "1\\.0")
	private final String specversion;
	
	@NotBlank
	private final String type;
	
	private final String datacontenttype;
	
	private final URI dataschema;
	
	@Size(min = 1)
	private final String subject;

	@DateTimeFormat
	@JsonProperty("time")
	private final String time;

	public AttributesImpl(String id, URI source, String specversion,
			String type, String datacontenttype,
			URI dataschema, String subject, ZonedDateTime time) {
		
		this.id = id;
		this.source = source;
		this.specversion = specversion;
		this.type = type;
		this.datacontenttype = datacontenttype;
		this.dataschema = dataschema;
		this.subject = subject;
		this.time = formatZonedDateTime(time);
	}

	public AttributesImpl(String id, URI source, String specversion,
						  String type, String datacontenttype,
						  URI dataschema, String subject, String time) {
		this.id = id;
		this.source = source;
		this.specversion = specversion;
		this.type = type;
		this.datacontenttype = datacontenttype;
		this.dataschema = dataschema;
		this.subject = subject;
		this.time = time;
	}
	
	@Override
	public Optional<String> getMediaType() {
		return getDatacontenttype();
	}

	public String getId() {
		return id;
	}

	public URI getSource() {
		return source;
	}

	public String getSpecversion() {
		return specversion;
	}

	public String getType() {
		return type;
	}

	public Optional<String> getDatacontenttype() {
		return Optional.ofNullable(datacontenttype);
	}

	public Optional<URI> getDataschema() {
		return Optional.ofNullable(dataschema);
	}

	public Optional<String> getSubject() {
		return Optional.ofNullable(subject);
	}

	@JsonIgnore
	public Optional<ZonedDateTime> getTime() {
		return Optional.ofNullable(parseZonedDateTime(time));
	}

	@Override
	public String toString() {
		return "AttibutesImpl [id=" + id + ", source=" + source 
				+ ", specversion=" + specversion + ", type=" + type
				+ ", datacontenttype=" + datacontenttype + ", dataschema=" 
				+ dataschema + ", subject=" + subject
				+ ", time=" + time + "]";
	}
	
	/**
	 * Used by the Jackson framework to unmarshall.
	 */
	@JsonCreator
	public static AttributesImpl build(
			@JsonProperty("id") String id,
			@JsonProperty("source") URI source,
			@JsonProperty("specversion") String specversion,
			@JsonProperty("type") String type,
			@JsonProperty("datacontenttype") String datacontenttype,
			@JsonProperty("dataschema") URI dataschema,
			@JsonProperty("subject") String subject,
			@JsonProperty("time") String time) {
		
		return new AttributesImpl(id, source, specversion, type,
				datacontenttype, dataschema, subject, time);
	}
	
	/**
	 * Creates the marshaller instance to marshall {@link AttributesImpl} as 
	 * a {@link Map} of strings
	 */
	public static Map<String, String> marshal(AttributesImpl attributes) {
		Objects.requireNonNull(attributes);
		Map<String, String> result = new HashMap<>();
		
		result.put(ContextAttributes.id.name(),
				attributes.getId());
		result.put(ContextAttributes.source.name(),
				attributes.getSource().toString());
		result.put(ContextAttributes.specversion.name(),
				attributes.getSpecversion());
		result.put(ContextAttributes.type.name(),
				attributes.getType());
		
		attributes.getDatacontenttype().ifPresent(dct -> result.put(ContextAttributes.datacontenttype.name(), dct));
		attributes.getDataschema().ifPresent(dataschema -> result.put(ContextAttributes.dataschema.name(),
																  dataschema.toString()));
		attributes.getSubject().ifPresent(subject -> result.put(ContextAttributes.subject.name(), subject));
		attributes.getTime().ifPresent(time -> result.put(ContextAttributes.time.name(),
													  time.format(DateTimeFormatter.ISO_OFFSET_DATE_TIME)));
		
		return result;
	}
	
	/**
	 * The attribute unmarshaller for the binary format, that receives a
	 * {@code Map} with attributes names as String and value as String.
	 */
	public static AttributesImpl unmarshal(Map<String, String> attributes) {
		String type = attributes.get(ContextAttributes.type.name());
		String time = attributes.get(ContextAttributes.time.name());
		
		String specversion = attributes.get(ContextAttributes.specversion.name()); 
		URI source = URI.create(attributes.get(ContextAttributes.source.name()));
		
		URI dataschema = 
			Optional.ofNullable(attributes.get(ContextAttributes.dataschema.name()))
			.map(URI::create)
			.orElse(null);
		
		String id = attributes.get(ContextAttributes.id.name());
		
		String datacontenttype = 
			attributes.get(ContextAttributes.datacontenttype.name());
		
		String subject = attributes.get(ContextAttributes.subject.name());
		
		return AttributesImpl.build(id, source, specversion, type,
				datacontenttype, dataschema, subject, time);
	}

	static String formatZonedDateTime(ZonedDateTime zonedDateTime) {
		return zonedDateTime == null? null :zonedDateTime.format(ISO_ZONED_DATE_TIME);
	}

	static ZonedDateTime parseZonedDateTime(String zonedDateTime) {
		return zonedDateTime == null ? null : ZonedDateTime.parse(zonedDateTime);
	}
}
