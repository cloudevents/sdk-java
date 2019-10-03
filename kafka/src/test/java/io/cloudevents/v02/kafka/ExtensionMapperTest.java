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

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.HashMap;
import java.util.Map;

import org.junit.jupiter.api.Test;

/**
 * 
 * @author fabiojose
 *
 */
public class ExtensionMapperTest {
	
	@Test
	public void error_when_headers_map_isnull() {
		// act
		assertThrows(NullPointerException.class, () -> {
			ExtensionMapper.map(null);
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
	public void should_not_map_null_values() {
		//setuṕ
		String expected = "nullexp";
		
		Map<String, Object> myHeaders = new HashMap<>();
		myHeaders.put("ce_id", "0x11".getBytes());
		myHeaders.put("ce_source", "/source".getBytes());
		myHeaders.put("ce_specversion", "0.2".getBytes());
		myHeaders.put("ce_type", "br.my".getBytes());
		myHeaders.put("my-ext", "myextension".getBytes());
		myHeaders.put("traceparent", "0".getBytes());
		myHeaders.put("tracestate", "congo=4".getBytes());
		myHeaders.put("Content-Type", "application/json".getBytes());
		myHeaders.put(expected, null);
		
		// act
		Map<String, String> actual = ExtensionMapper.map(myHeaders);
		
		
		// assert
		assertFalse(actual.containsKey(expected));
	}
	
	@Test
	public void should_result_an_empty_map_when_no_extensions() {
		// setup
		Map<String, Object> headers = new HashMap<>();
		
		//act
		Map<String, String> actual = ExtensionMapper.map(headers);
		
		//assert
		assertTrue(actual.isEmpty());
	}
	
	@Test
	public void should_result_myexp() {
		// setup
		String expected = "my-extension";
		Map<String, Object> headers = new HashMap<>();
		headers.put("myexp", "my-extension".getBytes());
		
		//act
		String actual = ExtensionMapper.map(headers).get("myexp");
		
		//assert
		assertEquals(expected, actual);
	}
	
	@Test
	public void should_return_just_potential_extensions() {
		// setup
		Map<String, Object> myHeaders = new HashMap<>();
		myHeaders.put("ce_id", "0x11".getBytes());
		myHeaders.put("ce_source", "/source".getBytes());
		myHeaders.put("ce_specversion", "0.2".getBytes());
		myHeaders.put("ce_type", "br.my".getBytes());
		myHeaders.put("ce_time", "2019-09-16T20:49:00Z".getBytes());
		myHeaders.put("ce_schemaurl", "http://my.br".getBytes());
		myHeaders.put("my-ext", "myextension".getBytes());
		myHeaders.put("traceparent", "0".getBytes());
		myHeaders.put("tracestate", "congo=4".getBytes());
		myHeaders.put("Content-Type", "application/json".getBytes());
		
		// act
		Map<String, String> actual = ExtensionMapper.map(myHeaders);
		
		// asset
		assertFalse(actual.isEmpty());
		assertEquals(3, actual.keySet().size());
		actual.keySet()
			.forEach(header -> {
				assertFalse(header.startsWith("ce_"));
			});

		assertEquals("0", actual.get("traceparent"));
		assertEquals("congo=4", actual.get("tracestate"));
		assertEquals("myextension", actual.get("my-ext"));
	}
}
