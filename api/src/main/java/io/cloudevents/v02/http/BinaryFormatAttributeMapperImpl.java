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

import static java.util.stream.Collectors.toMap;

import java.util.Map;
import java.util.AbstractMap.SimpleEntry;
import java.util.Locale;
import java.util.Map.Entry;
import java.util.Objects;
import java.util.concurrent.atomic.AtomicReference;

import io.cloudevents.fun.BinaryFormatAttributeMapper;

/**
 * 
 * @author fabiojose
 *
 */
public class BinaryFormatAttributeMapperImpl implements BinaryFormatAttributeMapper {
	
	public static final String HEADER_PREFIX = "ce-";

	@Override
	public Map<String, String> map(final Map<String, Object> headers) {
		Objects.requireNonNull(headers);
		
		headers.keySet()
			.stream()
			.map(header -> header.toLowerCase(Locale.US))
			.filter(header -> header.startsWith(HEADER_PREFIX))
			.map(header -> header.substring(HEADER_PREFIX.length()));
		
		final AtomicReference<Entry<String, Object>> ct = new AtomicReference<>();
		Map<String, String> result = headers.entrySet()
			.stream()
			.map(header -> new SimpleEntry<>(header.getKey().toLowerCase(Locale.US),
					header.getValue()))
			.peek(header -> {
				if("content-type".equals(header.getKey())) {
					ct.set(header);
				}
			})
			.filter(header -> header.getKey().startsWith(HEADER_PREFIX))
			.map(header -> new SimpleEntry<>(header.getKey().substring(HEADER_PREFIX.length()), header.getValue()))
			.map(header -> new SimpleEntry<>(header.getKey(), header.getValue().toString()))
			.collect(toMap(Entry::getKey, Entry::getValue));
		
		result.put("contenttype", ct.get().getValue().toString());
			
		return result;
	}

}
