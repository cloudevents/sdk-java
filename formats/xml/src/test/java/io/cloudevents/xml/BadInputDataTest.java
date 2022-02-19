package io.cloudevents.xml;

import io.cloudevents.core.format.EventFormat;
import io.cloudevents.rw.CloudEventRWException;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import static org.assertj.core.api.Assertions.assertThatExceptionOfType;

import java.io.IOException;
import java.util.stream.Stream;

/**
 * A seperate Test set to hold the test cases related
 * to dealing with invalid representations
 */
public class BadInputDataTest {

    private final EventFormat format = new XMLFormat();

    @ParameterizedTest
    @MethodSource("badExampleFiles")
    public void verifyRejection(String fileName) throws IOException {

        byte[] data = TestUtils.getData(fileName);

        assertThatExceptionOfType(CloudEventRWException.class).isThrownBy(() -> {format.deserialize(data);});
    }

    public static Stream<Arguments> badExampleFiles() {
        return Stream.of(
            Arguments.of("bad/bad_ns.xml"),
            Arguments.of("bad/bad_no_ns.xml"),
            Arguments.of("bad/bad_missing_data_ns.xml"),
            Arguments.of("bad/bad_malformed.xml"),
            Arguments.of("bad/bad_data_ns.xml"),
            Arguments.of("bad/bad_data_content.xml")
        );
    }
}
