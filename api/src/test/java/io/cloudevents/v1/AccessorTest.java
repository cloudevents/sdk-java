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
package io.cloudevents.v1;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import io.cloudevents.extensions.DatarefExtension;
import java.net.URI;
import java.util.Collection;

import org.junit.Test;

import io.cloudevents.extensions.DistributedTracingExtension;
import io.cloudevents.extensions.ExtensionFormat;
import io.cloudevents.extensions.InMemoryFormat;
import io.cloudevents.v1.Accessor;
import io.cloudevents.v1.CloudEventBuilder;
import io.cloudevents.v1.CloudEventImpl;

/**
 * 
 * @author fabiojose
 *
 */
public class AccessorTest {
	@Test
	public void should_empty_collection_when_no_extensions() {
		// setup	
		CloudEventImpl<Object> ce = 
				CloudEventBuilder.<Object>builder()
					.withId("x10")
					.withSource(URI.create("/source"))
					.withType("event-type")
					.withDataschema(URI.create("/schema"))
					.withDataContentType("text/plain")
					.withData("my-data")
					.build();
		
		// act
		Collection<ExtensionFormat> actual = Accessor.extensionsOf(ce);
		
		// assert
		assertTrue(actual.isEmpty());
	}
	
	@Test
	public void should_return_the_tracing_extension() {
		// setup 
		final DistributedTracingExtension dt = new DistributedTracingExtension();
		dt.setTraceparent("0");
		dt.setTracestate("congo=4");
		
		final ExtensionFormat expected = new DistributedTracingExtension.Format(dt);
		
		CloudEventImpl<Object> ce = 
				CloudEventBuilder.<Object>builder()
					.withId("x10")
					.withSource(URI.create("/source"))
					.withType("event-type")
					.withDataschema(URI.create("/schema"))
					.withDataContentType("text/plain")
					.withData("my-data")
					.withExtension(expected)
					.build();
		
		// act
		Collection<ExtensionFormat> extensions = 
				Accessor.extensionsOf(ce);
		
		// assert
		assertFalse(extensions.isEmpty());
		ExtensionFormat actual = extensions.iterator().next();
		
		assertEquals("0", actual.transport().get("traceparent"));
		assertEquals("congo=4", actual.transport().get("tracestate"));
		
		assertEquals("0", 
				((DistributedTracingExtension)actual.memory().getValue()).getTraceparent());
		
		assertEquals("congo=4", 
				((DistributedTracingExtension)actual.memory().getValue()).getTracestate());
	}

	@Test
	public void should_return_the_dataref_extension() {
		// setup
		final DatarefExtension datarefExtension = new DatarefExtension();
		datarefExtension.setDataref(URI.create("/dataref"));

		final ExtensionFormat expected = new DatarefExtension.Format(datarefExtension);

		CloudEventImpl<Object> ce =
				CloudEventBuilder.<Object>builder()
						.withId("x10")
						.withSource(URI.create("/source"))
						.withType("event-type")
						.withDataschema(URI.create("/schema"))
						.withDataContentType("text/plain")
						.withData("my-data")
						.withExtension(expected)
						.build();

		// act
		Collection<ExtensionFormat> extensions = Accessor.extensionsOf(ce);

		// assert
		assertFalse(extensions.isEmpty());
		ExtensionFormat actual = extensions.iterator().next();

		assertEquals("/dataref", actual.transport().get("dataref"));
		assertEquals("/dataref",
				((DatarefExtension)actual.memory().getValue()).getDataref().toString());
	}
	
	@Test
	public void should_return_the_custom_extension() {
		// setup
		String customExt = "comexampleextension1";
		String customVal = "my-ext-val";
		InMemoryFormat inMemory = 
			InMemoryFormat.of(customExt, customVal, String.class);
		
		ExtensionFormat expected = 
			ExtensionFormat.of(inMemory, customExt, customVal);
		
		CloudEventImpl<Object> ce = 
				CloudEventBuilder.<Object>builder()
					.withId("x10")
					.withSource(URI.create("/source"))
					.withType("event-type")
					.withDataschema(URI.create("/schema"))
					.withDataContentType("text/plain")
					.withData("my-data")
					.withExtension(expected)
					.build();
		
		// act
		Collection<ExtensionFormat> extensions = 
				Accessor.extensionsOf(ce);
		
		// assert
		assertFalse(extensions.isEmpty());
		ExtensionFormat actual = extensions.iterator().next();
		
		assertEquals(customVal, actual.transport().get(customExt));
		
		assertEquals(String.class, actual.memory().getValueType());
		
		assertEquals(customExt, actual.memory().getKey());
		
		assertEquals(customVal, actual.memory().getValue());
	}
}
