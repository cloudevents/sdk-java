package io.cloudevents.v02;

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

/**
 * CloudEvent instances builder 
 * 
 * @author fabiojose
 *
 */
public class CloudEventBuilder<T> {
	
	private static final String SPEC_VERSION = "0.2";
	private static final String MESSAGE_SEPARATOR = ", ";
	private static final String MESSAGE = "'%s' %s";
	private static final String ERR_MESSAGE = "invalid payload: %s";

	private String type;
	private String id;
	private URI source;
	
	private ZonedDateTime time;
	private URI schemaurl;
	private String contenttype;
	private T data;
	
	private final Set<ExtensionFormat> extensions = new HashSet<>();
	
	private Validator getValidator() {
		return Validation.buildDefaultValidatorFactory().getValidator();
	}

	/**
	 * 
	 * @return An new {@link CloudEvent} immutable instance
	 * @throws IllegalStateException When there are specification constraints
	 * violations
	 */
	public CloudEvent<T> build() {
		CloudEvent<T> event = new CloudEvent<>(id, source, SPEC_VERSION, type,
				time, schemaurl, contenttype, data, extensions);
		
		Set<ConstraintViolation<CloudEvent<T>>> violations =
				getValidator().validate(event);
		
		final String errs = 
			violations.stream()
				.map(v -> format(MESSAGE, v.getPropertyPath(), v.getMessage()))
				.collect(Collectors.joining(MESSAGE_SEPARATOR));
		
		Optional.ofNullable(
			"".equals(errs) ? null : errs
					
		).ifPresent((e) -> {
			throw new IllegalStateException(format(ERR_MESSAGE, e));
		});
		
		return event;
	}
 	
	public CloudEventBuilder<T> withType(String type) {
		this.type = type;
		return this;
	}
	
	public CloudEventBuilder<T> withId(String id) {
		this.id = id;
		return this;
	}
	
	public CloudEventBuilder<T> withSource(URI source) {
		this.source = source;
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
	
	public CloudEventBuilder<T> withContenttype(String contenttype) {
		this.contenttype = contenttype;
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
