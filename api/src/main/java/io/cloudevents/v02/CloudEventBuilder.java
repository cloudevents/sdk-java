/**
 * Copyright 2019 The CloudEvents Authors
 * <p>
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * <p>
 * http://www.apache.org/licenses/LICENSE-2.0
 * <p>
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package io.cloudevents.v02;

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

import io.cloudevents.Builder;
import io.cloudevents.CloudEvent;
import io.cloudevents.extensions.ExtensionFormat;
import io.cloudevents.fun.EventBuilder;

import static java.lang.String.format;

/**
 * CloudEvent instances builder 
 *
 * @author fabiojose
 * @author dturanski
 * @version 0.2
 */
public class CloudEventBuilder<T> implements EventBuilder<T, AttributesImpl>,
                                             Builder<AttributesImpl, T> {
    private CloudEventBuilder() {
    }

    private static Validator VALIDATOR;

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
    private Validator validator;

    private static Validator getValidator() {
        if (null == VALIDATOR) {
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
	 *
	 * @param <T> The 'data' type
	 * @param base A base event to copy {@link CloudEvent#getAttributes()},
	 *  {@link CloudEvent#getData()} and {@link CloudEvent#getExtensions()}
	 * @return
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

        attributes.getTime().ifPresent(result::withTime);
        attributes.getSchemaurl().ifPresent(result::withSchemaurl);
        attributes.getContenttype().ifPresent(result::withContenttype);
        Accessor.extensionsOf(base).forEach(result::withExtension);
        base.getData().ifPresent(result::withData);

        return result;
    }

    /**
     *
     * @param <T> the type of 'data'
     * @param data the value of data
     * @param attributes the context attributes
     * @return An new {@link CloudEventImpl} immutable instance
     * @throws IllegalStateException When there are specification constraints
     * violations
     */
    public static <T> CloudEventImpl<T> of(T data, AttributesImpl attributes,
                    Collection<ExtensionFormat> extensions, Validator validator) {
        CloudEventBuilder<T> builder = new CloudEventBuilder<T>()
            .withId(attributes.getId())
            .withSource(attributes.getSource())
            .withType(attributes.getType());

        attributes.getTime().ifPresent(builder::withTime);
        attributes.getSchemaurl().ifPresent(builder::withSchemaurl);
        attributes.getContenttype().ifPresent(builder::withContenttype);
        extensions.forEach(builder::withExtension);

        return builder
			.withData(data)
			.withValidator(validator)
			.build();
    }

    @Override
    public CloudEvent<AttributesImpl, T> build(T data, AttributesImpl attributes,
					Collection<ExtensionFormat> extensions) {
        return CloudEventBuilder.of(data, attributes, extensions, this.validator);
    }

    /**
     * {@inheritDoc}
     * @return An new {@link CloudEventImpl} immutable instance
     * @throws IllegalStateException When there are specification constraints
     * violations
     */
    @Override
    public CloudEventImpl<T> build() {
        AttributesImpl attributes = new AttributesImpl(type, SPEC_VERSION,
                source, id, AttributesImpl.formatZonedDateTime(time), schemaurl, contenttype);

        CloudEventImpl<T> event = new CloudEventImpl<>(attributes, data, extensions);

        if (validator == null) {
            validator = getValidator();
        }
        Set<ConstraintViolation<Object>> violations =
            validator.validate(event);

        violations.addAll(validator.validate(event.getAttributes()));

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

    public CloudEventBuilder<T> withValidator(Validator validator) {
        this.validator = validator;
        return this;
    }

    public <TT> CloudEvent<AttributesImpl, TT>
    build(CloudEvent<AttributesImpl, T> base, String id, TT newData) {
        return build(base, id, newData, null);
    }

    public <TT> CloudEvent<AttributesImpl, TT>
    build(CloudEvent<AttributesImpl, T> base, String id, TT newData, Validator validator) {
        Objects.requireNonNull(base);

        AttributesImpl attributes = base.getAttributes();

        CloudEventBuilder<TT> builder = new CloudEventBuilder<TT>()
            .withId(id)
            .withSource(attributes.getSource())
            .withType(attributes.getType());

        attributes.getTime().ifPresent(builder::withTime);
        attributes.getSchemaurl().ifPresent(builder::withSchemaurl);
        attributes.getContenttype().ifPresent(builder::withContenttype);
        Collection<ExtensionFormat> extensions = Accessor.extensionsOf(base);
        extensions.forEach(builder::withExtension);

        return builder
            .withData(newData)
            .withValidator(validator)
            .build();
    }
}
