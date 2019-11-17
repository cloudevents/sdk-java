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
public class StructuredMarshallerTest {

	@Rule
	public ExpectedException expectedEx = ExpectedException.none();
	
	@Test
	public void should_throw_on_null_envelope_mime_header() {
		// setup
		expectedEx.expect(NullPointerException.class);
		
		// act
		StructuredMarshaller.<Attributes, Much, String, String>builder()
			.mime(null, "");
	}
	
	@Test
	public void should_throw_on_null_envelope_mime_value() {
		// setup
		expectedEx.expect(NullPointerException.class);
		
		// act
		StructuredMarshaller.<Attributes, Much, String, String>builder()
			.mime("", null);
	}
	
	@Test
	public void should_be_ok_on_the_first_step() {
		
		// act
		StructuredMarshaller.<Attributes, Much, String, String>builder()
			.mime("Content-Type", "application/cloudevents+json");
	}
	
	@Test
	public void should_throw_on_null_marshaller_step() {
		// setup
		expectedEx.expect(NullPointerException.class);
				
		// act
		StructuredMarshaller.<Attributes, Much, String, String>builder()
			.mime("Content-Type", "application/cloudevents+json")
			.map(null);
	}
	
	@Test
	public void should_throw_on_null_event() {
		// setup
		expectedEx.expect(NullPointerException.class);
		
		StructuredMarshaller.<Attributes, Much, String, String>builder()
		.mime("Content-Type", "application/cloudevents+json")
		.map((ce) -> null)
		.skip()
		.withEvent(null);
	}
	
	@Test
	public void should_be_ok_on_the_third_step() {
		// act
		StructuredMarshaller.<Attributes, Much, String, String>builder()
		.mime("Content-Type", "application/cloudevents+json")
		.map((ce) -> null)
		.skip()
		.withEvent(() -> null);
	}
	
	@Test
	public void should_throw_on_null_extension_accessor() {
		// setup
		expectedEx.expect(NullPointerException.class);
		
		StructuredMarshaller.<Attributes, Much, String, String>builder()
		.mime("Content-Type", "application/cloudevents+json")
		.map((ce) -> null)
		.map(null);
	}
	
	@Test
	public void should_ok_on_the_extension_acessor() {
		// act		
		StructuredMarshaller.<Attributes, Much, String, String>builder()
		.mime("Content-Type", "application/cloudevents+json")
		.map((ce) -> null)
		.map((event) -> null);
	}
	
	@Test
	public void should_throw_on_null_extension_marshaller() {
		// setup
		expectedEx.expect(NullPointerException.class);
		
		StructuredMarshaller.<Attributes, Much, String, String>builder()
		.mime("Content-Type", "application/cloudevents+json")
		.map((ce) -> null)
		.map((event) -> null)
		.map(null);
	}
	
	@Test
	public void should_ok_on_extension_marshaller() {
		StructuredMarshaller.<Attributes, Much, String, String>builder()
		.mime("Content-Type", "application/cloudevents+json")
		.map((ce) -> null)
		.map((event) -> null)
		.map((extensions) -> null);
	}
	
	@Test
	public void should_throw_on_null_header_mapper() {
		// setup
		expectedEx.expect(NullPointerException.class);
		
		StructuredMarshaller.<Attributes, Much, String, String>builder()
		.mime("Content-Type", "application/cloudevents+json")
		.map((ce) -> null)
		.map((event) -> null)
		.map((extensions) -> null)
		.map(null);
	}
	
	@Test
	public void should_ok_on_header_mapper() {
		StructuredMarshaller.<Attributes, Much, String, String>builder()
		.mime("Content-Type", "application/cloudevents+json")
		.map((ce) -> null)
		.map((event) -> null)
		.map((extensions) -> null)
		.map((attributes, extensions) -> null);
	}
}
