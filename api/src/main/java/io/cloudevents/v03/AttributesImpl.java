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

import static java.time.format.DateTimeFormatter.ISO_ZONED_DATE_TIME;

import java.net.URI;
import java.time.ZonedDateTime;
import java.util.Map;
import java.util.Optional;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Pattern;
import javax.validation.constraints.Size;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonInclude.Include;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;

import io.cloudevents.Attributes;
import io.cloudevents.fun.AttributeUnmarshaller;
import io.cloudevents.json.ZonedDateTimeDeserializer;

/**
 * The event attributes implementation for v0.3
 * 
 * @author fabiojose
 *
 */
@JsonInclude(value = Include.NON_ABSENT)
public class AttributesImpl implements Attributes {
	
	@NotBlank
	private final String id;
	
	@NotNull
	private final URI source;
	
	@NotBlank
	@Pattern(regexp = "0\\.3")
	private final String specversion;
	
	@NotBlank
	private final String type;
	
	@JsonDeserialize(using = ZonedDateTimeDeserializer.class)
	private final ZonedDateTime time;
	private final URI schemaurl;
	
	@Pattern(regexp =  "base64")
	private final String datacontentencoding;
	private final String datacontenttype;
	
	@Size(min = 1)
	private final String subject;
	
	AttributesImpl(String id, URI source, String specversion, String type,
			ZonedDateTime time, URI schemaurl, String datacontentencoding,
			String datacontenttype, String subject) {
		this.id = id;
		this.source = source;
		this.specversion = specversion;
		this.type = type;
		
		this.time = time;
		this.schemaurl = schemaurl;
		this.datacontentencoding = datacontentencoding;
		this.datacontenttype = datacontenttype;
		this.subject = subject;
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
	public Optional<ZonedDateTime> getTime() {
		return Optional.ofNullable(time);
	}
	public Optional<URI> getSchemaurl() {
		return Optional.ofNullable(schemaurl);
	}
	public Optional<String> getDatacontentencoding() {
		return Optional.ofNullable(datacontentencoding);
	}
	public Optional<String> getDatacontenttype() {
		return Optional.ofNullable(datacontenttype);
	}
	/**
	 * {@inheritDoc}
	 */
	public Optional<String> getMediaType() {
		return getDatacontenttype();
	}
	public Optional<String> getSubject() {
		return Optional.ofNullable(subject);
	}
	
	@Override
	public String toString() {
		return "AttributesImpl [id=" + id + ", source=" + source 
				+ ", specversion=" + specversion + ", type=" + type 
				+ ", time=" + time + ", schemaurl=" + schemaurl 
				+ ", datacontentencoding=" + datacontentencoding
				+ ", datacontenttype=" + datacontenttype + ", subject=" 
				+ subject + "]";
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
			@JsonProperty("time") ZonedDateTime time,
			@JsonProperty("schemaurl") URI schemaurl,
			@JsonProperty("datacontentenconding") String datacontentencoding,
			@JsonProperty("datacontenttype") String datacontenttype,
			@JsonProperty("subject") String subject) {
		
		return new AttributesImpl(id, source, specversion, type, time,
				schemaurl, datacontentencoding, datacontenttype, subject);
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
				
				String datacontenttype = 
					attributes.get(ContextAttributes.datacontenttype.name());
				
				String datacontentencoding = 
					attributes.get(ContextAttributes.datacontentencoding.name());
				
				String subject = attributes.get(ContextAttributes.subject.name());
				
				return AttributesImpl.build(id, source, specversion, type,
						time, schemaurl, datacontentencoding,
						datacontenttype, subject);
			}
			
		};
	}
}