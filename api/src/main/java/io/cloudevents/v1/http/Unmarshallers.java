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
package io.cloudevents.v1.http;

import io.cloudevents.extensions.DatarefExtension;
import io.cloudevents.extensions.DistributedTracingExtension;
import io.cloudevents.format.BinaryUnmarshaller;
import io.cloudevents.format.StructuredUnmarshaller;
import io.cloudevents.format.builder.HeadersStep;
import io.cloudevents.json.Json;
import io.cloudevents.v1.AttributesImpl;
import io.cloudevents.v1.CloudEventBuilder;
import io.cloudevents.v1.CloudEventImpl;
import io.cloudevents.v1.http.AttributeMapper;
import io.cloudevents.v1.http.ExtensionMapper;

/**
 * 
 * @author fabiojose
 * @version 1.0
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
		return 
			BinaryUnmarshaller.<AttributesImpl, T, String>builder()
				.map(AttributeMapper::map)
				.map(AttributesImpl::unmarshal)
				.map("application/json", Json.umarshaller(type)::unmarshal)
				.next()
				.map(ExtensionMapper::map)
				.map(DistributedTracingExtension::unmarshall)
				.map(DatarefExtension::unmarshall)
				.next()
				.builder(CloudEventBuilder.<T>builder()::build);
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
		
		return
		StructuredUnmarshaller.<AttributesImpl, T, String>
		  builder()
			.map(ExtensionMapper::map)
			.map(DistributedTracingExtension::unmarshall)
			.map(DatarefExtension::unmarshall)
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
				
				return builder.build();
			});
	}
}
