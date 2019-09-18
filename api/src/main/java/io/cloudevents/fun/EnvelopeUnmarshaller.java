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
package io.cloudevents.fun;

import java.util.List;
import java.util.function.Supplier;

import io.cloudevents.Attributes;
import io.cloudevents.CloudEvent;
import io.cloudevents.extensions.ExtensionFormat;

/**
 * 
 * @author fabiojose
 *
 */
public interface EnvelopeUnmarshaller<A extends Attributes, T, P> {

	/**
	 * Unmarshals the payload into {@link CloudEvent} instance implementation
	 *  
	 * @param payload The envelope payload
	 * @param extensions Supplies a list of {@link ExtensionFormat}
	 * @return The unmarshalled impl of CloudEvent
	 */
	CloudEvent<A, T> unmarshal(P payload, 
			Supplier<List<ExtensionFormat>> extensions);
	
}
