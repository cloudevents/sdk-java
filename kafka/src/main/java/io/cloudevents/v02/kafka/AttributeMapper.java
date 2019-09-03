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

import static java.util.stream.Collectors.toMap;

import java.util.AbstractMap.SimpleEntry;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Objects;

import org.apache.kafka.common.serialization.Deserializer;
import org.apache.kafka.common.serialization.Serdes;

/**
 * 
 * @author fabiojose
 * @version 0.2
 */
public final class AttributeMapper {
	private AttributeMapper() {}
	
	static final String HEADER_PREFIX = "ce_";
	
	private static final Deserializer<String> DESERIALIZER = 
			Serdes.String().deserializer();
	
	private static final String NULL_ARG = null;
	
	/**
	 * Maps the headers into the map of attributes
	 * 
	 * @param headers
	 * @return
	 */
	public static Map<String, String> map(Map<String, Object> headers) {
		Objects.requireNonNull(headers);
		
		return headers.entrySet()
			.stream()
			.map(header -> new SimpleEntry<>(header.getKey(),
					(byte[])header.getValue()))
			.map(header -> {
				
				String key = header.getKey();
				key = key.substring(HEADER_PREFIX.length());

				String val = DESERIALIZER.deserialize(NULL_ARG, header.getValue());
				return new SimpleEntry<>(key, val);
			})
			.collect(toMap(Entry::getKey, Entry::getValue));
	}

}
