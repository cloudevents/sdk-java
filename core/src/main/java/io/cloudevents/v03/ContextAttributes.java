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
package io.cloudevents.v03;

import java.util.Arrays;
import java.util.Set;
import java.util.stream.Collectors;

/**
 * The specification reserved words: the context attributes
 *
 * @author fabiojose
 */
public enum ContextAttributes {
    ID,
    SOURCE,
    SPECVERSION,
    TYPE,
    TIME,
    SCHEMAURL,
    DATACONTENTTYPE,
    DATACONTENTENCODING,
    SUBJECT;
    public static final Set<String> VALUES =
        Arrays.stream(ContextAttributes.values())
            .map(Enum::name)
            .map(String::toLowerCase)
            .collect(Collectors.toSet());

    public static ContextAttributes parse(String value) {
        return ContextAttributes.valueOf(value.toUpperCase());
    }

    @Override
    public String toString() {
        return name().toLowerCase();
    }
}
