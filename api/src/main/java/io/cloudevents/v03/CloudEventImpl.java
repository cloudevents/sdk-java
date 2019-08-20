package io.cloudevents.v03;

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
import com.fasterxml.jackson.annotation.JsonInclude.Include;

import io.cloudevents.Event;
import io.cloudevents.ExtensionFormat;

/**
 * The event implementation
 * 
 * @author fabiojose
 *
 */
@JsonInclude(value = Include.NON_ABSENT)
public class CloudEventImpl<T> implements Event<AttributesImpl, T> {
	
	@JsonIgnore
	@JsonUnwrapped
	@NotNull
	private final AttributesImpl attributes;
	
	private final T data;
	
	@NotNull
	private final Map<String, Object> extensions;
	
	CloudEventImpl(AttributesImpl attributes, T data,
			Set<ExtensionFormat> extensions) {
		this.attributes = attributes;
		this.data = data;
		
		this.extensions = extensions
				.stream()
				.collect(Collectors
						.toMap(ExtensionFormat::getKey,
								ExtensionFormat::getExtension));
	}

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
		return Collections.unmodifiableMap(extensions);
	}
	
	/**
	 * The unique method that allows mutable. Used by
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
			@JsonProperty("specversion") String specversion,
			@JsonProperty("type") String type,
			@JsonProperty("time") ZonedDateTime time,
			@JsonProperty("schemaurl") URI schemaurl,
			@JsonProperty("datacontentencoding") String datacontentencoding,
			@JsonProperty("datacontenttype") String datacontenttype,
			@JsonProperty("subject") String subject,
			@JsonProperty("data") T data) {
		
		return CloudEventBuilder.<T>builder()
				.withId(id)
				.withSource(source)
				.withType(type)
				.withTime(time)
				.withSchemaurl(schemaurl)
				.withDatacontentencoding(datacontentencoding)
				.withDatacontenttype(datacontenttype)
				.withData(data)
				.withSubject(subject)
				.build();
	}

}
