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
package io.cloudevents.v02;

import static java.time.format.DateTimeFormatter.ISO_ZONED_DATE_TIME;

import com.fasterxml.jackson.annotation.JsonIgnore;
import io.cloudevents.format.DateTimeFormat;
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

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonInclude.Include;

import io.cloudevents.Attributes;

/**
 * 
 * @author fabiojose
 * @author dturanski
 * @version 0.2
 */
@JsonInclude(value = Include.NON_ABSENT)
public class AttributesImpl implements Attributes {
	
	@NotBlank
	private final String type;
	
	@NotBlank
	@Pattern(regexp = "0\\.2")
	private final String specversion;
	
	@NotNull
	private final URI source;
	
	@NotBlank
	private final String id;

	@DateTimeFormat
	@JsonProperty("time")
	private final String time;
	private final URI schemaurl;
	private final String contenttype;
	
	
	AttributesImpl(String type, String specversion, URI source,
			String id, String time, URI schemaurl, String contenttype) {
		this.type = type;
		this.specversion = specversion;
		this.source = source;
		this.id = id;
		this.time = time;
		this.schemaurl = schemaurl;
		this.contenttype = contenttype;
	}

	/**
     * Type of occurrence which has happened. Often this property is used for
     * routing, observability, policy enforcement, etc.
     */
	public String getType() {
		return type;
	}
	
	/**
     * ID of the event. The semantics of this string are explicitly
     * undefined to ease the implementation of producers. Enables
     * deduplication.
     */
	public String getId() {
		return id;
	}
	
	/**
     * The version of the CloudEvents specification which the event uses.
     * This enables the interpretation of the context.
     */
	public String getSpecversion() {
		return specversion;
	}
	
	/**
     * This describes the event producer. Often this will include
     * information such as the type of the event source, the organization
     * publishing the event, and some unique identifiers.
     * The exact syntax and semantics behind the data encoded in the URI
     * is event producer defined.
     */
	public URI getSource() {
		return source;
	}

	/**
     * Timestamp of when the event happened.
     */
	@JsonIgnore
	public Optional<ZonedDateTime> getTime() {
		return Optional.ofNullable(parseZonedDateTime(time));
	}
	
	/**
     * A link to the schema that the data attribute adheres to.
     */
	public Optional<URI> getSchemaurl() {
		return Optional.ofNullable(schemaurl);
	}
	
	/**
     * Describe the data encoding format
     */
	public Optional<String> getContenttype() {
		return Optional.ofNullable(contenttype);
	}
	
	/**
	 * {@inheritDoc}
	 */
	public Optional<String> getMediaType() {
		return getContenttype();
	}

	@JsonCreator
	public static AttributesImpl build(
			@JsonProperty("id") String id,
			@JsonProperty("source") URI source,
			@JsonProperty("specversion") String specversion,
			@JsonProperty("type") String type,
			@JsonProperty("time") String time,
			@JsonProperty("schemaurl") URI schemaurl,
			@JsonProperty("contenttype") String contenttype) {
		
		return new AttributesImpl(type, specversion, source, id, time,
				schemaurl, contenttype);
	}
	
	/**
	 * The attribute unmarshaller for the binary format, that receives a
	 * {@code Map} with attributes names as String and values as String.
	 */
	public static AttributesImpl unmarshal(Map<String, String> attributes) {
		String type = attributes.get(ContextAttributes.type.name());
		String time = attributes.get(ContextAttributes.time.name());
		
		String specversion = attributes.get(ContextAttributes.specversion.name()); 
		URI source = URI.create(attributes.get(ContextAttributes.source.name()));
		
		URI schemaurl = 
			Optional.ofNullable(attributes.get(ContextAttributes.schemaurl.name()))
			.map(URI::create)
			.orElse(null);
		
		String id = attributes.get(ContextAttributes.id.name());
		
		String contenttype = 
			attributes.get(ContextAttributes.contenttype.name());

		
		return AttributesImpl.build(id, source, specversion, type,
				time, schemaurl, contenttype);
	}
	
	/**
	 * Creates the marshaller instance to marshall {@link AttributesImpl} as 
	 * a {@link Map} of strings
	 */
	public static Map<String, String> marshal(AttributesImpl attributes) {
		Objects.requireNonNull(attributes);
		
		Map<String, String> result = new HashMap<>();

		result.put(ContextAttributes.type.name(), 
				attributes.getType());
		result.put(ContextAttributes.specversion.name(),
				attributes.getSpecversion());
		result.put(ContextAttributes.source.name(),
				attributes.getSource().toString());
		result.put(ContextAttributes.id.name(),
				attributes.getId());
		
		attributes.getTime().ifPresent((value) -> result.put(ContextAttributes.time.name(),
														 value.format(DateTimeFormatter.ISO_OFFSET_DATE_TIME)));
		attributes.getSchemaurl().ifPresent((schema) -> result.put(ContextAttributes.schemaurl.name(),
															   schema.toString()));
		attributes.getContenttype().ifPresent((ct) -> result.put(ContextAttributes.contenttype.name(), ct));

		return result;
	}

	static String formatZonedDateTime(ZonedDateTime zonedDateTime) {
		return zonedDateTime == null? null :zonedDateTime.format(ISO_ZONED_DATE_TIME);
	}

	static ZonedDateTime parseZonedDateTime(String zonedDateTime) {
		return zonedDateTime == null ? null : ZonedDateTime.parse(zonedDateTime);
	}
}
