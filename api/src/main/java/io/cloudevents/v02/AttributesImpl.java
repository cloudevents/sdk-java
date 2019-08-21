package io.cloudevents.v02;

import java.net.URI;
import java.time.ZonedDateTime;
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
import io.cloudevents.fun.BinaryFormatAttributeUnmarshaller;
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
	
	public AttributesImpl(String type, String specversion, URI source,
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
	public static BinaryFormatAttributeUnmarshaller<AttributesImpl> unmarshaller() {
		
		return null;
	}
}
