package io.cloudevents.core.data;

import java.nio.charset.StandardCharsets;

import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

class PojoCloudEventDataTest {

    @Test
    void testWrapAndMemoization() {
        PojoCloudEventData<Integer> data = PojoCloudEventData.wrap(10, i -> i.toString().getBytes(StandardCharsets.UTF_8));

        assertThat(data.getValue())
            .isEqualTo(10);

        byte[] firstConversion = data.toBytes();

        assertThat(firstConversion)
            .isEqualTo("10".getBytes(StandardCharsets.UTF_8));

        assertThat(data.toBytes())
            .isSameAs(firstConversion);
    }

    @Test
    void testAlreadySerializedValue() {
        byte[] serialized = "10".getBytes(StandardCharsets.UTF_8);
        PojoCloudEventData<Integer> data = PojoCloudEventData.wrap(10, v -> serialized);

        assertThat(data.getValue())
            .isEqualTo(10);

        assertThat(data.toBytes())
            .isSameAs(serialized);
    }
}
