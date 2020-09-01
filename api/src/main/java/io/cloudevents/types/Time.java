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

package io.cloudevents.types;

import java.time.OffsetDateTime;
import java.time.format.DateTimeParseException;

/**
 * Utilities to handle the <a href="https://github.com/cloudevents/spec/blob/v1.0/spec.md#type-system">CloudEvent Attribute Timestamp type</a>
 */
public final class Time {

    private Time() {
    }

    /**
     * Parse a {@link String} RFC3339 compliant as {@link OffsetDateTime}
     */
    public static OffsetDateTime parseTime(String time) throws DateTimeParseException {
        return OffsetDateTime.parse(time);
    }

    /**
     * Convert a {@link OffsetDateTime} to {@link String}
     */
    public static String writeTime(OffsetDateTime time) throws DateTimeParseException {
        return time.toString();
    }
}
