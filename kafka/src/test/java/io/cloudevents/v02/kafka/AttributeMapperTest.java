package io.cloudevents.v02.kafka;

import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;

import java.util.HashMap;
import java.util.Map;

import org.junit.jupiter.api.Test;

import io.cloudevents.v02.kafka.AttributeMapper;

/**
 * 
 * @author fabiojose
 *
 */
public class AttributeMapperTest {

	@Test
	public void error_when_headers_map_isnull() {
		// act
		assertThrows(NullPointerException.class, () -> {
			AttributeMapper.map(null);
		});
	}
	
	@Test
	public void error_when_header_value_is_not_byte_array() {
		// setup
		Map<String, Object> headers = new HashMap<>();
		headers.put("ce_specversion", "0.2");

		assertThrows(ClassCastException.class, () -> {
			AttributeMapper.map(headers);
		});
	}
	
	@Test
	public void should_map_cespecversion_to_specversion() {
		// setup
		Map<String, Object> headers = new HashMap<>();
		headers.put("ce_specversion", "0.2".getBytes());
		
		String expected = "specversion";
		
		// act
		Map<String, String> attributes = 
				AttributeMapper.map(headers);
		
		// assert
		assertNotNull(attributes.get(expected));
	}
	
	public void should_all_without_prefix_ce() {
		
	}
}
