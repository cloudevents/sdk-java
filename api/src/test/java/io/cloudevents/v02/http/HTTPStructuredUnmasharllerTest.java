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
import static org.junit.Assert.assertTrue;

import java.net.URI;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

import org.junit.Test;

import com.fasterxml.jackson.core.type.TypeReference;

import io.cloudevents.CloudEvent;
import io.cloudevents.extensions.DistributedTracingExtension;
import io.cloudevents.extensions.ExtensionFormat;
import io.cloudevents.format.StructuredUnmarshaller;
import io.cloudevents.json.Json;
import io.cloudevents.json.types.Much;
import io.cloudevents.v02.Accessor;
import io.cloudevents.v02.AttributesImpl;
import io.cloudevents.v02.CloudEventBuilder;
import io.cloudevents.v02.CloudEventImpl;

/**
 * 
 * @author fabiojose
 *
 */
public class HTTPStructuredUnmasharllerTest {

	@Test
	public void should_unmarshal_json_envelope_and_json_data() {
		// setup
		Map<String, Object> httpHeaders = new HashMap<>();
		httpHeaders.put("Content-Type", "application/cloudevents+json");
		
		String json = "{\"data\":{\"wow\":\"yes!\"},\"id\":\"x10\",\"source\":\"/source\",\"specversion\":\"0.2\",\"type\":\"event-type\",\"contenttype\":\"application/json\"}";
		
		Much ceData = new Much();
		ceData.setWow("yes!");
		
		CloudEventImpl<Much> expected = 
				CloudEventBuilder.<Much>builder()
					.withId("x10")
					.withSource(URI.create("/source"))
					.withType("event-type")
					.withContenttype("application/json")
					.withData(ceData)
					.build();
		
		// act
		CloudEvent<AttributesImpl, Much> actual = 
			StructuredUnmarshaller.<AttributesImpl, Much, String>
			  builder()
				.map("application/json", Json.umarshaller(Much.class)::unmarshal)
				.next()
				.skip()
				.map((payload) -> 
					Json.decodeValue(payload, 
						new TypeReference<CloudEventImpl<Much>>() {}))
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
	public void should_unmarshal_json_envelope_and_text_data() {
		// setup
		Map<String, Object> httpHeaders = new HashMap<>();
		httpHeaders.put("Content-Type", "application/cloudevents+json");
		
		String json = "{\"data\":\"yes!\",\"id\":\"x10\",\"source\":\"/source\",\"specversion\":\"0.2\",\"type\":\"event-type\",\"contenttype\":\"text/plain\"}";
		String ceData = "yes!";

		CloudEventImpl<String> expected = 
				CloudEventBuilder.<String>builder()
					.withId("x10")
					.withSource(URI.create("/source"))
					.withType("event-type")
					.withContenttype("text/plain")
					.withData(ceData)
					.build();
		
		// act
		CloudEvent<AttributesImpl, String> actual = 
			StructuredUnmarshaller.<AttributesImpl, String, String>
			  builder()
				.map("application/json", Json.umarshaller(String.class)::unmarshal)
				.next()
				.skip()
				.map((payload) -> 
					Json.decodeValue(payload, 
						new TypeReference<CloudEventImpl<String>>() {}))
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
	public void should_unmarshal_then_tracing_extension() {
		// setup
		Map<String, Object> httpHeaders = new HashMap<>();
		httpHeaders.put("Content-Type", "application/cloudevents+json");
		
		String json = "{\"data\":\"yes!\",\"id\":\"x10\",\"source\":\"/source\",\"specversion\":\"0.2\",\"type\":\"event-type\",\"contenttype\":\"text/plain\", \"distributedTracing\":{\"traceparent\":\"0\",\"tracestate\":\"congo=4\"}}";
		String ceData = "yes!";
		
		final DistributedTracingExtension dt = new DistributedTracingExtension();
		dt.setTraceparent("0");
		dt.setTracestate("congo=4");
		
		final ExtensionFormat tracing = new DistributedTracingExtension.Format(dt);

		CloudEventImpl<String> expected = 
				CloudEventBuilder.<String>builder()
					.withId("x10")
					.withSource(URI.create("/source"))
					.withType("event-type")
					.withContenttype("text/plain")
					.withData(ceData)
					.withExtension(tracing)
					.build();
		
		// act
		CloudEvent<AttributesImpl, String> actual = 
			StructuredUnmarshaller.<AttributesImpl, String, String>
			  builder()
				.map("application/json", Json.umarshaller(String.class)::unmarshal)
				.next()
				.map(ExtensionMapper::map)
				.map(DistributedTracingExtension::unmarshall)
				.next()
				.map((payload) -> 
					Json.decodeValue(payload, 
						new TypeReference<CloudEventImpl<String>>() {}))
				.withHeaders(() -> httpHeaders)
				.withPayload(() -> json)
				.unmarshal();
		
		// assert
		assertTrue(actual.getExtensions().containsKey(
				DistributedTracingExtension.Format.IN_MEMORY_KEY));
	}
}
