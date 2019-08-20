package io.cloudevents.v03;

import java.net.URI;
import java.time.ZonedDateTime;
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
import io.cloudevents.json.ZonedDateTimeDeserializer;

/**
 * The event attributes implementation for v0.2
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
	public Optional<String> getSubject() {
		return Optional.ofNullable(subject);
	}
	
	
	
	@Override
	public String toString() {
		return "AttributesImpl [id=" + id + ", source=" + source + ", specversion="
				+ specversion + ", type=" + type + ", time=" + time + ", schemaurl=" + schemaurl + ", datacontentencoding=" + datacontentencoding
				+ ", datacontenttype=" + datacontenttype + ", subject=" + subject + "]";
	}

	
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
}