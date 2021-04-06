package io.cloudevents.core.impl;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import io.cloudevents.CloudEvent;
import io.cloudevents.core.builder.CloudEventBuilder;
import io.cloudevents.core.extensions.DistributedTracingExtension;
import io.cloudevents.core.test.Data;
import org.junit.jupiter.api.Test;

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
    public void testLongExtensionNameV1() {
        assertDoesNotThrow(() -> {
            CloudEventBuilder.v1(Data.V1_WITH_JSON_DATA_WITH_EXT)
                .withExtension("thisextensionnameistoolong", "")
                .build();
        });
    }

    @Test
    public void testLongExtensionNameV03() {
        assertDoesNotThrow(() -> {
            CloudEventBuilder.v03(Data.V1_WITH_JSON_DATA_WITH_EXT)
                .withExtension("thisextensionnameistoolong", "")
                .build();
        });
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

    @Test
    public void testBinaryExtension() {

        final String EXT_NAME = "verifyme";

        CloudEvent given = CloudEventBuilder.v1(Data.V1_MIN)
            .withExtension(EXT_NAME, Data.BINARY_VALUE)
            .build();

        // Sanity
        assertNotNull(given);

        // Did the extension stick
        assertTrue(given.getExtensionNames().contains(EXT_NAME));
        assertNotNull(given.getExtension(EXT_NAME));

        // Does the extension have the right value
        assertEquals(Data.BINARY_VALUE, given.getExtension(EXT_NAME));

    }
}
