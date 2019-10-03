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

import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;

import io.cloudevents.Attributes;
import io.cloudevents.json.types.Much;

/**
 * 
 * @author fabiojose
 *
 */
public class StructuredUnmarshallerTest {

	@Rule
	public ExpectedException expectedEx = ExpectedException.none();
	
	@Test
	public void should_throw_on_null_extension_mapper() {
		// setup
		expectedEx.expect(NullPointerException.class);
		
		StructuredUnmarshaller.<Attributes, Much, String>builder()
			.map(null);
	}
	
	@Test
	public void should_ok_on_extension_mapper() {
		StructuredUnmarshaller.<Attributes, Much, String>builder()
			.map((headers) -> {
				
				return null;
			});
	}
	
	@Test
	public void should_throw_on_null_extension_unmarshaller() {
		// setup
		expectedEx.expect(NullPointerException.class);
		
		StructuredUnmarshaller.<Attributes, Much, String>builder()
			.map((headers) -> {
				
				return null;
			})
			.map(null);
	}
	
	@Test
	public void should_ok_on_extension_unmarshaller() {
		StructuredUnmarshaller.<Attributes, Much, String>builder()
			.map((headers) -> {
				
				return null;
			})
			.map((extensions) -> {
				return null;
			});
	}
	
	@Test
	public void should_throw_on_null_envelope_unmarshaller() {
		// setup
		expectedEx.expect(NullPointerException.class);
		
		StructuredUnmarshaller.<Attributes, Much, String>builder()
			.map((headers) -> {
				
				return null;
			})
			.map((extensions) -> {
				return null;
			})
			.next()
			.map(null);
	}
	
	@Test
	public void should_ok_on_envelope_unmarshaller() {
		StructuredUnmarshaller.<Attributes, Much, String>builder()
			.map((headers) -> null)
			.map((extensions) -> null)
			.next()
			.map((payload, extensions) -> null);
	}
	
	@Test
	public void should_throw_on_null_headers() {
		// setup
		expectedEx.expect(NullPointerException.class);
		
		StructuredUnmarshaller.<Attributes, Much, String>builder()
		.map((headers) -> null)
		.map((extensions) -> null)
		.next()
		.map((payload, extensions) -> null)
		.withHeaders(null);
	}
	
	@Test
	public void should_ok_on_headers() {
		StructuredUnmarshaller.<Attributes, Much, String>builder()
		.map((headers) -> null)
		.map((extensions) -> null)
		.next()
		.map((payload, extensions) -> null)
		.withHeaders(() -> null);
	}
	
	@Test
	public void should_throw_on_null_payload_supplier() {
		// setup
		expectedEx.expect(NullPointerException.class);	
		
		StructuredUnmarshaller.<Attributes, Much, String>builder()
		.map((headers) -> null)
		.map((extensions) -> null)
		.next()
		.map((payload, extensions) -> null)
		.withHeaders(() -> null)
		.withPayload(null);
	}
	
	@Test
	public void should_ok_on_payload_supplier() {
		StructuredUnmarshaller.<Attributes, Much, String>builder()
		.map((headers) -> null)
		.map((extensions) -> null)
		.next()
		.map((payload, extensions) -> null)
		.withHeaders(() -> null)
		.withPayload(() -> null);
	}
}
