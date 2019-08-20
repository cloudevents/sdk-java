package io.cloudevents.v03;

import static java.lang.String.format;

import java.net.URI;
import java.time.ZonedDateTime;
import java.util.HashSet;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

import javax.validation.ConstraintViolation;
import javax.validation.Validation;
import javax.validation.Validator;

import io.cloudevents.Event;
import io.cloudevents.ExtensionFormat;

/**
 * The event builder.
 * 
 * @author fabiojose
 *
 */
public final class CloudEventBuilder<T> {
	
	private static Validator VALIDATOR;
	
	public static final String SPEC_VERSION = "0.3";
	private static final String MESSAGE_SEPARATOR = ", ";
	private static final String MESSAGE = "'%s' %s";
	private static final String ERR_MESSAGE = "invalid payload: %s";
	
	private String id;
	private URI source;

	private String type;
	
	private ZonedDateTime time;
	private URI schemaurl;
	private String datacontentencoding;
	private String datacontenttype;
	private String subject;
	
	private T data;
	
	private final Set<ExtensionFormat> extensions = new HashSet<>();

	private CloudEventBuilder() {}
	
	private static Validator getValidator() {
		if(null== VALIDATOR) {
			VALIDATOR = Validation.buildDefaultValidatorFactory().getValidator();
		}
		return VALIDATOR;
	}
	
	/**
	 * Gets a brand new builder instance
	 * @param <T> The 'data' type
	 */
	public static <T> CloudEventBuilder<T> builder() {
		return new CloudEventBuilder<T>();
	}
	
	/**
	 * Build an event from data and attributes
	 * @param <T> the type of 'data'
	 * @param data the value of data
	 * @param attributes the context attributes
	 * @return An new {@link CloudEventImpl} immutable instance
	 * @throws IllegalStateException When there are specification constraints
	 * violations
	 */
	public static <T> CloudEventImpl<T> of(T data, AttributesImpl attributes) {
		CloudEventBuilder<T> builder = CloudEventBuilder.<T>builder()
			.withId(attributes.getId())
			.withSource(attributes.getSource())
			.withType(attributes.getType());
		
		attributes.getTime().ifPresent((time) -> {
			builder.withTime(time);
		});
		
		attributes.getSchemaurl().ifPresent((schemaurl) -> {
			builder.withSchemaurl(schemaurl);
		}); 
		
		attributes.getDatacontentencoding().ifPresent((dce) -> {
			builder.withDatacontentencoding(dce);
		});
		
		attributes.getDatacontenttype().ifPresent((dct) -> {
			builder.withDatacontenttype(dct);
		});
		
		attributes.getSubject().ifPresent((subject) -> {
			builder.withSubject(subject);
		});
		
		builder.withData(data);
		
		return builder.build();
	}
	
	/**
	 * 
	 * @return An new {@link Event} immutable instance
	 * @throws IllegalStateException When there are specification constraints
	 * violations
	 */
	public CloudEventImpl<T> build() {
		
		AttributesImpl attributes = new AttributesImpl(id, source, SPEC_VERSION,
				type, time, schemaurl, datacontentencoding, datacontenttype,
				subject);
		
		CloudEventImpl<T> cloudEvent = 
				new CloudEventImpl<T>(attributes, data, extensions);
		
		Set<ConstraintViolation<Object>> violations =
				getValidator().validate(cloudEvent);
		
		violations.addAll(getValidator().validate(cloudEvent.getAttributes()));
		
		final String errs = 
			violations.stream()
				.map(v -> format(MESSAGE, v.getPropertyPath(), v.getMessage()))
				.collect(Collectors.joining(MESSAGE_SEPARATOR));
		
		Optional.ofNullable(
			"".equals(errs) ? null : errs
					
		).ifPresent((e) -> {
			throw new IllegalStateException(format(ERR_MESSAGE, e));
		});
		
		return cloudEvent;
	}
	
	public CloudEventBuilder<T> withId(String id) {
		this.id = id;
		return this;
	}
	
	public CloudEventBuilder<T> withSource(URI source) {
		this.source = source;
		return this;
	}
	
	public CloudEventBuilder<T> withType(String type) {
		this.type = type;
		return this;
	}
	
	public CloudEventBuilder<T> withTime(ZonedDateTime time) {
		this.time = time;
		return this;
	}
	
	public CloudEventBuilder<T> withSchemaurl(URI schemaurl) {
		this.schemaurl = schemaurl;
		return this;
	}
	
	public CloudEventBuilder<T> withDatacontentencoding(
			String datacontentencoding) {
		this.datacontentencoding = datacontentencoding;
		return this;
	}
	
	public CloudEventBuilder<T> withDatacontenttype(
			String datacontenttype) {
		this.datacontenttype = datacontenttype;
		return this;
	}
	
	public CloudEventBuilder<T> withSubject(
			String subject) {
		this.subject = subject;
		return this;
	}
	
	public CloudEventBuilder<T> withData(T data) {
		this.data = data;
		return this;
	}
	
	public CloudEventBuilder<T> withExtension(ExtensionFormat extension) {
		this.extensions.add(extension);
		return this;
	}
}
