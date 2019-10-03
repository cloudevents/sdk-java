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
package io.cloudevents.v02.http;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

import java.net.URI;

import org.junit.Test;

import io.cloudevents.extensions.DistributedTracingExtension;
import io.cloudevents.extensions.ExtensionFormat;
import io.cloudevents.format.Wire;
import io.cloudevents.json.types.Much;
import io.cloudevents.v02.CloudEventBuilder;
import io.cloudevents.v02.CloudEventImpl;

/**
 * 
 * @author fabiojose
 *
 */
public class HTTPStructuredMarshallerTest {
	
	@Test
	public void should_marshal_all_as_json() {
		// setup
		String expected = "{\"data\":{\"wow\":\"yes!\"},\"id\":\"x10\",\"source\":\"/source\",\"specversion\":\"0.2\",\"type\":\"event-type\",\"contenttype\":\"application/json\"}";
		
		Much ceData = new Much();
		ceData.setWow("yes!");

		CloudEventImpl<Much> ce = 
				CloudEventBuilder.<Much>builder()
					.withId("x10")
					.withSource(URI.create("/source"))
					.withType("event-type")
					.withContenttype("application/json")
					.withData(ceData)
					.build();
		
		// act
		Wire<String, String, String> actual = 
			Marshallers.<Much>structured()
				.withEvent(() -> ce)
				.marshal();
		
		assertTrue(actual.getPayload().isPresent());
		assertEquals(expected, actual.getPayload().get());
	}
	
	@Test
	public void should_marshal_data_as_text_and_evelope_as_json() {
		// setup
		String expected = "{\"data\":\"yes!\",\"id\":\"x10\",\"source\":\"/source\",\"specversion\":\"0.2\",\"type\":\"event-type\",\"contenttype\":\"text/plain\"}";
		String ceData = "yes!";

		CloudEventImpl<String> ce = 
				CloudEventBuilder.<String>builder()
					.withId("x10")
					.withSource(URI.create("/source"))
					.withType("event-type")
					.withContenttype("text/plain")
					.withData(ceData)
					.build();
		
		// act
		Wire<String, String, String> actual = 
			Marshallers.<String>structured()
				.withEvent(() -> ce)
				.marshal();
		
		assertTrue(actual.getPayload().isPresent());
		assertEquals(expected, actual.getPayload().get());
	}
	
	@Test
	public void should_headers_have_content_type() {
		// setup
		String expected = "application/cloudevents+json";
		String ceData = "yes!";

		CloudEventImpl<String> ce = 
				CloudEventBuilder.<String>builder()
					.withId("x10")
					.withSource(URI.create("/source"))
					.withType("event-type")
					.withContenttype("text/plain")
					.withData(ceData)
					.build();
		
		// act
		Wire<String, String, String> actual = 
			Marshallers.<String>structured()
				.withEvent(() -> ce)
				.marshal();
		
		assertFalse(actual.getHeaders().isEmpty());
		assertTrue(actual.getHeaders().containsKey("Content-Type"));
		assertEquals(expected, actual.getHeaders().get("Content-Type"));
	}
	
	@Test
	public void should_marshal_the_tracing_extension_as_header() {
		// setup
		final DistributedTracingExtension dt = new DistributedTracingExtension();
		dt.setTraceparent("0");
		dt.setTracestate("congo=4");
		
		final ExtensionFormat tracing = new DistributedTracingExtension.Format(dt);
		
		CloudEventImpl<String> ce = 
			CloudEventBuilder.<String>builder()
				.withId("id")
				.withSource(URI.create("/source"))
				.withType("type")
				.withExtension(tracing)
				.build();
		
		// act
		Wire<String, String, String> actual = 		
			Marshallers.<String>structured()
				.withEvent(() -> ce)
				.marshal();
		
		// assert
		assertFalse(actual.getHeaders().isEmpty());
		assertNotNull(actual.getHeaders().get(DistributedTracingExtension
				.Format.TRACE_PARENT_KEY));
		assertNotNull(actual.getHeaders().get(DistributedTracingExtension
				.Format.TRACE_STATE_KEY));
	}
}
