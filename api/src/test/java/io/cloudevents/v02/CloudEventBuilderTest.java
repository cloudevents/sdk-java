package io.cloudevents.v02;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import java.net.URI;
import java.time.ZonedDateTime;

import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;

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
		new CloudEventBuilder<Object>()
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
		new CloudEventBuilder<Object>()
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
		new CloudEventBuilder<Object>()
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
		new CloudEventBuilder<Object>()
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
		new CloudEventBuilder<Object>()
			.withId("id")
			.withType("type")
			.build();
	}
	
	@Test
	public void should_have_id() {
		// act
		CloudEventImpl<Object> ce = 
			new CloudEventBuilder<>()
				.withId("id")
				.withSource(URI.create("/source"))
				.withType("type")
				.build();
		
		// assert
		assertEquals("id", ce.getAttributes().getId());
	}
	
	@Test
	public void should_have_source() {
		// act
		CloudEventImpl<Object> ce = 
			new CloudEventBuilder<>()
				.withId("id")
				.withSource(URI.create("/source"))
				.withType("type")
				.build();
		
		// assert
		assertEquals(URI.create("/source"), ce.getAttributes().getSource());
	}
	
	@Test
	public void should_have_type() {
		// act
		CloudEventImpl<Object> ce = 
			new CloudEventBuilder<>()
				.withId("id")
				.withSource(URI.create("/source"))
				.withType("type")
				.build();
		
		// assert
		assertEquals("type", ce.getAttributes().getType());
	}
	
	@Test
	public void should_have_specversion() {
		// act
		CloudEventImpl<Object> ce = 
			new CloudEventBuilder<>()
				.withId("id")
				.withSource(URI.create("/source"))
				.withType("type")
				.build();
		
		// assert
		assertEquals("0.2", ce.getAttributes().getSpecversion());
	}
	
	@Test
	public void should_have_time() {
		// setup
		ZonedDateTime expected = ZonedDateTime.now();
		
		// act
		CloudEventImpl<Object> ce = 
			new CloudEventBuilder<>()
				.withId("id")
				.withSource(URI.create("/source"))
				.withType("type")
				.withTime(expected)
				.build();
		
		// assert
		assertTrue(ce.getAttributes().getTime().isPresent());
		assertEquals(expected, ce.getAttributes().getTime().get());
	}
	
	@Test
	public void should_have_schemaurl() {
		// setup
		URI expected = URI.create("/schema");
		
		// act
		CloudEventImpl<Object> ce = 
			new CloudEventBuilder<>()
				.withId("id")
				.withSource(URI.create("/source"))
				.withType("type")
				.withSchemaurl(expected)
				.build();
		
		// assert
		assertTrue(ce.getAttributes().getSchemaurl().isPresent());
		assertEquals(expected, ce.getAttributes().getSchemaurl().get());
	}
	
	@Test
	public void should_have_contenttype() {
		// setup
		String expected = "application/json";
		
		// act
		CloudEventImpl<Object> ce = 
			new CloudEventBuilder<>()
				.withId("id")
				.withSource(URI.create("/source"))
				.withType("type")
				.withContenttype(expected)
				.build();
		
		// assert
		assertTrue(ce.getAttributes().getContenttype().isPresent());
		assertEquals(expected, ce.getAttributes().getContenttype().get());
	}
	
	@Test
	public void should_have_data() {
		// setup
		String expected = "my data";
		
		// act
		CloudEventImpl<Object> ce = 
			new CloudEventBuilder<>()
				.withId("id")
				.withSource(URI.create("/source"))
				.withType("type")
				.withData(expected)
				.build();
		
		// assert
		assertTrue(ce.getData().isPresent());
		assertEquals(expected, ce.getData().get());
	}
}
