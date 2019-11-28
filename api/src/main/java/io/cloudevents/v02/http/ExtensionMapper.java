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

import java.util.AbstractMap.SimpleEntry;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Objects;
import java.util.stream.Collectors;

import io.cloudevents.fun.FormatExtensionMapper;
import io.cloudevents.v02.ContextAttributes;

/**
 * 
 * @author fabiojose
 * @version 0.2
 */
public class ExtensionMapper {
	private ExtensionMapper() {}
	
	private static final List<String> RESERVED_HEADERS = 
		ContextAttributes.VALUES.stream()
			.map(attribute -> AttributeMapper
					.HEADER_PREFIX + attribute)
			.collect(Collectors.toList());
	static {
		RESERVED_HEADERS.add("content-type");
	};

	/**
	 * Following the signature of {@link FormatExtensionMapper}
	 * @param headers The HTTP headers
	 * @return The potential extensions without parsing
	 */
	public static Map<String, String> map(Map<String, Object> headers) {
		Objects.requireNonNull(headers);
		
		// remove all reserved words and the remaining may be extensions
		return 
		headers.entrySet()
			.stream()
			.filter(header -> null!= header.getValue())
			.map(header -> new SimpleEntry<>(header.getKey()
					.toLowerCase(Locale.US), header.getValue().toString()))
			.filter(header -> !RESERVED_HEADERS.contains(header.getKey()))
			.collect(Collectors.toMap(Entry::getKey, Entry::getValue));
	}

}
