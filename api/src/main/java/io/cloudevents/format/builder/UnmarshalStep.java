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
package io.cloudevents.format.builder;

import io.cloudevents.Attributes;
import io.cloudevents.CloudEvent;

/**
 * 
 * @author fabiojose
 *
 * @param <A> The attributes type
 * @param <T> The 'data' type
 */
public interface UnmarshalStep<A extends Attributes, T>  {

	/**
	 * Unmarshals the payload and headers to {@link CloudEvent} instance
	 * @return New cloud event instance
	 */
	CloudEvent<A, T> unmarshal();
	
}
