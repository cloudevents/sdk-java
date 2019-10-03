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
package io.cloudevents.json;

import static org.junit.Assert.assertNull;

import java.util.Map;

import org.junit.Test;

import com.fasterxml.jackson.core.type.TypeReference;

/**
 * 
 * @author fabiojose
 *
 */
public class JsonTest {

	@Test
	public void should_result_null_on_decode_type_empty_string() {
		// setup
		String payload = "";
		
		// act
		Object actual = Json.decodeValue(payload, Map.class);
		
		// assert
		assertNull(actual);
	}
	
	@Test
	public void should_result_null_on_decode_type_null_string() {
	
		// act
		Object actual = Json.decodeValue(null, Map.class);
		
		// assert
		assertNull(actual);
	}
	
	@Test
	public void should_result_null_on_decode_typereference_empty_string() {
		// setup
		String payload = "";
		
		// act
		Object actual = Json.decodeValue(payload, new TypeReference<Map<String, Object>>() {});
		
		// assert
		assertNull(actual);
	}
	
	@Test
	public void should_result_null_on_decode_typereference_null_string() {
	
		// act
		Object actual = Json.decodeValue(null, new TypeReference<Map<String, Object>>() {});
		
		// assert
		assertNull(actual);
	}
}
