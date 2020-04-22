package io.cloudevents.message;

import io.cloudevents.CloudEvent;
import io.cloudevents.mock.CSVFormat;
import io.cloudevents.mock.MockBinaryMessage;
import io.cloudevents.mock.MockStructuredMessage;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.MethodSource;

import static org.assertj.core.api.Assertions.assertThat;

public class EventMessageRoundtripTest {

    /**
     * This test doesn't test extensions in event because the CSVFormat doesn't support it
     *
     * @param input
     */
    @ParameterizedTest()
    @MethodSource("io.cloudevents.test.Data#allEventsWithoutExtensions")
    void structuredToEvent(CloudEvent input) {
        assertThat(input.asStructuredMessage(CSVFormat.INSTANCE).toEvent())
            .isEqualTo(input);
    }

    /**
     * This test doesn't test extensions in event because the CSVFormat doesn't support it
     *
     * @param input
     */
    @ParameterizedTest()
    @MethodSource("io.cloudevents.test.Data#allEventsWithoutExtensions")
    void structuredToMockStructuredMessageToEvent(CloudEvent input) {
        assertThat(input.asStructuredMessage(CSVFormat.INSTANCE).visit(new MockStructuredMessage()).toEvent())
            .isEqualTo(input);
    }

    @ParameterizedTest()
    @MethodSource("io.cloudevents.test.Data#allEvents")
    void binaryToEvent(CloudEvent input) {
        assertThat(input.asBinaryMessage().toEvent())
            .isEqualTo(input);
    }

    @ParameterizedTest()
    @MethodSource("io.cloudevents.test.Data#allEvents")
    void binaryToMockBinaryMessageToEvent(CloudEvent input) {
        assertThat(input.asBinaryMessage().visit(new MockBinaryMessage()).toEvent())
            .isEqualTo(input);
    }

}
