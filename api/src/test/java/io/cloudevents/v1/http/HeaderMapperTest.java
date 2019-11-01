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

import static org.junit.Assert.assertFalse;

import java.util.HashMap;
import java.util.Map;

import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;

import io.cloudevents.v1.http.HeaderMapper;

/**
 * 
 * @author fabiojose
 *
 */
public class HeaderMapperTest {
	@Rule
	public ExpectedException expectedEx = ExpectedException.none();

	@Test
	public void error_when_attributes_map_isnull() {
		// setup 
		expectedEx.expect(NullPointerException.class);
		
		Map<String, String> extensions = new HashMap<>();
		
		// act
		HeaderMapper.map(null, extensions);
	}
	
	@Test
	public void error_when_extensions_map_isnull() {
		// setup 
		expectedEx.expect(NullPointerException.class);
		
		Map<String, String> attributes = new HashMap<>();
		
		// act
		HeaderMapper.map(attributes, null);
	}
	
	@Test
	public void should_not_map_null_attribute_value() {
		// setup
		Map<String, String> attributes = new HashMap<>();
		attributes.put("type", null);
		attributes.put("specversion", "1.0");
		
		Map<String, String> extensions = new HashMap<>();
		
		// act
		Map<String, String> actual = HeaderMapper.map(attributes, extensions);
		
		//assert
		assertFalse(actual.containsKey("ce-type"));
	}
	
	@Test
	public void should_not_map_null_extension_value() {
		// setup
		Map<String, String> attributes = new HashMap<>();
		attributes.put("type", "mytype");
		attributes.put("specversion", "1.0");
		
		Map<String, String> extensions = new HashMap<>();
		extensions.put("null-ext", null);
		extensions.put("comexampleextension1", "value");
		
		// act
		Map<String, String> actual = HeaderMapper.map(attributes, extensions);
		
		//assert
		assertFalse(actual.containsKey("ce-null-ext"));
	}
	
	@Test
	public void should_not_map_absent_datacontenttype() {
		// setup
		Map<String, String> attributes = new HashMap<>();
		attributes.put("type", "mytype");
		attributes.put("specversion", "1.0");
		
		Map<String, String> extensions = new HashMap<>();
		extensions.put("null-ext", "null-value");
		extensions.put("comexampleextension1", "value");
		
		// act
		Map<String, String> actual = HeaderMapper.map(attributes, extensions);
		
		//assert
		assertFalse(actual.containsKey("Content-Type"));	
	}
}
