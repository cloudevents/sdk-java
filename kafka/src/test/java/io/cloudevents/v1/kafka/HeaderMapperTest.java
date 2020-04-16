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

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.HashMap;
import java.util.Map;

import org.junit.jupiter.api.Test;

import io.cloudevents.v1.ContextAttributes;

/**
 *
 * @author fabiojose
 *
 */
public class HeaderMapperTest {
	@Test
	public void error_when_attributes_map_isnull() {
		// setup
		Map<String, String> extensions = new HashMap<>();

		assertThrows(NullPointerException.class, () -> {
			// act
			HeaderMapper.map(null, extensions);
		});
	}

	@Test
	public void error_when_extensions_map_isnull() {
		// setup
		Map<String, String> attributes = new HashMap<>();

		assertThrows(NullPointerException.class, () -> {
			// act
			HeaderMapper.map(attributes, null);
		});
	}

	@Test
	public void should_not_map_null_attribute_value() {
		// setup
		Map<String, String> attributes = new HashMap<>();
		attributes.put("type", null);
		attributes.put("specversion", "1.0");

		Map<String, String> extensions = new HashMap<>();

		// act
		Map<String, byte[]> actual = HeaderMapper.map(attributes, extensions);

		//assert
		assertFalse(actual.containsKey("ce-type"));
	}

	@Test
	public void should_map_datacontenttype_to_content_type() {
		// setup
		Map<String, String> attributes = new HashMap<>();
		attributes.put(ContextAttributes.DATACONTENTTYPE.name(), "application/json");

		Map<String, String> extensions = new HashMap<>();

		// act
		Map<String, byte[]> actual = HeaderMapper.map(attributes, extensions);

		//assert
		assertTrue(actual.containsKey("content-type"));
		assertEquals("application/json", new String(actual.get("content-type")));
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
		Map<String, byte[]> actual = HeaderMapper.map(attributes, extensions);

		//assert
		assertFalse(actual.containsKey("null-ext"));
	}

	@Test
	public void should_not_map_absent_contenttype() {
		// setup
		Map<String, String> attributes = new HashMap<>();
		attributes.put("type", "mytype");
		attributes.put("specversion", "1.0");

		Map<String, String> extensions = new HashMap<>();
		extensions.put("null-ext", "null-value");
		extensions.put("comexampleextension1", "value");

		// act
		Map<String, byte[]> actual = HeaderMapper.map(attributes, extensions);

		//assert
		assertFalse(actual.containsKey("Content-Type"));
	}

	@Test
	public void should_map_extension_with_prefix() {
		// setup
		Map<String, String> attributes = new HashMap<>();
		attributes.put("type", "mytype");
		attributes.put("specversion", "1.0");

		Map<String, String> extensions = new HashMap<>();
		extensions.put("null-ext", "null-value");
		extensions.put("comexampleextension1", "value");

		// act
		Map<String, byte[]> actual = HeaderMapper.map(attributes, extensions);

		//assert
		assertTrue(actual.containsKey("ce_comexampleextension1"));
	}

	@Test
	public void should_all_values_as_byte_array() {
		// setup
		Map<String, String> attributes = new HashMap<>();
		attributes.put("type", "mytype");
		attributes.put("specversion", "1.0");

		Map<String, String> extensions = new HashMap<>();
		extensions.put("null-ext", "null-value");
		extensions.put("comexampleextension1", "value");

		// act
		Map<String, byte[]> actuals = HeaderMapper.map(attributes, extensions);

		// assert
		actuals.values()
			.forEach(actual -> {
				assertTrue(actual instanceof byte[]);
			});
	}
}
