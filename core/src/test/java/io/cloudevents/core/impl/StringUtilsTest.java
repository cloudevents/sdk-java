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

package io.cloudevents.core.impl;

import org.assertj.core.api.Assertions;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;

import java.util.stream.Stream;

public class StringUtilsTest {

    @ParameterizedTest
    @MethodSource("startsWithIgnoreCaseArgs")
    public void startsWithIgnoreCase(final String s, final String prefix, final boolean expected) {
        Assertions.assertThat(StringUtils.startsWithIgnoreCase(s, prefix)).isEqualTo(expected);
    }

    private static Stream<Arguments> startsWithIgnoreCaseArgs() {
        return Stream.of(
            Arguments.of("s", "s", true),
            Arguments.of("sa", "S", true),
            Arguments.of("saS", "As", false),
            Arguments.of("sasso", "SASO", false),
            Arguments.of("sasso", "SaSsO", true)
        );
    }
}
