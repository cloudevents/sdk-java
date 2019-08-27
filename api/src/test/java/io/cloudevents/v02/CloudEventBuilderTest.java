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
package io.cloudevents.v02;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

import java.net.URI;
import java.time.ZonedDateTime;

import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;

import io.cloudevents.extensions.DistributedTracingExtension;
import io.cloudevents.extensions.ExtensionFormat;
import io.cloudevents.extensions.InMemoryFormat;

/**
 * 
 * @author fabiojose
 *
 */
public class CloudEventBuilderTest {
	
	@Rule
	public ExpectedException expectedEx = ExpectedException.none();

	@Test
	public void error_when_null_id() {
		// setup
		expectedEx.expect(IllegalStateException.class);
		expectedEx.expectMessage("invalid payload: 'id' must not be blank");
		
		// act
		CloudEventBuilder.<Object>builder()
			.withSource(URI.create("/test"))
			.withType("type")
			.build();
	}
	
	@Test
	public void error_when_empty_id() {
		// setup
		expectedEx.expect(IllegalStateException.class);
		expectedEx.expectMessage("invalid payload: 'id' must not be blank");
		
		// act
		CloudEventBuilder.<Object>builder()
			.withId("")
			.withSource(URI.create("/test"))
			.withType("type")
			.build();
	}
	
	@Test
	public void error_when_null_type() {
		// setup
		expectedEx.expect(IllegalStateException.class);
		expectedEx.expectMessage("invalid payload: 'type' must not be blank");
		
		// act
		CloudEventBuilder.<Object>builder()
			.withId("id")
			.withSource(URI.create("/test"))
			.build();
	}
	
	@Test
	public void error_when_empty_type() {
		// setup
		expectedEx.expect(IllegalStateException.class);
		expectedEx.expectMessage("invalid payload: 'type' must not be blank");
		
		// act
		CloudEventBuilder.<Object>builder()
			.withId("id")
			.withSource(URI.create("/test"))
			.withType("")
			.build();
	}
	
	@Test
	public void error_when_null_source() {
		// setup
		expectedEx.expect(IllegalStateException.class);
		expectedEx.expectMessage("invalid payload: 'source' must not be null");
		
		// act
		CloudEventBuilder.<Object>builder()
			.withId("id")
			.withType("type")
			.build();
	}
	
	@Test
	public void should_have_id() {
		// act
		CloudEventImpl<Object> ce = 
			CloudEventBuilder.<Object>builder()
				.withId("id")
				.withSource(URI.create("/source"))
				.withType("type")
				.build();
		
		// assert
		assertEquals("id", ce.getAttributes().getId());
	}
	
	@Test
	public void should_have_source() {
		// act
		CloudEventImpl<Object> ce = 
			CloudEventBuilder.<Object>builder()
				.withId("id")
				.withSource(URI.create("/source"))
				.withType("type")
				.build();
		
		// assert
		assertEquals(URI.create("/source"), ce.getAttributes().getSource());
	}
	
	@Test
	public void should_have_type() {
		// act
		CloudEventImpl<Object> ce = 
			CloudEventBuilder.<Object>builder()
				.withId("id")
				.withSource(URI.create("/source"))
				.withType("type")
				.build();
		
		// assert
		assertEquals("type", ce.getAttributes().getType());
	}
	
	@Test
	public void should_have_specversion() {
		// act
		CloudEventImpl<Object> ce = 
			CloudEventBuilder.<Object>builder()
				.withId("id")
				.withSource(URI.create("/source"))
				.withType("type")
				.build();
		
		// assert
		assertEquals("0.2", ce.getAttributes().getSpecversion());
	}
	
	@Test
	public void should_have_time() {
		// setup
		ZonedDateTime expected = ZonedDateTime.now();
		
		// act
		CloudEventImpl<Object> ce = 
			CloudEventBuilder.<Object>builder()
				.withId("id")
				.withSource(URI.create("/source"))
				.withType("type")
				.withTime(expected)
				.build();
		
		// assert
		assertTrue(ce.getAttributes().getTime().isPresent());
		assertEquals(expected, ce.getAttributes().getTime().get());
	}
	
	@Test
	public void should_have_schemaurl() {
		// setup
		URI expected = URI.create("/schema");
		
		// act
		CloudEventImpl<Object> ce = 
			CloudEventBuilder.<Object>builder()
				.withId("id")
				.withSource(URI.create("/source"))
				.withType("type")
				.withSchemaurl(expected)
				.build();
		
		// assert
		assertTrue(ce.getAttributes().getSchemaurl().isPresent());
		assertEquals(expected, ce.getAttributes().getSchemaurl().get());
	}
	
	@Test
	public void should_have_contenttype() {
		// setup
		String expected = "application/json";
		
		// act
		CloudEventImpl<Object> ce = 
			CloudEventBuilder.<Object>builder()
				.withId("id")
				.withSource(URI.create("/source"))
				.withType("type")
				.withContenttype(expected)
				.build();
		
		// assert
		assertTrue(ce.getAttributes().getContenttype().isPresent());
		assertEquals(expected, ce.getAttributes().getContenttype().get());
	}
	
	@Test
	public void should_have_data() {
		// setup
		String expected = "my data";
		
		// act
		CloudEventImpl<Object> ce = 
			CloudEventBuilder.<Object>builder()
				.withId("id")
				.withSource(URI.create("/source"))
				.withType("type")
				.withData(expected)
				.build();
		
		// assert
		assertTrue(ce.getData().isPresent());
		assertEquals(expected, ce.getData().get());
	}
	
	@Test
	public void should_have_dte() {
		// setup
		final DistributedTracingExtension dt = new DistributedTracingExtension();
		dt.setTraceparent("0");
		dt.setTracestate("congo=4");
		
		final ExtensionFormat tracing = new DistributedTracingExtension.Format(dt);
		
		// act
		CloudEventImpl<Object> ce = 
			CloudEventBuilder.<Object>builder()
				.withId("id")
				.withSource(URI.create("/source"))
				.withType("type")
				.withExtension(tracing)
				.build();
		
		Object actual = ce.getExtensions()
			.get(DistributedTracingExtension.Format.IN_MEMORY_KEY);
		
		// assert
		assertNotNull(actual);
		assertTrue(actual instanceof DistributedTracingExtension);
	}
	
	@Test
	public void should_have_custom_extension() {
		String myExtKey = "comexampleextension1";
		String myExtVal = "value";
		
		ExtensionFormat custom = ExtensionFormat
			.of(InMemoryFormat.of(myExtKey, myExtKey, String.class),
				myExtKey, myExtVal);
		
		// act
		CloudEventImpl<Object> ce = 
			CloudEventBuilder.<Object>builder()
				.withId("id")
				.withSource(URI.create("/source"))
				.withType("type")
				.withExtension(custom)
				.build();
		
		Object actual = ce.getExtensions()
				.get(myExtKey);
		
		assertNotNull(actual);
		assertTrue(actual instanceof String);
	}
}
