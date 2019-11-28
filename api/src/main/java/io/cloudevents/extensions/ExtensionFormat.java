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
package io.cloudevents.extensions;

import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Objects;
import java.util.stream.Collectors;

/**
 * Defines a way to add custom extension in the abstract envelop.
 * 
 * @author fabiojose
 *
 */
public interface ExtensionFormat {

	/**
	 * The in-memory format to be used by the structured content mode.
	 */
	InMemoryFormat memory();
	
	/**
	 * The transport format to be used by the binary content mode.
	 */
	Map<String, String> transport();
	
	static ExtensionFormat of(final InMemoryFormat inMemory,
			final String key, final String value) {
		
		final Map<String, String> transport = new HashMap<>();
		transport.put(key, value);
		
		return new ExtensionFormat() {
			@Override
			public InMemoryFormat memory() {
				return inMemory;
			}

			@Override
			public Map<String, String> transport() {
				return Collections.unmodifiableMap(transport);
			}
		};
	}
	
	@SafeVarargs
	static ExtensionFormat of(final InMemoryFormat inMemory,
			Entry<String, String> ... transport){
		Objects.requireNonNull(inMemory);
		Objects.requireNonNull(transport);
		
		final Map<String, String> transports = Arrays.asList(transport)
			.stream()
			.collect(Collectors.toMap(Entry::getKey, Entry::getValue));
			
		return new ExtensionFormat() {
			@Override
			public InMemoryFormat memory() {
				return inMemory;
			}

			@Override
			public Map<String, String> transport() {
				return transports;
			}
		};
	}
	
	/**
	 * Marshals a collection of {@link ExtensionFormat} to {@code Map<String, String>}
	 * @param extensions
	 * @return
	 */
	static Map<String, String> marshal(Collection<ExtensionFormat>
		extensions) {
		
		return extensions.stream()
				.map(ExtensionFormat::transport)
				.flatMap(t -> t.entrySet().stream())
				.collect(Collectors.toMap(Entry::getKey,
						Entry::getValue));
	}
}
