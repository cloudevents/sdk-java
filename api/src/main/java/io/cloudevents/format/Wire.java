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
package io.cloudevents.format;

import java.util.Collections;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;

/**
 * Represents a result of binary marshal, to be used by the wire transfer
 * 
 * @author fabiojose
 * @param <T> The payload type
 * @param <K> The header key type
 * @param <V> The header value type
 */
public class Wire<T, K, V> {

	private final T payload;
	private final Map<K, V> headers;
	
	public Wire(T payload, Map<K, V> headers) {
		Objects.requireNonNull(headers);
		this.payload = payload;
		this.headers = headers;
	}

	/**
	 * The payload
	 */
	public Optional<T> getPayload() {
		return Optional.ofNullable(payload);
	}
	
	/**
	 * The headers
	 */
	public Map<K, V> getHeaders() {
		return Collections.unmodifiableMap(headers);
	}
}
