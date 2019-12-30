package io.cloudevents.v1.http;

import java.util.HashMap;
import java.util.Map;

import io.cloudevents.CloudEvent;
import io.cloudevents.extensions.ExtensionFormat;
import io.cloudevents.format.BinaryMarshaller;
import io.cloudevents.format.StructuredMarshaller;
import io.cloudevents.format.Wire;
import io.cloudevents.format.builder.EventStep;
import io.cloudevents.json.Json;
import io.cloudevents.v1.Accessor;
import io.cloudevents.v1.AttributesImpl;
import io.cloudevents.v1.CloudEventImpl;
import io.cloudevents.v1.http.HeaderMapper;

/**
 * 
 * @author fabiojose
 * @version 1.0
 */
public class Marshallers {
	private Marshallers() {}
	
	private static final Map<String, String> NO_HEADERS =
			new HashMap<String, String>();
		
	/**
	 * Builds a Binary Content Mode marshaller to marshal cloud events as JSON for
	 * HTTP Transport Binding
	 * 
	 * @param <T> The 'data' type
	 * @return A step to provide the {@link CloudEventImpl} and marshal as JSON
	 * @see BinaryMarshaller
	 */
	public static <T> EventStep<AttributesImpl, T, String, String> binary() {
		return 
			BinaryMarshaller.<AttributesImpl, T, String, String>
			  builder()
				.map(AttributesImpl::marshal)
				.map(Accessor::extensionsOf)
				.map(ExtensionFormat::marshal)
				.map(HeaderMapper::map)
				.map(Json.<T, String>marshaller()::marshal)
				.builder(Wire::new);
	}
	
	/**
	 * Builds a Structured Content Mode marshaller to marshal cloud event as JSON for
	 * HTTP Transport Binding
	 * @param <T> The 'data' type
	 * @return A step to provider the {@link CloudEventImpl} and marshal as JSON
	 * @see StructuredMarshaller
	 */
	public static <T> EventStep<AttributesImpl, T, String, String> structured() {
		return 
		StructuredMarshaller.
		  <AttributesImpl, T, String, String>builder()
			.mime("Content-Type", "application/cloudevents+json")
			.map((event) -> Json.<CloudEvent<AttributesImpl, T>, String>
						marshaller().marshal(event, NO_HEADERS))
			.map(Accessor::extensionsOf)
			.map(ExtensionFormat::marshal)
			.map(HeaderMapper::map);
	}
}
