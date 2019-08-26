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
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;

import io.cloudevents.Attributes;
import io.cloudevents.fun.AttributeMarshaller;
import io.cloudevents.fun.AttributeUnmarshaller;
import io.cloudevents.json.ZonedDateTimeDeserializer;

/**
 * 
 * @author fabiojose
 *
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
	
	private final ZonedDateTime time;
	private final URI schemaurl;
	private final String contenttype;
	
	AttributesImpl(String type, String specversion, URI source,
			String id, ZonedDateTime time, URI schemaurl, String contenttype) {
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
	@JsonDeserialize(using = ZonedDateTimeDeserializer.class)
	public Optional<ZonedDateTime> getTime() {
		return Optional.ofNullable(time);
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

	@JsonCreator
	public static AttributesImpl build(
			@JsonProperty("id") String id,
			@JsonProperty("source") URI source,
			@JsonProperty("specversion") String specversion,
			@JsonProperty("type") String type,
			@JsonProperty("time") ZonedDateTime time,
			@JsonProperty("schemaurl") URI schemaurl,
			@JsonProperty("contenttype") String contenttype) {
		
		return new AttributesImpl(type, specversion, source, id, time,
				schemaurl, contenttype);
	}
	
	/**
	 * The attribute unmarshaller for the binary format, that receives a
	 * {@code Map} with attributes names as String and value as String.
	 */
	public static AttributeUnmarshaller<AttributesImpl> unmarshaller() {
		
		return new AttributeUnmarshaller<AttributesImpl>() {
			@Override
			public AttributesImpl unmarshal(Map<String, String> attributes) {
				String type = attributes.get(ContextAttributes.type.name());
				ZonedDateTime time =
					Optional.ofNullable(attributes.get(ContextAttributes.time.name()))
					.map((t) -> ZonedDateTime.parse(t,
							ISO_ZONED_DATE_TIME))
					.orElse(null);
				
				String specversion = attributes.get(ContextAttributes.specversion.name()); 
				URI source = URI.create(attributes.get(ContextAttributes.source.name()));
				
				URI schemaurl = 
					Optional.ofNullable(attributes.get(ContextAttributes.schemaurl.name()))
					.map(schema -> URI.create(schema))
					.orElse(null);
				
				String id = attributes.get(ContextAttributes.id.name());
				
				String contenttype = 
					attributes.get(ContextAttributes.contenttype.name());
		
				
				return AttributesImpl.build(id, source, specversion, type,
						time, schemaurl, contenttype);
			}
			
		};
	}
	
	/**
	 * Creates the marshaller instance to marshall {@link AttributesImpl} as 
	 * a {@link Map} of strings
	 */
	public static AttributeMarshaller<AttributesImpl> marshaller() {
		return new AttributeMarshaller<AttributesImpl>() {
			@Override
			public Map<String, String> marshal(AttributesImpl attributes) {
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
				
				attributes.getTime().ifPresent((value) -> {
					result.put(ContextAttributes.time.name(), 
						value.format(DateTimeFormatter.ISO_OFFSET_DATE_TIME));
				});
				
				attributes.getSchemaurl().ifPresent((schema) -> {
					result.put(ContextAttributes.schemaurl.name(),
							schema.toString());
				});
				
				attributes.getContenttype().ifPresent((ct) -> {
					result.put(ContextAttributes.contenttype.name(), ct);
				});

				return result;
			}
		};
	}
}
