/**
 * Copyright 2019 The CloudEvents Authors
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package io.cloudevents.v03.kafka;

import static io.cloudevents.extensions.DistributedTracingExtension.Format.IN_MEMORY_KEY;
import static io.cloudevents.extensions.DistributedTracingExtension.Format.TRACE_PARENT_KEY;
import static io.cloudevents.extensions.DistributedTracingExtension.Format.TRACE_STATE_KEY;
import static java.util.Optional.ofNullable;

import java.util.Map;
import java.util.Optional;
import java.util.AbstractMap.SimpleEntry;
import java.util.Map.Entry;
import java.util.stream.Collectors;

import io.cloudevents.extensions.DistributedTracingExtension;
import io.cloudevents.extensions.ExtensionFormat;
import io.cloudevents.extensions.DistributedTracingExtension.Format;
import io.cloudevents.format.BinaryUnmarshaller;
import io.cloudevents.format.StructuredUnmarshaller;
import io.cloudevents.format.builder.HeadersStep;
import io.cloudevents.json.Json;
import io.cloudevents.v03.AttributesImpl;
import io.cloudevents.v03.CloudEventBuilder;
import io.cloudevents.v03.CloudEventImpl;

/**
 * 
 * @author fabiojose
 * @version 0.3
 */
public class Unmarshallers {
	private Unmarshallers() {}
	
	/**
	 * Builds a Binary Content Mode unmarshaller to unmarshal JSON as CloudEvents data
	 * for Kafka Transport Binding
	 * 
	 * @param <T> The 'data' type
	 * @param type The type reference to use for 'data' unmarshal
	 * @return A step to supply the headers, payload and to unmarshal
	 * @see BinaryUnmarshaller
	 */
	public static <T> HeadersStep<AttributesImpl, T, byte[]> 
			binary(Class<T> type) {
				
		return 
			BinaryUnmarshaller.<AttributesImpl, T, byte[]>
			  builder()
				.map(AttributeMapper::map)
				.map(AttributesImpl::unmarshal)
				.map("application/json", Json.binaryUmarshaller(type))
				.next()
				.map(ExtensionMapper::map)
				.map(DistributedTracingExtension::unmarshall)
				.next()
				.builder(CloudEventBuilder.<T>builder()::build);

	}
			
	/**
	 * Builds a Structured Content Mode unmarshaller to unmarshal JSON as CloudEvents data
	 * for Kafka Transport Binding
	 * 
	 * @param <T> The 'data' type
	 * @param typeOfData The type reference to use for 'data' unmarshal
	 * @return A step to supply the headers, payload and to unmarshal
	 * @see StructuredUnmarshaller
	 */
	@SuppressWarnings("unchecked")
	public static <T> HeadersStep<AttributesImpl, T, byte[]> 
			structured(Class<T> typeOfData) {
		
		return
		StructuredUnmarshaller.<AttributesImpl, T, byte[]>
		  builder()
			.map(ExtensionMapper::map)
			.map(DistributedTracingExtension::unmarshall)
			.next()
			.map((payload, extensions) -> {	

				CloudEventImpl<T> event =
					Json.<CloudEventImpl<T>>
						binaryDecodeValue(payload, CloudEventImpl.class, typeOfData);
				
				Optional<ExtensionFormat> dteFormat = 
				ofNullable(event.getExtensions().get(IN_MEMORY_KEY))
					.filter(extension -> extension instanceof Map)
					.map(extension -> (Map<String, Object>)extension)
					.map(extension -> 
						extension.entrySet()
							.stream()
							.filter(entry -> 
								null!= entry.getKey() 
									&& null!= entry.getValue())
							.map(tracing -> 
								new SimpleEntry<>(tracing.getKey(), 
										tracing.getValue().toString()))
							.collect(Collectors.toMap(Entry::getKey, Entry::getValue)))
					.map(extension -> {
						DistributedTracingExtension dte = 
								new DistributedTracingExtension();
						dte.setTraceparent(extension.get(TRACE_PARENT_KEY));
						dte.setTracestate(extension.get(TRACE_STATE_KEY));
						
						return new Format(dte);
					});
				
				CloudEventBuilder<T> builder = 
					CloudEventBuilder.<T>builder(event);
				
				extensions.get().forEach(extension -> {
					builder.withExtension(extension);
				});
				
				dteFormat.ifPresent(tracing -> {
					builder.withExtension(tracing);
				});
				
				return builder.build();
			});
	}
}
