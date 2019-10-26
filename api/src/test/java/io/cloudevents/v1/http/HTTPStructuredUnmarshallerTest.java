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
import static org.junit.Assert.assertTrue;

import java.net.URI;
import java.util.HashMap;
import java.util.Map;

import org.junit.Test;

import io.cloudevents.CloudEvent;
import io.cloudevents.extensions.DistributedTracingExtension;
import io.cloudevents.json.types.Much;
import io.cloudevents.v1.AttributesImpl;
import io.cloudevents.v1.CloudEventBuilder;
import io.cloudevents.v1.CloudEventImpl;
import io.cloudevents.v1.http.Unmarshallers;

/**
 * 
 * @author fabiojose
 *
 */
public class HTTPStructuredUnmarshallerTest {
	@Test
	public void should_unmarshal_json_envelope_and_json_data() {
		// setup
		Map<String, Object> httpHeaders = new HashMap<>();
		httpHeaders.put("Content-Type", "application/cloudevents+json");
		
		String json = "{\"data\":{\"wow\":\"yes!\"},\"id\":\"x10\",\"source\":\"/source\",\"specversion\":\"1.0\",\"type\":\"event-type\",\"datacontenttype\":\"application/json\",\"subject\":\"subject\"}";
		
		Much ceData = new Much();
		ceData.setWow("yes!");
		
		CloudEventImpl<Much> expected = 
				CloudEventBuilder.<Much>builder()
					.withId("x10")
					.withSource(URI.create("/source"))
					.withType("event-type")
					.withDatacontenttype("application/json")
					.withSubject("subject")
					.withData(ceData)
					.build();
		
		// act
		CloudEvent<AttributesImpl, Much> actual = 
			Unmarshallers.structured(Much.class)
				.withHeaders(() -> httpHeaders)
				.withPayload(() -> json)
				.unmarshal();
		
		// assert
		assertEquals(expected.getAttributes().getSpecversion(), 
				actual.getAttributes().getSpecversion());
		
		assertEquals(expected.getAttributes().getId(),
				actual.getAttributes().getId());
		
		assertEquals(expected.getAttributes().getSource(),
				actual.getAttributes().getSource());
		
		assertEquals(expected.getAttributes().getType(),
				actual.getAttributes().getType());
		
		assertTrue(actual.getData().isPresent());
		assertEquals(expected.getData().get(), actual.getData().get());
		
		assertTrue(actual.getAttributes().getSubject().isPresent());
		assertEquals(expected.getAttributes().getSubject().get(),
				actual.getAttributes().getSubject().get());
	}
	
	@Test
	public void should_unmarshal_json_envelope_and_text_data() {
		// setup
		Map<String, Object> httpHeaders = new HashMap<>();
		httpHeaders.put("Content-Type", "application/cloudevents+json");
		
		String json = "{\"data\":\"yes!\",\"id\":\"x10\",\"source\":\"/source\",\"specversion\":\"1.0\",\"type\":\"event-type\",\"datacontenttype\":\"text/plain\"}";
		String ceData = "yes!";

		CloudEventImpl<String> expected = 
				CloudEventBuilder.<String>builder()
					.withId("x10")
					.withSource(URI.create("/source"))
					.withType("event-type")
					.withDatacontenttype("text/plain")
					.withData(ceData)
					.build();
		
		// act
		CloudEvent<AttributesImpl, String> actual = 
			Unmarshallers.structured(String.class)
				.withHeaders(() -> httpHeaders)
				.withPayload(() -> json)
				.unmarshal();
		
		// assert
		assertEquals(expected.getAttributes().getSpecversion(), 
				actual.getAttributes().getSpecversion());
		
		assertEquals(expected.getAttributes().getId(),
				actual.getAttributes().getId());
		
		assertEquals(expected.getAttributes().getSource(),
				actual.getAttributes().getSource());
		
		assertEquals(expected.getAttributes().getType(),
				actual.getAttributes().getType());
		
		assertTrue(actual.getData().isPresent());
		assertEquals(expected.getData().get(), actual.getData().get());
	}
	
	@Test
	public void should_unmarshal_the_tracing_extension_from_headers() {
		// setup
		Map<String, Object> httpHeaders = new HashMap<>();
		httpHeaders.put("Content-Type", "application/cloudevents+json");
		
		httpHeaders.put("traceparent", "0x200");
		httpHeaders.put("tracestate", "congo=9");
		
		String json = "{\"data\":\"yes!\",\"id\":\"x10\",\"source\":\"/source\",\"specversion\":\"1.0\",\"type\":\"event-type\",\"datacontenttype\":\"text/plain\"}";

		// act
		CloudEvent<AttributesImpl, String> actual = 
			Unmarshallers.structured(String.class)
				.withHeaders(() -> httpHeaders)
				.withPayload(() -> json)
				.unmarshal();
		
		// assert
		assertTrue(actual.getExtensions().containsKey(
				DistributedTracingExtension.Format.IN_MEMORY_KEY));
		
		assertTrue(actual.getExtensions().get(
				DistributedTracingExtension.Format.IN_MEMORY_KEY) 
					instanceof DistributedTracingExtension);
	}
}
