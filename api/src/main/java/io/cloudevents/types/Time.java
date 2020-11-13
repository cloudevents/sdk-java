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

import io.cloudevents.rw.CloudEventRWException;

import java.time.DateTimeException;
import java.time.OffsetDateTime;
import java.time.format.DateTimeParseException;

import static java.time.format.DateTimeFormatter.ISO_OFFSET_DATE_TIME;

/**
 * Utilities to handle the <a href="https://github.com/cloudevents/spec/blob/v1.0/spec.md#type-system">CloudEvent Attribute Timestamp type</a>
 */
public final class Time {

    private Time() {
    }

    /**
     * Parse a {@link String} RFC3339 compliant as {@link OffsetDateTime}.
     *
     * @param time the value to parse as time
     * @return the parsed {@link OffsetDateTime}
     * @throws DateTimeParseException if something went wrong when parsing the provided time.
     */
    public static OffsetDateTime parseTime(String time) throws DateTimeParseException {
        return OffsetDateTime.parse(time);
    }

    /**
     * Parse an attribute/extension with RFC3339 compliant {@link String} value as {@link OffsetDateTime}.
     *
     * @param attributeName the attribute/extension name
     * @param time          the value to parse as time
     * @return the parsed {@link OffsetDateTime}
     * @throws CloudEventRWException if something went wrong when parsing the attribute/extension.
     */
    public static OffsetDateTime parseTime(String attributeName, String time) throws CloudEventRWException {
        try {
            return parseTime(time);
        } catch (DateTimeParseException e) {
            throw CloudEventRWException.newInvalidAttributeValue(attributeName, time, e);
        }
    }

    /**
     * Convert a {@link OffsetDateTime} to a RFC3339 compliant {@link String}.
     *
     * @param time the time to write as {@link String}
     * @return the serialized time
     * @throws DateTimeException if something went wrong when serializing the provided time.
     */
    public static String writeTime(OffsetDateTime time) throws DateTimeException {
        return ISO_OFFSET_DATE_TIME.format(time);
    }

    /**
     * Convert an attribute/extension {@link OffsetDateTime} to a RFC3339 compliant {@link String}.
     *
     * @param attributeName the attribute/extension name
     * @param time          the time to write as {@link String}
     * @return the serialized time
     * @throws CloudEventRWException if something went wrong when serializing the attribute/extension.
     */
    public static String writeTime(String attributeName, OffsetDateTime time) throws DateTimeException {
        try {
            return writeTime(time);
        } catch (DateTimeParseException e) {
            throw CloudEventRWException.newInvalidAttributeValue(attributeName, time, e);
        }
    }
}
