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

import io.cloudevents.CloudEvent;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

/**
 *
 * @author fabiojose
 *
 */
public class DistributedTracingExtensionTest {

	@Test
	public void writeExtension() {
		DistributedTracingExtension tracing = new DistributedTracingExtension();
		tracing.setTraceparent("parent");
		tracing.setTracestate("state");

        CloudEvent event = CloudEvent.build().build();
        tracing.writeToEvent(event);

        assertThat(event.getExtensions())
            .containsEntry(DistributedTracingExtension.TRACEPARENT, "parent")
            .containsEntry(DistributedTracingExtension.TRACESTATE, "state");
	}

	@Test
	public void parseExtension() {
        CloudEvent event = CloudEvent.build()
            .withExtension(DistributedTracingExtension.TRACEPARENT, "parent")
            .withExtension(DistributedTracingExtension.TRACESTATE, "state")
            .build();

        DistributedTracingExtension tracing = ExtensionsParser
            .getInstance()
            .parseExtension(DistributedTracingExtension.class, event);

        assertThat(tracing).isNotNull();
        assertThat(tracing.getTraceparent()).isEqualTo("parent");
        assertThat(tracing.getTracestate()).isEqualTo("state");

	}
}
