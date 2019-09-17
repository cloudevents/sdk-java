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
import java.util.HashMap;
import java.util.Map;

import org.junit.Test;

import io.cloudevents.CloudEvent;
import io.cloudevents.json.types.Much;
import io.cloudevents.v02.AttributesImpl;

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
		myHeaders.put("ce-specversion", "0.2");
		myHeaders.put("ce-type", "br.my");
		myHeaders.put("ce-time", "2019-09-16T20:49:00Z");
		myHeaders.put("ce-schemaurl", "http://my.br");
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
		assertEquals("0.2", actual.getAttributes().getSpecversion());
		assertEquals("br.my", actual.getAttributes().getType());
		assertTrue(actual.getAttributes().getTime().isPresent());
		assertTrue(actual.getAttributes().getSchemaurl().isPresent());
		assertEquals(URI.create("http://my.br"), actual.getAttributes().getSchemaurl().get());
		assertTrue(actual.getAttributes().getContenttype().isPresent());
		assertEquals("application/json", actual.getAttributes().getContenttype().get());
		assertTrue(actual.getData().isPresent());
		assertEquals(expected, actual.getData().get());
	}
}
