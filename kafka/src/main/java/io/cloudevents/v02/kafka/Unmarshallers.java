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
package io.cloudevents.v02.kafka;

import io.cloudevents.extensions.DistributedTracingExtension;
import io.cloudevents.format.BinaryUnmarshaller;
import io.cloudevents.format.builder.HeadersStep;
import io.cloudevents.json.Json;
import io.cloudevents.v02.AttributesImpl;
import io.cloudevents.v02.CloudEventBuilder;

/**
 * 
 * @author fabiojose
 *
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
}
