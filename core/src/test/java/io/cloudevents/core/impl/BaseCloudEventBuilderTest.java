package io.cloudevents.core.impl;

import io.cloudevents.CloudEvent;
import io.cloudevents.core.builder.CloudEventBuilder;
import io.cloudevents.core.extensions.DistributedTracingExtension;
import io.cloudevents.core.test.Data;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

public class BaseCloudEventBuilderTest {

    @Test
    public void copyAndRemoveExtension() {
        assertThat(Data.V1_WITH_JSON_DATA_WITH_EXT.getExtensionNames())
            .contains("astring");

        CloudEvent event = CloudEventBuilder.v1(Data.V1_WITH_JSON_DATA_WITH_EXT)
            .withoutExtension("astring")
            .build();

        assertThat(event.getExtensionNames())
            .doesNotContain("astring");
    }

    @Test
    public void copyAndRemoveMaterializedExtension() {
        DistributedTracingExtension ext = new DistributedTracingExtension();
        ext.setTraceparent("aaa"); // Set only traceparent

        CloudEvent given = CloudEventBuilder.v1(Data.V1_WITH_JSON_DATA_WITH_EXT)
            .withExtension(ext)
            .build();
        assertThat(given.getExtensionNames())
            .contains("traceparent")
            .doesNotContain("tracestate");

        CloudEvent event = CloudEventBuilder.v1(given)
            .withoutExtension(ext)
            .build();

        assertThat(event.getExtensionNames())
            .doesNotContain("traceparent", "tracestate");
    }

}
