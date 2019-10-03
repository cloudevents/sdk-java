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

import java.util.Collection;

import io.cloudevents.Attributes;
import io.cloudevents.CloudEvent;
import io.cloudevents.extensions.ExtensionFormat;

/**
 * To build the event.
 * 
 * @author fabiojose
 *
 */
@FunctionalInterface
public interface EventBuilder<T, A extends Attributes> {

	/**
	 * Builds a new event using 'data', 'attributes' and 'extensions'
	 */
	CloudEvent<A, T> build(T data, A attributes, 
			Collection<ExtensionFormat> extensions);
	
}
