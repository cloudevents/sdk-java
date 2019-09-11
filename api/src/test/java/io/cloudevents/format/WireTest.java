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
package io.cloudevents.format;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

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
public class WireTest {
	
	@Rule
	public ExpectedException expectedEx = ExpectedException.none();

	@Test
	public void throws_error_when_null_headers() {
		
		// setup
		expectedEx.expect(NullPointerException.class);
		
		new Wire<String, String, Object>("payload", null);
	}
	
	@Test
	public void throws_when_try_to_change_headers() {
		// setup
		expectedEx.expect(UnsupportedOperationException.class);
		
		Map<String, Object> headers = new HashMap<>();
		headers.put("contenttype", "application/json");
		
		// act
		Wire<String, String, Object> wire = new Wire<>("payload", headers);
		
		wire.getHeaders().put("my-header", "my-header-val");
	}
	
	@Test
	public void should_ok_when_null_payload() {
		Wire<String, String, Object> expected = 
				new Wire<>(null, new HashMap<>());
		
		assertFalse(expected.getPayload().isPresent());
	}
	
	@Test
	public void should_ok_when_payload_not_null() {
		Wire<String, String, Object> actual = 
				new Wire<>("payload", new HashMap<>());
		
		assertTrue(actual.getPayload().isPresent());
		assertEquals("payload", actual.getPayload().get());
	}
	
}
