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
package io.cloudevents.v03;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

import java.net.URI;

import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;

import io.cloudevents.CloudEvent;
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
		CloudEventBuilder.builder()
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
		CloudEventBuilder.builder()
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
		CloudEventBuilder.builder()
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
		CloudEventBuilder.builder()
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
		CloudEventBuilder.builder()
			.withId("id")
			.withType("type")
			.build();
	}
	
	@Test
	public void error_when_empty_subject() {
		// setup
		expectedEx.expect(IllegalStateException.class);
		expectedEx.expectMessage("invalid payload: 'subject' size must be between 1 and 2147483647");
		
		// act
		CloudEventBuilder.<Object>builder()
			.withId("id")
			.withType("type")
			.withSource(URI.create("/source"))
			.withSubject("")
			.build();
	}
	
	@Test
	public void error_when_invalid_encoding() {
		// setup
		expectedEx.expect(IllegalStateException.class);
		expectedEx.expectMessage("invalid payload: 'datacontentencoding' must match \"base64\"");
		
		// act
		CloudEventBuilder.<Object>builder()
			.withId("id")
			.withType("type")
			.withSource(URI.create("/source"))
			.withSubject("subject")
			.withDatacontentencoding("binary")
			.build();
	}
	
	@Test
	public void should_have_subject() {
		// act
		CloudEvent<AttributesImpl, Object> ce = 
				CloudEventBuilder.<Object>builder()
				.withId("id")
				.withSource(URI.create("/source"))
				.withType("type")
				.withSubject("subject")
				.build();
		
		// assert
		assertTrue(ce.getAttributes().getSubject().isPresent());
		assertEquals("subject", ce.getAttributes().getSubject().get());
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
			CloudEventBuilder.builder()
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
			CloudEventBuilder.builder()
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
