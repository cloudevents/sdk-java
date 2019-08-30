package io.cloudevents.extensions;

import static org.junit.Assert.assertEquals;

import org.junit.Test;

/**
 * 
 * @author fabiojose
 *
 */
public class DistributedTracingExtensionTest {

	@Test
	public void should_transport_format_ok() {
		// setup
		DistributedTracingExtension tracing = new DistributedTracingExtension();
		tracing.setTraceparent("parent");
		tracing.setTracestate("state");
		
		// act
		ExtensionFormat format = 
				new DistributedTracingExtension.Format(tracing);
		
		// assert
		assertEquals("parent", format.transport().get("traceparent"));
		assertEquals("state", format.transport().get("tracestate"));
	}
	
	@Test
	public void should_inmemory_format_ok() {
		// setup
		DistributedTracingExtension tracing = new DistributedTracingExtension();
		tracing.setTraceparent("parent");
		tracing.setTracestate("state");
		
		// act
		ExtensionFormat format = 
				new DistributedTracingExtension.Format(tracing);
		
		// assert	
		assertEquals("distributedTracing",  format.memory().getKey());
		assertEquals(DistributedTracingExtension.class, format.memory().getValueType());
		
		assertEquals("parent", 
				((DistributedTracingExtension)format.memory().getValue()).getTraceparent());
		
		assertEquals("state", 
				((DistributedTracingExtension)format.memory().getValue()).getTracestate());
	}
}
