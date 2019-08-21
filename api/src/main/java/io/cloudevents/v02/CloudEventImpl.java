package io.cloudevents.v02;

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
import com.fasterxml.jackson.annotation.JsonInclude.Include;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonUnwrapped;

import io.cloudevents.CloudEvent;
import io.cloudevents.ExtensionFormat;

/**
 * The event implementation
 * 
 * @author fabiojose
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
			@JsonProperty("specversion") String specversion,
			@JsonProperty("type") String type,
			@JsonProperty("time") ZonedDateTime time,
			@JsonProperty("schemaurl") URI schemaurl,
			@JsonProperty("contenttype") String contenttype,
			@JsonProperty("data") T data) {
		
		return new CloudEventBuilder<T>()
				.withId(id)
				.withSource(source)
				.withType(type)
				.withTime(time)
				.withSchemaurl(schemaurl)
				.withContenttype(contenttype)
				.withData(data)
				.build();
	}
}
