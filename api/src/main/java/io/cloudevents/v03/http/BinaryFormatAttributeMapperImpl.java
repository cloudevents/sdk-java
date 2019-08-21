package io.cloudevents.v03.http;

import static java.util.stream.Collectors.toMap;

import java.util.Locale;
import java.util.Map;
import java.util.Objects;
import java.util.AbstractMap.SimpleEntry;
import java.util.Map.Entry;
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
		
		result.put("datacontenttype", ct.get().getValue().toString());
			
		return result;
	}
	
}
