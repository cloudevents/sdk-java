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
import io.cloudevents.v1.CloudEventBuilder;
import io.cloudevents.v1.CloudEventImpl;
import io.cloudevents.v1.http.Marshallers;

/**
 * 
 * @author fabiojose
 *
 */
public class HTTPBinaryMarshallerTest {
	@Test
	public void should_marshal_data_as_json() {
		// setup
		String expected = "{\"wow\":\"yes!\"}";
		Much ceData = new Much();
		ceData.setWow("yes!");

		CloudEventImpl<Much> ce = 
				CloudEventBuilder.<Much>builder()
					.withId("x10")
					.withSource(URI.create("/source"))
					.withType("event-type")
					.withDatacontenttype("application/json")
					.withSubject("subject")
					.withData(ceData)
					.build();
		
		// act
		Wire<String, String, String> actual = 
		Marshallers.<Much>
		  binary()
			.withEvent(() -> ce)
			.marshal();
		
		// assert
		assertTrue(actual.getPayload().isPresent());
		assertEquals(expected, actual.getPayload().get());
	}
	
	@Test
	public void should_marshal_attributes_as_headers() {
		// setup
		Much ceData = new Much();
		ceData.setWow("yes!");

		CloudEventImpl<Much> ce = 
				CloudEventBuilder.<Much>builder()
					.withId("x10")
					.withSource(URI.create("/source"))
					.withType("event-type")
					.withDatacontenttype("application/json")
					.withSubject("subject")
					.withData(ceData)
					.build();
		
		// act
		Wire<String, String, String> actual = 
		Marshallers.<Much>
		  binary()
			.withEvent(() -> ce)
			.marshal();
		
		// assert
		assertFalse(actual.getHeaders().isEmpty());
		assertEquals(ce.getAttributes().getId(), actual.getHeaders().get("ce-id"));
		assertEquals(ce.getAttributes().getSource(), URI.create(actual.getHeaders().get("ce-source")));
		assertEquals(ce.getAttributes().getType(), actual.getHeaders().get("ce-type"));
		assertEquals(ce.getAttributes().getSubject().get(), actual.getHeaders().get("ce-subject"));
		assertEquals(ce.getAttributes().getDatacontenttype().get(), actual.getHeaders().get("Content-Type"));
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
			Marshallers.<String>
			  binary()
				.withEvent(() -> ce)
				.marshal();
		
		assertFalse(actual.getHeaders().isEmpty());
		assertNotNull(actual.getHeaders().get(DistributedTracingExtension
				.Format.TRACE_PARENT_KEY));
		assertNotNull(actual.getHeaders().get(DistributedTracingExtension
				.Format.TRACE_STATE_KEY));
	}
}
