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
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

import io.cloudevents.extensions.DatarefExtension;
import java.net.URI;
import java.util.HashMap;
import java.util.Map;

import org.junit.Test;

import io.cloudevents.CloudEvent;
import io.cloudevents.extensions.DistributedTracingExtension;
import io.cloudevents.json.types.Much;
import io.cloudevents.v1.AttributesImpl;
import io.cloudevents.v1.http.Unmarshallers;

/**
 * 
 * @author fabiojose
 *
 */
public class HTTPBinaryUnmarshallerTest {
	@Test
	public void should_unmarshal_headers_and_json_payload() {
		// setup
		Much expected = new Much();
		expected.setWow("yes!");
		
		Map<String, Object> myHeaders = new HashMap<>();
    	myHeaders.put("ce-id", "0x11");
		myHeaders.put("ce-source", "/source");
		myHeaders.put("ce-specversion", "1.0");
		myHeaders.put("ce-type", "br.my");
		myHeaders.put("ce-time", "2019-09-16T20:49:00Z");
		myHeaders.put("ce-dataschema", "http://my.br");
		myHeaders.put("ce-subject", "subject");
		myHeaders.put("Content-Type", "application/json");
		
		String payload = "{\"wow\":\"yes!\"}";
		
		// act
		CloudEvent<AttributesImpl, Much> actual = 
			Unmarshallers.binary(Much.class)
				.withHeaders(() -> myHeaders)
				.withPayload(() -> payload)
				.unmarshal();
		
		// assert
		assertEquals("0x11", actual.getAttributes().getId());
		assertEquals(URI.create("/source"), actual.getAttributes().getSource());
		assertEquals("1.0", actual.getAttributes().getSpecversion());
		assertEquals("br.my", actual.getAttributes().getType());
		assertTrue(actual.getAttributes().getTime().isPresent());
		assertTrue(actual.getAttributes().getDataschema().isPresent());
		assertEquals(URI.create("http://my.br"), actual.getAttributes().getDataschema().get());
		assertTrue(actual.getAttributes().getDatacontenttype().isPresent());
		assertEquals("application/json", actual.getAttributes().getDatacontenttype().get());
		assertTrue(actual.getData().isPresent());
		assertEquals(expected, actual.getData().get());
		assertTrue(actual.getAttributes().getSubject().isPresent());
		assertEquals("subject", actual.getAttributes().getSubject().get());
	}
	
	@Test
	public void should_unmarshal_tracing_extension_from_header() {
		// setup
		Much expected = new Much();
		expected.setWow("yes!");
		
		Map<String, Object> myHeaders = new HashMap<>();
    	myHeaders.put("ce-id", "0x11");
		myHeaders.put("ce-source", "/source");
		myHeaders.put("ce-specversion", "1.0");
		myHeaders.put("ce-type", "br.my");
		myHeaders.put("ce-time", "2019-09-16T20:49:00Z");
		myHeaders.put("ce-schemaurl", "http://my.br");
		myHeaders.put("Content-Type", "application/json");
		
		myHeaders.put("traceparent", "0x200");
		myHeaders.put("tracestate", "congo=9");
		
		String payload = "{\"wow\":\"yes!\"}";
		
		// act
		CloudEvent<AttributesImpl, Much> actual = 
			Unmarshallers.binary(Much.class)
				.withHeaders(() -> myHeaders)
				.withPayload(() -> payload)
				.unmarshal();
		
		// assert
		assertNotNull(actual.getExtensions()
			.get(DistributedTracingExtension.Format.IN_MEMORY_KEY));
		assertTrue(actual.getExtensions()
			.get(DistributedTracingExtension.Format.IN_MEMORY_KEY) 
				instanceof DistributedTracingExtension);
	}

	@Test
	public void should_unmarshal_dataref_extension_from_header() {
		// setup
		Much expected = new Much();
		expected.setWow("yes!");

		Map<String, Object> myHeaders = new HashMap<>();
		myHeaders.put("ce-id", "0x11");
		myHeaders.put("ce-source", "/source");
		myHeaders.put("ce-specversion", "1.0");
		myHeaders.put("ce-type", "br.my");
		myHeaders.put("ce-time", "2019-09-16T20:49:00Z");
		myHeaders.put("ce-schemaurl", "http://my.br");
		myHeaders.put("Content-Type", "application/json");

		myHeaders.put("dataref", "/dataref");

		String payload = "{\"wow\":\"yes!\"}";

		// act
		CloudEvent<AttributesImpl, Much> actual =
				Unmarshallers.binary(Much.class)
						.withHeaders(() -> myHeaders)
						.withPayload(() -> payload)
						.unmarshal();

		// assert
		assertNotNull(actual.getExtensions().get(DatarefExtension.Format.IN_MEMORY_KEY));
		assertTrue(actual.getExtensions().get(DatarefExtension.Format.IN_MEMORY_KEY)
				instanceof DatarefExtension);
	}
}
