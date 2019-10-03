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

import java.util.Map;
import java.util.function.Supplier;

import io.cloudevents.Attributes;

/**
 * 
 * @author fabiojose
 *
 * @param <A> The attributes type
 * @param <T> The 'data' type
 * @param <P> The payload type
 */
public interface HeadersStep<A extends Attributes, T, P> {

	/**
	 * Supplies a map of headers to be used by the unmarshaller
	 * @param headers
	 * @return The next step of builder
	 */
	PayloadStep<A, T, P> withHeaders(Supplier<Map<String, Object>> headers);
	
}
