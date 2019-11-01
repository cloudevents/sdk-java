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
package io.cloudevents.v1.kafka;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;

import java.util.HashMap;
import java.util.Map;

import org.junit.jupiter.api.Test;

import io.cloudevents.v1.kafka.AttributeMapper;

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
		headers.put("ce_specversion", "1.0");

		assertThrows(ClassCastException.class, () -> {
			AttributeMapper.map(headers);
		});
	}
	
	@Test
	public void should_map_cespecversion_to_specversion() {
		// setup
		Map<String, Object> headers = new HashMap<>();
		headers.put("ce_specversion", "1.0".getBytes());
		
		String expected = "specversion";
		
		// act
		Map<String, String> attributes = 
				AttributeMapper.map(headers);
		
		// assert
		assertNotNull(attributes.get(expected));
	}
	
	@Test
	public void should_not_map_null_value() {
		// setup
		Map<String, Object> headers = new HashMap<>();
		headers.put("ce_type", null);
		
		String expected = "type";
		
		// act
		Map<String, String> attributes = 
				AttributeMapper.map(headers);
		
		// assert
		assertFalse(attributes.containsKey(expected));
	}
	
	@Test
	public void should_all_without_prefix_ce() {
		// setup
		Map<String, Object> myHeaders = new HashMap<>();
		myHeaders.put("ce_id", "0x11".getBytes());
		myHeaders.put("ce_source", "/source".getBytes());
		myHeaders.put("ce_specversion", "1.0".getBytes());
		myHeaders.put("ce_type", "br.my".getBytes());
		myHeaders.put("ce_time", "2019-09-16T20:49:00Z".getBytes());
		myHeaders.put("ce_dataschema", "http://my.br".getBytes());
		myHeaders.put("ce_subject", "subject".getBytes());
		myHeaders.put("Content-Type", "application/json".getBytes());
		
		Map<String, String> actual = AttributeMapper.map(myHeaders);
		
		actual.keySet()
			.forEach((attribute) -> {
				assertFalse(attribute.startsWith("ce_"));
			});
	}
}
