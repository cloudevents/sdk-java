package io.cloudevents.v1.kafka;

import java.util.HashMap;
import java.util.Map;

import io.cloudevents.extensions.ExtensionFormat;
import io.cloudevents.format.BinaryMarshaller;
import io.cloudevents.format.StructuredMarshaller;
import io.cloudevents.format.Wire;
import io.cloudevents.format.builder.EventStep;
import io.cloudevents.json.Json;
import io.cloudevents.v1.Accessor;
import io.cloudevents.v1.AttributesImpl;
import io.cloudevents.v1.CloudEventImpl;
import io.cloudevents.v1.kafka.HeaderMapper;

/**
 * 
 * @author fabiojose
 * @version 1.0
 */
public class Marshallers {
private Marshallers() {}
	
	private static final Map<String, byte[]> NO_HEADERS = 
			new HashMap<>();
	
	/**
	 * Builds a Binary Content Mode marshaller to marshal cloud events as JSON for
	 * Kafka Transport Binding
	 * 
	 * @param <T> The 'data' type
	 * @return A step to provide the {@link CloudEventImpl} and marshal as JSON
	 * @see BinaryMarshaller
	 */
	public static <T> EventStep<AttributesImpl, T, byte[], byte[]> 
			binary() {
				
		return 
			BinaryMarshaller.<AttributesImpl, T, byte[], byte[]>
			  builder()
				.map(AttributesImpl::marshal)
				.map(Accessor::extensionsOf)
				.map(ExtensionFormat::marshal)
				.map(HeaderMapper::map)
				.map(Json::binaryMarshal)
				.builder(Wire<byte[], String, byte[]>::new);
	}

	/**
	 * Builds a Structured Content Mode marshaller to marshal cloud event as JSON for
	 * Kafka Transport Binding
	 * @param <T> The 'data' type
	 * @return A step to provider the {@link CloudEventImpl} and marshal as JSON
	 * @see StructuredMarshaller
	 */
	public static <T> EventStep<AttributesImpl, T, byte[], byte[]> 
			structured() {
				
		return 
			StructuredMarshaller.<AttributesImpl, T, byte[], byte[]>
			  builder()
				.mime("content-type", "application/cloudevents+json".getBytes())
				.map((event) -> 
					Json.binaryMarshal(event, NO_HEADERS))
				.skip();
	}
}
