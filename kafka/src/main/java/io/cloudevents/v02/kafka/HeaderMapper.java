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

import static io.cloudevents.v02.kafka.AttributeMapper.HEADER_PREFIX;

import java.util.AbstractMap.SimpleEntry;
import java.util.Locale;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Objects;
import java.util.stream.Collectors;

import io.cloudevents.fun.BinaryFormatHeaderMapper;

/**
 * 
 * @author fabiojose
 *
 */
public class HeaderMapper {
	private HeaderMapper() {}

	/**
	 * Following the signature of {@link BinaryFormatHeaderMapper}
	 * @param attributes The map of attributes created by {@link AttributeMapper}
	 * @param extensions The map of extensions created by {@link ExtensionMapper}
	 * @return The map of HTTP Headers
	 */
	public static Map<String, Object> map(Map<String, String> attributes,
			Map<String, String> extensions) {
		Objects.requireNonNull(attributes);
		Objects.requireNonNull(extensions);
		
		Map<String, Object> result = attributes.entrySet()
			.stream()
			.filter(attribute -> null!= attribute.getValue())
			.map(header -> new SimpleEntry<>(header.getKey()
					.toLowerCase(Locale.US), header.getValue()))
			.map(header -> new SimpleEntry<>(HEADER_PREFIX+header.getKey(),
					header.getValue()))
			.collect(Collectors.toMap(Entry::getKey, Entry::getValue));
		
		result.putAll(
			extensions.entrySet()
				.stream()
				.filter(extension -> null!= extension.getValue())
				.collect(Collectors.toMap(Entry::getKey, Entry::getValue))
		);
		
		return result;
	}
}
