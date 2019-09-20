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
package io.cloudevents.v03.kafka;

import static java.util.stream.Collectors.toMap;

import java.util.Locale;
import java.util.Map;
import java.util.Objects;
import java.util.AbstractMap.SimpleEntry;
import java.util.Map.Entry;

import org.apache.kafka.common.serialization.Deserializer;
import org.apache.kafka.common.serialization.Serdes;

import io.cloudevents.fun.BinaryFormatAttributeMapper;
import io.cloudevents.v03.ContextAttributes;

/**
 * 
 * @author fabiojose
 * @version 0.3
 */
public class AttributeMapper {

	static final String HEADER_PREFIX = "ce_";
	
	private static final Deserializer<String> DESERIALIZER = 
			Serdes.String().deserializer();
	
	private static final String NULL_ARG = null;
	
	/**
	 * Following the signature of {@link BinaryFormatAttributeMapper#map(Map)}
	 * @param headers Map of Kafka headers
	 * @return Map with spec attributes and values without parsing
	 * @see ContextAttributes
	 */
	public static Map<String, String> map(Map<String, Object> headers) {
		Objects.requireNonNull(headers);
		
		return headers.entrySet()
			.stream()
			.filter(header -> null!= header.getValue())
			.map(header -> new SimpleEntry<>(header.getKey()
					.toLowerCase(Locale.US), header.getValue()))
			.map(header -> new SimpleEntry<>(header.getKey(),
					(byte[])header.getValue()))
			.map(header -> {
				
				String key = header.getKey();
				key = key.substring(HEADER_PREFIX.length());

				String val = DESERIALIZER.deserialize(NULL_ARG,
						header.getValue());
				return new SimpleEntry<>(key, val);
			})
			.collect(toMap(Entry::getKey, Entry::getValue));
	}

}
