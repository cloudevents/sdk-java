package io.cloudevents;

import io.cloudevents.rw.CloudEventRWException;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatCode;
import static org.junit.jupiter.api.Assertions.assertAll;

class SpecVersionTest {

    @Test
    void parse() {
        assertAll(
            () -> assertThat(SpecVersion.parse("1.0")).isEqualTo(SpecVersion.V1),
            () -> assertThat(SpecVersion.parse("0.3")).isEqualTo(SpecVersion.V03),
            () -> assertThatCode(() -> SpecVersion.parse("9000.1"))
                .hasMessage(CloudEventRWException.newInvalidSpecVersion("9000.1").getMessage())
        );
    }
}
