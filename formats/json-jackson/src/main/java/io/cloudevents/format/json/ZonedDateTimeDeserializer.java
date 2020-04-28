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
package io.cloudevents.format.json;

import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.deser.std.StdDeserializer;
import io.cloudevents.types.Time;

import java.io.IOException;
import java.time.DateTimeException;
import java.time.ZonedDateTime;

public class ZonedDateTimeDeserializer extends StdDeserializer<ZonedDateTime> {
    private static final long serialVersionUID = 1L;

    protected ZonedDateTimeDeserializer() {
        this(null);
    }

    protected ZonedDateTimeDeserializer(Class<?> vc) {
        super(vc);
    }

    @Override
    public ZonedDateTime deserialize(JsonParser jsonparser, DeserializationContext ctxt) throws IOException {
        try {
            return ZonedDateTime.parse(jsonparser.getText(), Time.RFC3339_DATE_FORMAT);
        } catch (DateTimeException e) {
            throw new IllegalArgumentException("could not parse");
        }
    }
}
