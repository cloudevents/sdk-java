/*
 * Copyright 2018-Present The CloudEvents Authors
 * <p>
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * <p>
 * http://www.apache.org/licenses/LICENSE-2.0
 * <p>
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 *
 */

package io.cloudevents.core.message;

import io.cloudevents.CloudEvent;
import io.cloudevents.core.builder.CloudEventBuilder;
import io.cloudevents.core.message.impl.GenericStructuredMessageReader;
import io.cloudevents.core.mock.CSVFormat;
import io.cloudevents.core.mock.MockBinaryMessageWriter;
import io.cloudevents.core.mock.MockStructuredMessageReader;
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
    @MethodSource("io.cloudevents.core.test.Data#allEventsWithoutExtensions")
    void structuredToEvent(CloudEvent input) {
        assertThat(GenericStructuredMessageReader.from(input, CSVFormat.INSTANCE).toEvent())
            // We need to make sure that on both sides the payload is in byte[] format
            .isEqualTo(CloudEventBuilder.from(input).withData(input.getData()).build());
    }

    /**
     * This test doesn't test extensions in event because the CSVFormat doesn't support it
     *
     * @param input
     */
    @ParameterizedTest()
    @MethodSource("io.cloudevents.core.test.Data#allEventsWithoutExtensions")
    void structuredToMockStructuredMessageToEvent(CloudEvent input) {
        assertThat(new MockStructuredMessageReader(input, CSVFormat.INSTANCE).toEvent())
            // We need to make sure that on both sides the payload is in byte[] format
            .isEqualTo(CloudEventBuilder.from(input).withData(input.getData()).build());
    }

    @ParameterizedTest()
    @MethodSource("io.cloudevents.core.test.Data#allEvents")
    void binaryToMockBinaryMessageToEvent(CloudEvent input) {
        assertThat(new MockBinaryMessageWriter(input).toEvent())
            // We need to make sure that on both sides the payload is in byte[] format
            .isEqualTo(CloudEventBuilder.from(input).withData(input.getData()).build());
    }

}
