package io.cloudevents.v02;

import java.net.URI;
import java.time.ZonedDateTime;
import java.util.Optional;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Pattern;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonInclude.Include;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;

import io.cloudevents.json.ZonedDateTimeDeserializer;

/**
 * 
 * @author fabiojose
 * 
 * Implemented using immutable data structure.
 * 
 */
@JsonInclude(value = Include.NON_ABSENT)
public class CloudEvent<T> {
	
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
	
	private final T data;

	CloudEvent(String id, URI source, String specversion, String type,
			ZonedDateTime time, URI schemaurl, String contenttype,
			T data) {
		
		this.id = id;
		this.source = source;
		this.specversion = specversion;
		this.type = type;
		
		this.time = time;
		this.schemaurl = schemaurl;
		this.contenttype = contenttype;
		
		this.data = data;
	}

	public String getType() {
		return type;
	}
	public String getId() {
		return id;
	}
	public String getSpecversion() {
		return specversion;
	}
	public URI getSource() {
		return source;
	}

	@JsonDeserialize(using = ZonedDateTimeDeserializer.class)
	public Optional<ZonedDateTime> getTime() {
		return Optional.ofNullable(time);
	}
	public Optional<URI> getSchemaurl() {
		return Optional.ofNullable(schemaurl);
	}
	public Optional<String> getContenttype() {
		return Optional.ofNullable(contenttype);
	}
	public Optional<T> getData() {
		return Optional.ofNullable(data);
	}
	
	@JsonCreator
	public static <T> CloudEvent<T> build(
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
