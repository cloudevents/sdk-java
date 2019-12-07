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

import java.net.URI;

import java.util.Collections;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

import javax.validation.constraints.NotNull;

import com.fasterxml.jackson.annotation.JsonAnyGetter;
import com.fasterxml.jackson.annotation.JsonAnySetter;
import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonInclude.Include;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonUnwrapped;

import io.cloudevents.CloudEvent;
import io.cloudevents.extensions.ExtensionFormat;
import io.cloudevents.extensions.InMemoryFormat;

/**
 * The event implementation
 * 
 * @author fabiojose
 * @author dturanski
 * 
 */
@JsonInclude(value = Include.NON_ABSENT)
public class CloudEventImpl<T> implements CloudEvent<AttributesImpl, T> {

	@JsonIgnore
	@NotNull
	private final AttributesImpl attributes;
	
	private final T data;
	
	@NotNull
	private final Map<String, Object> extensions;
	
	private final Set<ExtensionFormat> extensionsFormats;

	CloudEventImpl(AttributesImpl attributes, T data,
			Set<ExtensionFormat> extensions) {
		
		this.attributes = attributes;
		
		this.data = data;
		
		this.extensions = extensions.stream()
			.map(ExtensionFormat::memory)
			.collect(Collectors.toMap(InMemoryFormat::getKey,
					InMemoryFormat::getValue));
		
		this.extensionsFormats = extensions;
	}
	
	/**
	 * Used by the {@link Accessor} to access the set of {@link ExtensionFormat}
	 */
	Set<ExtensionFormat> getExtensionsFormats() {
		return extensionsFormats;
	}
	
	@JsonUnwrapped
	@Override
	public AttributesImpl getAttributes() {
		return this.attributes;
	}
	
	/**
     * The event payload. The payload depends on the eventType,
     * schemaURL and eventTypeVersion, the payload is encoded into
     * a media format which is specified by the contentType attribute
     * (e.g. application/json).
     */
	public Optional<T> getData() {
		return Optional.ofNullable(data);
	}

	@JsonAnyGetter
	public Map<String, Object> getExtensions() {
		return Collections.unmodifiableMap(extensions);
	}
	
	/**
	 * The unique method that allows mutation. Used by
	 * Jackson Framework to inject the extensions.
	 * 
	 * @param name Extension name
	 * @param value Extension value
	 */
	@JsonAnySetter
	void addExtension(String name, Object value) {
		extensions.put(name, value);
	}
	
	@JsonCreator
	public static <T> CloudEventImpl<T> build(
			@JsonProperty("id") String id,
			@JsonProperty("source") URI source,
			@JsonProperty("type") String type,
			@JsonProperty("time") String time,
			@JsonProperty("schemaurl") URI schemaurl,
			@JsonProperty("contenttype") String contenttype,
			@JsonProperty("data") T data) {
		
		return CloudEventBuilder.<T>builder()
				.withId(id)
				.withSource(source)
				.withType(type)
				.withTime(AttributesImpl.parseZonedDateTime(time))
				.withSchemaurl(schemaurl)
				.withContenttype(contenttype)
				.withData(data)
				.build();
	}
}
