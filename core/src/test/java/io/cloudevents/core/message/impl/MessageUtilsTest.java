package io.cloudevents.core.message.impl;

import io.cloudevents.SpecVersion;
import io.cloudevents.core.mock.CSVFormat;
import io.cloudevents.rw.CloudEventRWException;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;

import java.util.stream.Stream;

import static io.cloudevents.SpecVersion.V03;
import static io.cloudevents.SpecVersion.V1;
import static io.cloudevents.core.message.impl.MessageUtils.parseStructuredOrBinaryMessage;
import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.*;

class MessageUtilsTest {

    @Test
    void parseStructuredOrBinaryMessage_Exception() {
        final CloudEventRWException cloudEventRWException = assertThrows(CloudEventRWException.class, () -> {
            parseStructuredOrBinaryMessage(() -> null, eventFormat -> null, () -> null, specVersion -> null);
        });
        assertThat(cloudEventRWException.getKind())
            .isEqualTo(CloudEventRWException.CloudEventRWExceptionKind.UNKNOWN_ENCODING);
    }

    @Test
    void testParseStructuredOrBinaryMessage_StructuredMode() {
        MessageUtils.parseStructuredOrBinaryMessage(() -> "application/cloudevents+csv;",
            eventFormat -> {
                assertTrue(eventFormat instanceof CSVFormat);
                return null;
            },
            () -> null, specVersion -> null);
    }

    @ParameterizedTest
    @MethodSource
    void testParseStructuredOrBinaryMessage_BinaryMode(String specVersionHeader, SpecVersion expectedSpecVersion) {
        MessageUtils.parseStructuredOrBinaryMessage(() -> null, eventFormat -> null,
            () -> specVersionHeader, specVersion -> {
                assertEquals(expectedSpecVersion, specVersion);
                return null;
            });
    }

    private static Stream<Arguments> testParseStructuredOrBinaryMessage_BinaryMode() {
        return Stream.of(
            Arguments.of("0.3", V03),
            Arguments.of("1.0", V1)
        );
    }

}
