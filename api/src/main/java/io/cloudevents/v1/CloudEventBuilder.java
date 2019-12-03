package io.cloudevents.v1;

import static java.lang.String.format;

import java.net.URI;
import java.time.ZonedDateTime;
import java.util.Collection;
import java.util.HashSet;
import java.util.Objects;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

import javax.validation.ConstraintViolation;
import javax.validation.Validation;
import javax.validation.Validator;

import io.cloudevents.CloudEvent;
import io.cloudevents.extensions.ExtensionFormat;
import io.cloudevents.fun.EventBuilder;

/**
 * 
 * @author fabiojose
 * @author dturanski
 * @version 1.0
 */
public class CloudEventBuilder<T> implements 
	EventBuilder<T, AttributesImpl> {
	
	private CloudEventBuilder() {}
	
	private static Validator VALIDATOR;
	
	public static final String SPEC_VERSION = "1.0";
	private static final String MESSAGE_SEPARATOR = ", ";
	private static final String MESSAGE = "'%s' %s";
	private static final String ERR_MESSAGE = "invalid payload: %s";
	
	private String id;
	private URI source;

	private String type;
	private String datacontenttype;
	private URI dataschema;
	private String subject;
	private ZonedDateTime time;

	private T data;

	private final Set<ExtensionFormat> extensions = new HashSet<>();

	private Validator validator;

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
		return new CloudEventBuilder<>();
	}

	/**
	 * Builder with base event to copy attributes
	 * @param <T> The 'data' type
	 * @param base The base event to copy attributes
	 */
	public static <T> CloudEventBuilder<T> builder(
		            CloudEvent<AttributesImpl, T> base) {
		Objects.requireNonNull(base);
		
		CloudEventBuilder<T> result = new CloudEventBuilder<>();
		
		AttributesImpl attributes = base.getAttributes();
		
		result
			.withId(attributes.getId())
			.withSource(attributes.getSource())
			.withType(attributes.getType());
		
		attributes.getDataschema().ifPresent(result::withDataschema);
		attributes.getDatacontenttype().ifPresent(result::withDataContentType);
		attributes.getSubject().ifPresent(result::withSubject);
		attributes.getTime().ifPresent(result::withTime);
		Accessor.extensionsOf(base).forEach(result::withExtension);
		base.getData().ifPresent(result::withData);

		return result;
	}

	@Override
	public CloudEvent<AttributesImpl, T> build(T data, 
			AttributesImpl attributes,
			Collection<ExtensionFormat> extensions) {

		CloudEventBuilder<T> builder = CloudEventBuilder.<T>builder()
				.withId(attributes.getId())
				.withSource(attributes.getSource())
				.withType(attributes.getType());
			
		attributes.getTime().ifPresent(builder::withTime);
		attributes.getDataschema().ifPresent(builder::withDataschema);
		attributes.getDatacontenttype().ifPresent(builder::withDataContentType);
		attributes.getSubject().ifPresent(builder::withSubject);
		extensions.forEach(builder::withExtension);

		return builder
			.withData(data)
			.withValidator(validator)
			.build();
	}
	
	/**
	 * 
	 * @return An new {@link CloudEvent} immutable instance
	 * @throws IllegalStateException When there are specification constraints
	 * violations
	 */
	public CloudEventImpl<T> build() {
		
		AttributesImpl attributes = new AttributesImpl(id, source, SPEC_VERSION, type,
				datacontenttype, dataschema, subject, time);
		
		CloudEventImpl<T> cloudEvent =
			new CloudEventImpl<>(attributes, data, extensions);
		
		if(data instanceof byte[]) {
			cloudEvent.setDataBase64((byte[])data);
		}
		if(validator == null) {
			validator = getValidator();
		}
		Set<ConstraintViolation<Object>> violations =
				validator.validate(cloudEvent);
		
		violations.addAll(validator.validate(cloudEvent.getAttributes()));
		
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
	
	public CloudEventBuilder<T> withDataschema(URI dataschema) {
		this.dataschema = dataschema;
		return this;
	}
	
	public CloudEventBuilder<T> withDataContentType(
			String datacontenttype) {
		this.datacontenttype = datacontenttype;
		return this;
	}
	
	public CloudEventBuilder<T> withSubject(
			String subject) {
		this.subject = subject;
		return this;
	}

	public CloudEventBuilder<T> withTime(ZonedDateTime time) {
		this.time = time;
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

	public CloudEventBuilder<T> withValidator(Validator validator) {
		this.validator = validator;
		return this;
	}
}
