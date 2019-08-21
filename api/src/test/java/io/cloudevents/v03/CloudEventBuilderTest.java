package io.cloudevents.v03;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import java.net.URI;

import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;

import io.cloudevents.CloudEvent;

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
		CloudEventBuilder.builder()
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
		CloudEventBuilder.builder()
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
		CloudEventBuilder.builder()
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
		CloudEventBuilder.builder()
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
		CloudEventBuilder.builder()
			.withId("id")
			.withType("type")
			.build();
	}
	
	@Test
	public void error_when_empty_subject() {
		// setup
		expectedEx.expect(IllegalStateException.class);
		expectedEx.expectMessage("invalid payload: 'subject' size must be between 1 and 2147483647");
		
		// act
		CloudEventBuilder.<Object>builder()
			.withId("id")
			.withType("type")
			.withSource(URI.create("/source"))
			.withSubject("")
			.build();
	}
	
	@Test
	public void error_when_invalid_encoding() {
		// setup
		expectedEx.expect(IllegalStateException.class);
		expectedEx.expectMessage("invalid payload: 'datacontentencoding' must match \"base64\"");
		
		// act
		CloudEventBuilder.<Object>builder()
			.withId("id")
			.withType("type")
			.withSource(URI.create("/source"))
			.withSubject("subject")
			.withDatacontentencoding("binary")
			.build();
	}
	
	@Test
	public void should_have_subject() {
		// act
		CloudEvent<AttributesImpl, Object> ce = 
				CloudEventBuilder.<Object>builder()
				.withId("id")
				.withSource(URI.create("/source"))
				.withType("type")
				.withSubject("subject")
				.build();
		
		// assert
		assertTrue(ce.getAttributes().getSubject().isPresent());
		assertEquals("subject", ce.getAttributes().getSubject().get());
	}

}
