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
package io.cloudevents.v02.http;

import java.util.HashMap;
import java.util.Map;

import io.cloudevents.CloudEvent;
import io.cloudevents.extensions.ExtensionFormat;
import io.cloudevents.format.BinaryMarshaller;
import io.cloudevents.format.StructuredMarshaller;
import io.cloudevents.format.Wire;
import io.cloudevents.format.builder.EventStep;
import io.cloudevents.json.Json;
import io.cloudevents.v02.Accessor;
import io.cloudevents.v02.AttributesImpl;
import io.cloudevents.v02.CloudEventImpl;

/**
 * 
 * @author fabiojose
 * @version 0.2
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
				.builder(Wire<String, String, String>::new);
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
			.map((event) -> {
				return Json.<CloudEvent<AttributesImpl, T>, String>
							marshaller().marshal(event, NO_HEADERS);
			})
			.map(Accessor::extensionsOf)
			.map(ExtensionFormat::marshal)
			.map(HeaderMapper::map);
	}
}
