package io.cloudevents.v02.http;

import javax.validation.Valid;
import javax.validation.Validator;

import io.cloudevents.extensions.DistributedTracingExtension;
import io.cloudevents.format.BinaryUnmarshaller;
import io.cloudevents.format.StructuredUnmarshaller;
import io.cloudevents.format.builder.HeadersStep;
import io.cloudevents.json.Json;
import io.cloudevents.v02.AttributesImpl;
import io.cloudevents.v02.CloudEventBuilder;
import io.cloudevents.v02.CloudEventImpl;

/**
 * 
 * @author fabiojose
 * @version 0.2
 */
public class Unmarshallers {
	private Unmarshallers() {}

	/**
	 * Builds a Binary Content Mode unmarshaller to unmarshal JSON as CloudEvents data
	 * for HTTP Transport Binding
	 *
	 * @param <T> The 'data' type
	 * @param type The type reference to use for 'data' unmarshal
	 * @return A step to supply the headers, payload and to unmarshal
	 * @see BinaryUnmarshaller
	 */
	public static <T> HeadersStep<AttributesImpl, T, String>
			binary(Class<T> type) {
		return binary(type, null);
	}

	/**
	 * Builds a Binary Content Mode unmarshaller to unmarshal JSON as CloudEvents data
	 * for HTTP Transport Binding
	 * 
	 * @param <T> The 'data' type
	 * @param type The type reference to use for 'data' unmarshal
	 * @param validator Provide an existing instance of a {@link Validator}
	 * @return A step to supply the headers, payload and to unmarshal
	 * @see BinaryUnmarshaller
	 */
	public static <T> HeadersStep<AttributesImpl, T, String> 
			binary(Class<T> type, Validator validator) {
		return 
			BinaryUnmarshaller.<AttributesImpl, T, String>builder()
				.map(AttributeMapper::map)
				.map(AttributesImpl::unmarshal)
				.map("application/json", Json.umarshaller(type)::unmarshal)
				.next()
				.map(ExtensionMapper::map)
				.map(DistributedTracingExtension::unmarshall)
				.next()
				.builder(CloudEventBuilder.<T>builder().withValidator(validator)::build);
	}

	/**
	 * Builds a Structured Content Mode unmarshaller to unmarshal JSON as CloudEvents data
	 * for HTTP Transport Binding
	 *
	 * @param <T> The 'data' type
	 * @param typeOfData The type reference to use for 'data' unmarshal
	 * @return A step to supply the headers, payload and to unmarshal
	 * @see StructuredUnmarshaller
	 */
	public static <T> HeadersStep<AttributesImpl, T, String>
	structured(Class<T> typeOfData) {
		return structured(typeOfData, null);
	}

	/**
	 * Builds a Structured Content Mode unmarshaller to unmarshal JSON as CloudEvents data
	 * for HTTP Transport Binding
	 * 
	 * @param <T> The 'data' type
	 * @param typeOfData The type reference to use for 'data' unmarshal
	 * @param validator Provided instance of a {@link Validator}
	 * @return A step to supply the headers, payload and to unmarshal
	 * @see StructuredUnmarshaller
	 */
	public static <T> HeadersStep<AttributesImpl, T, String> 
			structured(Class<T> typeOfData, Validator validator) {
		
		return
		StructuredUnmarshaller.<AttributesImpl, T, String>
		  builder()
			.map(ExtensionMapper::map)
			.map(DistributedTracingExtension::unmarshall)
			.next()
			.map((payload, extensions) -> {			
				CloudEventImpl<T> event =
					Json.<CloudEventImpl<T>>
						decodeValue(payload, CloudEventImpl.class, typeOfData);
				
				CloudEventBuilder<T> builder = 
					CloudEventBuilder.<T>builder(event);
				
				extensions.get().forEach(extension -> {
					builder.withExtension(extension);
				});

				return builder.withValidator(validator).build();
			});
	}
}
