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

package io.cloudevents.protobuf;

import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;

import java.io.IOException;
import java.util.stream.Stream;

import static org.assertj.core.api.Assertions.assertThat;

public class ProtoSupportTest {

    @ParameterizedTest
    @MethodSource("textContentArguments")
    public void serialize(String contentType, boolean isText) throws IOException {

        assertThat(isText).isEqualTo(ProtoSupport.isTextContent(contentType));
    }

    // Test Data set for contentType text determination.
    public static Stream<Arguments> textContentArguments() {
        return Stream.of(
            Arguments.of("application/json", true),
            Arguments.of("application/xml", true),
            Arguments.of("text/plain", true),
            Arguments.of("application/protobuf", false),
            Arguments.of("application/fubar+xml", true),
            Arguments.of("application/fubar+json", true)
        );
    }
}
