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
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

import java.io.InputStream;
import java.net.URI;

import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;

import com.fasterxml.jackson.core.type.TypeReference;

import io.cloudevents.CloudEvent;
import io.cloudevents.extensions.DistributedTracingExtension;
import io.cloudevents.extensions.ExtensionFormat;
import io.cloudevents.json.Json;
import io.cloudevents.json.types.Much;
import io.cloudevents.v1.AttributesImpl;
import io.cloudevents.v1.CloudEventBuilder;
import io.cloudevents.v1.CloudEventImpl;

/**
 * 
 * @author fabiojose
 *
 */
public class CloudEventJacksonTest {

	private static InputStream resourceOf(String name) {
		return Thread.currentThread().getContextClassLoader().getResourceAsStream(name);
	}
	
	@Rule
	public ExpectedException expectedEx = ExpectedException.none();
	
	@Test
	public void should_encode_right_with_minimal_attrs() {
		// setup
		CloudEvent<AttributesImpl, Object> ce = 
				CloudEventBuilder.builder()
					.withId("x10")
					.withSource(URI.create("/source"))
					.withType("event-type")
					.build();
		
		// act
		String json = Json.encode(ce);
		System.out.println(json);
		
		// assert
		assertTrue(json.contains("x10"));
		assertTrue(json.contains("/source"));
		assertTrue(json.contains("event-type"));
		assertTrue(json.contains("1.0"));
		
		assertFalse(json.contains("time"));
		assertFalse(json.contains("schemaurl"));
		assertFalse(json.contains("contenttype"));
		assertFalse(json.contains("data"));
	}
	
	@Test
	public void should_have_optional_attrs() {
		// setup
		CloudEvent<AttributesImpl, Object> ce = 
				CloudEventBuilder.builder()
					.withId("x10")
					.withSource(URI.create("/source"))
					.withType("event-type")
					.withDataschema(URI.create("/schema"))
					.withDatacontenttype("text/plain")
					.withSubject("subject0")
					.withData("my-data")
					.build();
		
		// act
		String json = Json.encode(ce);
		
		// assert
		assertTrue(json.contains("/schema"));
		assertTrue(json.contains("text/plain"));
		assertTrue(json.contains("my-data"));
		assertTrue(json.contains("subject0"));
		
		assertTrue(json.contains("\"dataschema\""));
		assertTrue(json.contains("datacontenttype"));
		assertTrue(json.contains("\"subject\""));
	}
	
	@Test
	public void should_serialize_trace_extension() {
		// setup
		String expected = "\"distributedTracing\":{\"traceparent\":\"0\",\"tracestate\":\"congo=4\"}";
		final DistributedTracingExtension dt = new DistributedTracingExtension();
		dt.setTraceparent("0");
		dt.setTracestate("congo=4");
		
		final ExtensionFormat tracing = new DistributedTracingExtension.Format(dt);
		
		CloudEvent<AttributesImpl, Object> ce = 
				CloudEventBuilder.builder()
					.withId("x10")
					.withSource(URI.create("/source"))
					.withType("event-type")
					.withDataschema(URI.create("/schema"))
					.withDatacontenttype("text/plain")
					.withData("my-data")
					.withExtension(tracing)
					.build();
		
		// act
		String actual = Json.encode(ce);
		
		// assert
		assertTrue(actual.contains(expected));
	}
	
	@Test
	public void should_not_serialize_attributes_element() {
		// setup
		CloudEvent<AttributesImpl, Object> ce = 
				CloudEventBuilder.builder()
					.withId("x10")
					.withSource(URI.create("/source"))
					.withType("event-type")
					.withDataschema(URI.create("/schema"))
					.withDatacontenttype("text/plain")
					.withSubject("subject0")
					.withData("my-data")
					.build();
		
		// act
		String actual = Json.encode(ce);
		
		// assert
		assertFalse(actual.contains("\"attributes\""));
	}
	
	@Test
    public void should_have_type() {
		// act
        CloudEvent<AttributesImpl, Object> ce = 
        	Json.fromInputStream(resourceOf("1_new.json"), CloudEventImpl.class);

        // assert
        assertEquals("aws.s3.object.created", ce.getAttributes().getType());
    }
	
	@Test
    public void should_have_id() {
		// act
		CloudEvent<AttributesImpl, Object> ce = 
	        	Json.fromInputStream(resourceOf("1_new.json"), CloudEventImpl.class);
        
        // assert
        assertEquals("C234-1234-1234", ce.getAttributes().getId());
    }
	
	//should have time
	@Test
    public void should_have_time() {
		// act
		CloudEvent<AttributesImpl, Object> ce = 
	        	Json.fromInputStream(resourceOf("1_new.json"), CloudEventImpl.class);
        
        // assert
        assertTrue(ce.getAttributes().getTime().isPresent());
    }
	
	@Test
    public void should_have_source() {
		// act
		CloudEvent<AttributesImpl, Object> ce = 
	        	Json.fromInputStream(resourceOf("1_new.json"), CloudEventImpl.class);
        
        // assert
        assertEquals(URI.create("https://serverless.com"), ce.getAttributes().getSource());
    }
	
	@Test
    public void should_have_datacontenttype() {
		// act
		CloudEvent<AttributesImpl, Object> ce = 
	        	Json.fromInputStream(resourceOf("1_new.json"), CloudEventImpl.class);
        
        // assert
        assertTrue(ce.getAttributes().getDatacontenttype().isPresent());
        assertEquals("application/json", ce.getAttributes().getDatacontenttype().get());
    }
	
	@Test
    public void should_have_dataschema() {
		// act
		CloudEvent<AttributesImpl, Object> ce = 
	        	Json.fromInputStream(resourceOf("1_new.json"), CloudEventImpl.class);
        
        // assert
        assertTrue(ce.getAttributes().getDataschema().isPresent());
        assertEquals(URI.create("/my-schema"), ce.getAttributes().getDataschema().get());
    }
	
	@Test
    public void should_have_specversion() {
		// act
		CloudEvent<AttributesImpl, Object> ce = 
	        	Json.fromInputStream(resourceOf("1_new.json"), CloudEventImpl.class);
        
        // assert
        assertEquals("1.0", ce.getAttributes().getSpecversion());
    }
	
	@Test
	public void should_throw_when_absent() {
		// setup
		expectedEx.expect(IllegalStateException.class);
		expectedEx.expectMessage("invalid payload: 'id' must not be blank");
		
		// act
		Json.fromInputStream(resourceOf("1_absent.json"), CloudEventImpl.class);
	}
	
	@Test
    public void should_have_tracing_extension() {
		// act
		CloudEvent<AttributesImpl, Object> ce = 
	        	Json.fromInputStream(resourceOf("1_extension.json"), CloudEventImpl.class);
        
        // assert
        assertNotNull(ce.getExtensions()
        	.get(DistributedTracingExtension.Format.IN_MEMORY_KEY));
    }
	
	@Test
    public void should_have_custom_extension() {
		// setup
		String extensionKey = "my-extension";
		String expected = "extension-value";
		
		// act
		CloudEvent<AttributesImpl, Object> ce = 
	        	Json.fromInputStream(resourceOf("1_extension.json"), CloudEventImpl.class);
        
        // assert
        assertEquals(expected, ce.getExtensions()
        	.get(extensionKey));
    }
	
	@Test
    public void should_have_custom_data() {
		// setup
		Much expected = new Much();
		expected.setWow("kinda");
		
		String json = "{\"type\":\"aws.s3.object.created\",\"id\":\"C234-1234-1234\",\"time\":\"2019-08-19T19:35:00.000Z\",\"source\":\"https://serverless.com\",\"datacontenttype\":\"application/json\",\"specversion\":\"1.0\",\"data\":{\"wow\":\"kinda\"}}";
		
		// act		
		CloudEvent<AttributesImpl, Much> ce = 
			Json.decodeValue(json, new TypeReference<CloudEventImpl<Much>>() {});
         
        // assert
		assertTrue(ce.getData().isPresent());
        assertEquals(expected.getWow(), ce.getData().get().getWow());
    }
	
}
