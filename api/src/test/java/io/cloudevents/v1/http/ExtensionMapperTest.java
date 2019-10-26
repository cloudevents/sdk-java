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
package io.cloudevents.v1.http;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;

import java.util.HashMap;
import java.util.Map;

import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;

import io.cloudevents.v1.http.ExtensionMapper;

/**
 * 
 * @author fabiojose
 *
 */
public class ExtensionMapperTest {
	@Rule
	public ExpectedException expectedEx = ExpectedException.none();

	@Test
	public void error_when_headers_map_isnull() {
		// setup 
		expectedEx.expect(NullPointerException.class);
		
		// act
		ExtensionMapper.map(null);
	}
	
	@Test
	public void should_not_map_null_values() {
		//setuṕ
		String expected = "nullexp";
		
		Map<String, Object> myHeaders = new HashMap<>();
		myHeaders.put("ce-id", "0x11");
		myHeaders.put("ce-source", "/source");
		myHeaders.put("ce-specversion", "1.0");
		myHeaders.put("ce-type", "br.my");
		myHeaders.put("ce-time", "2019-09-16T20:49:00Z");
		myHeaders.put("ce-dataschema", "http://my.br");
		myHeaders.put("my-ext", "myextension");
		myHeaders.put("traceparent", "0");
		myHeaders.put("tracestate", "congo=4");
		myHeaders.put("Content-Type", "application/json");
		myHeaders.put(expected, null);
		
		// act
		Map<String, String> actual = ExtensionMapper.map(myHeaders);
		
		
		// assert
		assertFalse(actual.containsKey(expected));
	}
	
	@Test
	public void should_return_just_potential_extensions() {
		// setup
		Map<String, Object> myHeaders = new HashMap<>();
		myHeaders.put("ce-id", "0x11");
		myHeaders.put("ce-source", "/source");
		myHeaders.put("ce-specversion", "1.0");
		myHeaders.put("ce-type", "br.my");
		myHeaders.put("ce-time", "2019-09-16T20:49:00Z");
		myHeaders.put("ce-dataschema", "http://my.br");
		myHeaders.put("my-ext", "myextension");
		myHeaders.put("traceparent", "0");
		myHeaders.put("tracestate", "congo=4");
		myHeaders.put("Content-Type", "application/json");
		
		// act
		Map<String, String> actual = ExtensionMapper.map(myHeaders);
		
		// asset
		assertFalse(actual.isEmpty());
		assertEquals(3, actual.keySet().size());
		actual.keySet()
			.forEach(header -> {
				assertFalse(header.startsWith("ce-"));
			});
		
		assertEquals("0", actual.get("traceparent"));
		assertEquals("congo=4", actual.get("tracestate"));
		assertEquals("myextension", actual.get("my-ext"));
	}
}
