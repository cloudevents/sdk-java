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
@JsonInclude(value = JsonInclude.Include.NON_ABSENT)
public class CloudEventImpl<T> implements CloudEvent<AttributesImpl, T> {

	@NotNull
	@JsonIgnore
	private final AttributesImpl attributes;

	private final T data;

	//To use with json binary data
	private final byte[] dataBase64;

	@NotNull
	private final Map<String, Object> extensions;

	private final Set<ExtensionFormat> extensionsFormats;

	CloudEventImpl(AttributesImpl attributes, byte[] dataBase64,
				   Set<ExtensionFormat> extensions){
		this(attributes, extensions, null, dataBase64);
	}

	CloudEventImpl(AttributesImpl attributes, T data,
			Set<ExtensionFormat> extensions){
		this(attributes, extensions, data, null);
	}

	private CloudEventImpl(AttributesImpl attributes, Set<ExtensionFormat> extensions, T data, byte[] dataBase64){
		this.attributes = attributes;
		this.extensions = extensions.stream()
			.map(ExtensionFormat::memory)
			.collect(Collectors.toMap(InMemoryFormat::getKey,
									  InMemoryFormat::getValue));
		this.data = data;
		this.dataBase64 = dataBase64;
		this.extensionsFormats = extensions;
	}

	/**
	 * Used by the {@link Accessor} to access the set of {@link ExtensionFormat}
	 */
	Set<ExtensionFormat> getExtensionsFormats() {
		return extensionsFormats;
	}


	@Override
	@JsonUnwrapped
	public AttributesImpl getAttributes() {
		return attributes;
	}

	@Override
	public Optional<T> getData() {
		return Optional.ofNullable(data);
	}

	@Override
	@JsonProperty("data_base64")
	public byte[] getDataBase64() {
		return dataBase64;
	}

	@Override
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
			@JsonProperty("data") T data,
			@JsonProperty("data_base64") byte[] dataBase64){

		return CloudEventBuilder.<T>builder()
				.withId(id)
				.withSource(source)
				.withType(type)
				.withTime(time)
				.withDataschema(dataschema)
				.withDataContentType(datacontenttype)
				.withData(data)
				.withDataBase64(dataBase64)
				.withSubject(subject)
				.build();
	}
}
