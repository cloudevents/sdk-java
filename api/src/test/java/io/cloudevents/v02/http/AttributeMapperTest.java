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


import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;

import java.util.HashMap;
import java.util.Map;

import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;

/**
 * 
 * @author fabiojose
 *
 */
public class AttributeMapperTest {
	
	@Rule
	public ExpectedException expectedEx = ExpectedException.none();

	@Test
	public void error_when_headers_map_isnull() {
		// setup 
		expectedEx.expect(NullPointerException.class);
		
		// act
		AttributeMapper.map(null);
	}
	
	@Test
	public void should_not_map_null_header_value_to_attribute() {
		// setup
		Map<String, Object> headers = new HashMap<>();
		headers.put("ce_specversion", null);
		
		// act
		Map<String, String> actual = AttributeMapper.map(headers);
		
		// assert
		assertFalse(actual.containsKey("specversion"));
	}
	
	@Test
	public void should_ok_when_no_content_type() {
		// setup
		Map<String, Object> headers = new HashMap<>();
		headers.put("ce-specversion", "0.2");

		// act
		Map<String, String> attributes = 
				AttributeMapper.map(headers);
		
		// assert
		assertFalse(attributes.isEmpty());
	}
	
	@Test
	public void should_map_cespecversion_to_specversion() {
		// setup
		Map<String, Object> headers = new HashMap<>();
		headers.put("ce-specversion", "0.2");
		headers.put("Content-Type", "application/json");
		
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
		headers.put("ce-type", null);
		
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
		myHeaders.put("ce-id", "0x11");
		myHeaders.put("ce-source", "/source");
		myHeaders.put("ce-specversion", "0.2");
		myHeaders.put("ce-type", "br.my");
		myHeaders.put("ce-time", "2019-09-16T20:49:00Z");
		myHeaders.put("ce-schemaurl", "http://my.br");
		myHeaders.put("Content-Type", "application/json");
		
		Map<String, String> actual = AttributeMapper.map(myHeaders);
		
		actual.keySet()
			.forEach((attribute) -> {
				assertFalse(attribute.startsWith("ce-"));
			});
	}
}
