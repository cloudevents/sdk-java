package io.cloudevents.v1.http;

import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Objects;
import java.util.AbstractMap.SimpleEntry;
import java.util.Map.Entry;
import java.util.stream.Collectors;

import io.cloudevents.fun.FormatExtensionMapper;
import io.cloudevents.v1.ContextAttributes;
import io.cloudevents.v1.http.AttributeMapper;

/**
 * 
 * @author fabiojose
 * @version 1.0
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
