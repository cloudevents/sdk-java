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

package io.cloudevents.jackson;

import com.fasterxml.jackson.databind.node.JsonNodeFactory;
import io.cloudevents.CloudEvent;
import io.cloudevents.core.builder.CloudEventBuilder;
import io.cloudevents.core.mock.MyCloudEventData;
import io.cloudevents.core.provider.EventFormatProvider;
import io.cloudevents.core.test.Data;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;

import java.util.stream.Stream;

import static org.assertj.core.api.Assertions.assertThat;

public class JsonCloudEventDataTest {

    @ParameterizedTest
    @MethodSource("textContentArguments")
    public void testMapper(String contentType) {
        CloudEvent event = CloudEventBuilder.v1(Data.V1_MIN)
            .withData(contentType, new JsonCloudEventData(JsonNodeFactory.instance.numberNode(10)))
            .build();

        byte[] serialized = EventFormatProvider.getInstance().resolveFormat(JsonFormat.CONTENT_TYPE)
            .serialize(event);

        CloudEvent deserialized = EventFormatProvider.getInstance().resolveFormat(JsonFormat.CONTENT_TYPE)
            .deserialize(serialized, data -> {
                assertThat(data)
                    .isInstanceOf(JsonCloudEventData.class);
                assertThat(((JsonCloudEventData) data).getNode().isInt())
                    .isTrue();
                return new MyCloudEventData(((JsonCloudEventData) data).getNode().asInt());
            });

        assertThat(deserialized.getData())
            .isInstanceOf(MyCloudEventData.class);
        assertThat(((MyCloudEventData) deserialized.getData()).getValue())
            .isEqualTo(10);
    }

    public static Stream<Arguments> textContentArguments() {
        return Stream.of(
            Arguments.of("application/json"),
            Arguments.of("text/json"),
            Arguments.of("application/foobar+json")
        );
    }
}
