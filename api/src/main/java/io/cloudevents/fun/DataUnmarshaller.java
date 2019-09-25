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

import io.cloudevents.Attributes;

/**
 * For the 'data' unmarshalling.
 * 
 * @author fabiojose
 *
 * @param <P> The payload type
 * @param <T> The 'data' type
 */
@FunctionalInterface
public interface DataUnmarshaller<P, T, A extends Attributes> {

	/**
	 * Unmarshals the payload into 'data'
	 * @param payload
	 * @param attributes
	 * @return
	 * @throws RuntimeException If something bad happens during the umarshal
	 */
	T unmarshal(P payload, A attributes);
	
}
