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
 */

package io.cloudevents.types;


import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;

import java.time.LocalDateTime;
import java.time.OffsetDateTime;
import java.time.ZoneOffset;
import java.util.stream.Stream;

import static org.assertj.core.api.Assertions.assertThat;

public class TimeTest {

    @ParameterizedTest
    @MethodSource("parseDateArguments")
    void testParseAndFormatDate(String ts) {
        OffsetDateTime offsetDateTime = Time.parseTime(ts);
        assertThat(ts).isEqualTo(offsetDateTime.toString());
    }

    @Test
    void testParseDateOffset() {
        assertThat(Time.parseTime("1937-01-01T12:20:27.87+00:20"))
            .isEqualTo("1937-01-01T12:00:27.87Z");
    }

    @Test
    void testSerializeDateOffset() {
        assertThat(Time.writeTime(OffsetDateTime.of(
            LocalDateTime.of(2020, 8, 3, 18, 10, 0, 0),
            ZoneOffset.ofHours(2)
        ))).isEqualTo("2020-08-03T18:10+02:00");
    }

    public static Stream<Arguments> parseDateArguments() {
        return Stream.of(
            Arguments.of("1985-04-12T23:20:50.520Z"),
            Arguments.of("1990-12-31T23:59Z"),
            Arguments.of("1990-12-31T15:59-08:00"),
            Arguments.of("1937-01-01T12:00:27.870+00:20")
        );
    }
}
