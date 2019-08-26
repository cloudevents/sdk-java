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
import java.util.stream.Collectors;
import java.util.Locale;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Objects;

import io.cloudevents.fun.BinaryFormatHeaderMapper;

import static io.cloudevents.v02.http.BinaryFormatAttributeMapperImpl.HEADER_PREFIX;

/**
 * 
 * @author fabiojose
 *
 */
public class BinaryFormatHeaderMapperImpl implements BinaryFormatHeaderMapper {

	@Override
	public Map<String, Object> map(Map<String, String> attributes) {
		Objects.requireNonNull(attributes);
		
		Map<String, Object> result = attributes.entrySet()
			.stream()
			.map(header -> new SimpleEntry<>(header.getKey()
					.toLowerCase(Locale.US), header.getValue()))
			.filter(header -> !header.getKey().equals("contenttype"))
			.map(header -> new SimpleEntry<>(HEADER_PREFIX+header.getKey(), header.getValue()))
			.collect(Collectors.toMap(Entry::getKey, Entry::getValue));
		
		result.put("Content-Type", attributes.get("contenttype"));
		
		return result;
	}

}