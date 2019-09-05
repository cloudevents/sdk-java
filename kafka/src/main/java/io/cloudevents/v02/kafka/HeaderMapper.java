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

import org.apache.kafka.common.serialization.Serdes;
import org.apache.kafka.common.serialization.Serializer;

import io.cloudevents.extensions.ExtensionFormat;
import io.cloudevents.fun.BinaryFormatHeaderMapper;
import io.cloudevents.v02.AttributesImpl;

/**
 * 
 * @author fabiojose
 *
 */
public class HeaderMapper {
	private HeaderMapper() {}
	
	private static final Serializer<String> SERIALIZER = 
		Serdes.String().serializer();

	/**
	 * Following the signature of {@link BinaryFormatHeaderMapper}
	 * @param attributes The map of attributes created by 
	 * {@link AttributesImpl#marshal(AttributesImpl)}
	 * @param extensions The map of extensions created by 
	 * {@link ExtensionFormat#marshal(java.util.Collection)}
	 * @return The map of Kafka Headers with values as {@code byte[]}
	 */
	public static Map<String, Object> map(Map<String, String> attributes,
			Map<String, String> extensions) {
		Objects.requireNonNull(attributes);
		Objects.requireNonNull(extensions);
		
		Map<String, Object> result = attributes.entrySet()
			.stream()
			.filter(attribute -> null!= attribute.getValue())
			.map(attribute -> 
				new SimpleEntry<>(attribute.getKey()
					.toLowerCase(Locale.US), attribute.getValue()))
			.map(attribute -> 
				new SimpleEntry<>(HEADER_PREFIX+attribute.getKey(),
					attribute.getValue()))
			.map(attribute -> 
				new SimpleEntry<>(attribute.getKey(), 
						SERIALIZER.serialize(null, attribute.getValue())))
			.collect(Collectors.toMap(Entry::getKey, Entry::getValue));
		
		result.putAll(
			extensions.entrySet()
				.stream()
				.filter(extension -> null!= extension.getValue())
				.map(extension -> 
					new SimpleEntry<>(extension.getKey(), 
						SERIALIZER.serialize(null, extension.getValue())))
				.collect(Collectors.toMap(Entry::getKey, Entry::getValue))
		);
		
		return result;
	}
}
