package io.cloudevents.core.impl;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import org.junit.jupiter.api.Test;

import io.cloudevents.CloudEvent;
import io.cloudevents.core.builder.CloudEventBuilder;
import io.cloudevents.core.extensions.DistributedTracingExtension;
import io.cloudevents.core.test.Data;

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

        CloudEvent have = CloudEventBuilder.v1(given)
            .withoutExtension(ext)
            .build();

        assertThat(have.getExtensionNames())
            .doesNotContain("traceparent", "tracestate");
        assertThat(Data.V1_WITH_JSON_DATA_WITH_EXT)
            .isEqualTo(have);
    }

    @Test
    public void testLongExtensionName() {
        Exception exception = assertThrows(RuntimeException.class, () -> {
            CloudEvent cloudEvent = CloudEventBuilder.v1(Data.V1_WITH_JSON_DATA_WITH_EXT)
                .withExtension("thisextensionnameistoolong", "")
                .build();
        });
        String expectedMessage = "Invalid extensions name: thisextensionnameistoolong";
        String actualMessage = exception.getMessage();

        assertTrue(actualMessage.contains(expectedMessage));
    }
    @Test
    public void testInvalidExtensionName() {
        Exception exception = assertThrows(RuntimeException.class, () -> {
            CloudEvent cloudEvent = CloudEventBuilder.v1(Data.V1_WITH_JSON_DATA_WITH_EXT)
                .withExtension("ExtensionName", "")
                .build();
        });
        String expectedMessage = "Invalid extensions name: ExtensionName";
        String actualMessage = exception.getMessage();

        assertTrue(actualMessage.contains(expectedMessage));
    }
}
