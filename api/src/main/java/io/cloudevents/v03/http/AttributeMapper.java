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
package io.cloudevents.v03.http;

import static java.util.stream.Collectors.toMap;

import java.util.Locale;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.AbstractMap.SimpleEntry;
import java.util.Map.Entry;
import java.util.concurrent.atomic.AtomicReference;

import io.cloudevents.fun.BinaryFormatAttributeMapper;
import io.cloudevents.v03.ContextAttributes;

/**
 *
 * @author fabiojose
 * @version 0.3
 */
public class AttributeMapper {
	private AttributeMapper() {}

	static final String HEADER_PREFIX = "ce-";

	/**
	 * Following the signature of {@link BinaryFormatAttributeMapper#map(Map)}
	 * @param headers Map of HTTP request
	 * @return Map with spec attributes and values without parsing
	 * @see ContextAttributes
	 */
	public static Map<String, String> map(final Map<String, Object> headers) {
		Objects.requireNonNull(headers);

		final AtomicReference<Optional<Entry<String, Object>>> ct =
				new AtomicReference<>();

		ct.set(Optional.empty());

		Map<String, String> result = headers.entrySet()
			.stream()
			.filter(header -> null!= header.getValue())
			.map(header -> new SimpleEntry<>(header.getKey()
					.toLowerCase(Locale.US), header.getValue()))
			.peek(header -> {
				if("content-type".equals(header.getKey())) {
					ct.set(Optional.ofNullable(header));
				}
			})
			.filter(header -> header.getKey().startsWith(HEADER_PREFIX))
			.map(header -> new SimpleEntry<>(header.getKey()
					.substring(HEADER_PREFIX.length()), header.getValue()))
			.map(header -> new SimpleEntry<>(header.getKey(),
					header.getValue().toString()))
			.collect(toMap(Entry::getKey, Entry::getValue));

		ct.get().ifPresent(contentType -> {
			result.put(ContextAttributes.DATACONTENTTYPE.name(),
					contentType.getValue().toString());
		});

		return result;
	}

}
