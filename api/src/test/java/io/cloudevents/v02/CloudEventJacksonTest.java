package io.cloudevents.v02;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import java.io.InputStream;
import java.net.URI;

import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;

import io.cloudevents.json.Json;

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
		CloudEvent<Object> ce = 
				new CloudEventBuilder<>()
					.withId("x10")
					.withSource(URI.create("/source"))
					.withType("event-type")
					.build();
		
		// act
		String json = Json.encode(ce);
		
		// assert
		assertTrue(json.contains("x10"));
		assertTrue(json.contains("/source"));
		assertTrue(json.contains("event-type"));
		assertTrue(json.contains("0.2"));
		
		assertFalse(json.contains("time"));
		assertFalse(json.contains("schemaurl"));
		assertFalse(json.contains("contenttype"));
		assertFalse(json.contains("data"));
	}
	
	@Test
	public void should_have_optional_attrs() {
		// setup
		CloudEvent<Object> ce = 
				new CloudEventBuilder<>()
					.withId("x10")
					.withSource(URI.create("/source"))
					.withType("event-type")
					.withSchemaurl(URI.create("/schema"))
					.withContenttype("text/plain")
					.withData("my-data")
					.build();
		
		// act
		String json = Json.encode(ce);
		
		// assert
		assertTrue(json.contains("/schema"));
		assertTrue(json.contains("text/plain"));
		assertTrue(json.contains("my-data"));
	}
	
	@Test
    public void should_have_type() {
		// act
        CloudEvent<?> ce = Json.fromInputStream(resourceOf("02_aws.json"), CloudEvent.class);
        
        // assert
        assertEquals("aws.s3.object.created", ce.getType());
    }
	
	@Test
    public void should_have_id() {
		// act
        CloudEvent<?> ce = Json.fromInputStream(resourceOf("02_aws.json"), CloudEvent.class);
        
        // assert
        assertEquals("C234-1234-1234", ce.getId());
    }
	
	//should have time
	@Test
    public void should_have_time() {
		// act
        CloudEvent<?> ce = Json.fromInputStream(resourceOf("02_aws.json"), CloudEvent.class);
        
        // assert
        assertTrue(ce.getTime().isPresent());
    }
	
	@Test
    public void should_have_source() {
		// act
        CloudEvent<?> ce = Json.fromInputStream(resourceOf("02_aws.json"), CloudEvent.class);
        
        // assert
        assertEquals(URI.create("https://serverless.com"), ce.getSource());
    }
	
	@Test
    public void should_have_contenttype() {
		// act
        CloudEvent<?> ce = Json.fromInputStream(resourceOf("02_aws.json"), CloudEvent.class);
        
        // assert
        assertTrue(ce.getContenttype().isPresent());
        assertEquals("application/json", ce.getContenttype().get());
    }
	
	@Test
    public void should_have_specversion() {
		// act
        CloudEvent<?> ce = Json.fromInputStream(resourceOf("02_aws.json"), CloudEvent.class);
        
        // assert
        assertEquals("0.2", ce.getSpecversion());
    }
	
	@Test
	public void should_throw_when_absent() {
		// setup
		expectedEx.expect(IllegalStateException.class);
		expectedEx.expectMessage("invalid payload: 'id' must not be blank");
		
		// act
		Json.fromInputStream(resourceOf("02_absent.json"), CloudEvent.class);
	}
}
