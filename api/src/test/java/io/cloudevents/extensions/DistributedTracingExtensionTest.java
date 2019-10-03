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
