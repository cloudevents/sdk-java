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

import java.net.URI;
import java.time.ZonedDateTime;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

import javax.validation.constraints.NotNull;

import com.fasterxml.jackson.annotation.JsonAlias;
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
 * 
 * @author fabiojose
 * @version 1.0
 */
@JsonInclude(value = Include.NON_ABSENT)
public class CloudEventImpl<T> implements CloudEvent<AttributesImpl, T> {
	
	public static final String EVENT_DATA_FIELD = "data";
	public static final String EVENT_DATA_BASE64_FILED = "data_base64";
	
	@JsonIgnore
	@NotNull
	private final AttributesImpl attributes;
	
	@JsonIgnore
	private final T data;
	
	//To use with json binary data
	@JsonIgnore
	private byte[] dataBase64;
	
	@NotNull
	private final Map<String, Object> extensions;
	
	private final Set<ExtensionFormat> extensionsFormats;
	
	CloudEventImpl(AttributesImpl attributes, T data,
			Set<ExtensionFormat> extensions){
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
	
	/**
	 * To handle the JSON base64 serialization
	 * @param data The byte array to encode as base64
	 */
	void setDataBase64(byte[] data) {
		this.dataBase64 = data;
	}
	
	@JsonUnwrapped
	@Override
	public AttributesImpl getAttributes() {
		return attributes;
	}

	@Override
	public Optional<T> getData() {
		return Optional.ofNullable(data);
	}

	@JsonAnyGetter
	@Override
	public Map<String, Object> getExtensions() {
		Map<String, Object> result = new HashMap<>(extensions);
		
		if(null== dataBase64) {
			if(null!= data) {
				result.put(EVENT_DATA_FIELD, data);
			}
		} else {
			result.put(EVENT_DATA_BASE64_FILED, dataBase64);
		}
		return Collections.unmodifiableMap(result);
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

	/**
	 * Used by the Jackson Framework to unmarshall.
	 */
	@JsonCreator
	public static <T> CloudEventImpl<T> build(
			@JsonProperty("id") String id,
			@JsonProperty("source") URI source,
			@JsonProperty("type") String type,
			@JsonProperty("datacontenttype") String datacontenttype,
			@JsonProperty("dataschema") URI dataschema,
			@JsonProperty("subject") String subject,
			@JsonProperty("time") ZonedDateTime time,
			@JsonProperty("data")
			@JsonAlias("data_base64")
			T data){
		
		return CloudEventBuilder.<T>builder()
				.withId(id)
				.withSource(source)
				.withType(type)
				.withTime(time)
				.withDataschema(dataschema)
				.withDataContentType(datacontenttype)
				.withData(data)
				.withSubject(subject)
				.build();
	}
}
