/**
 * Copyright 2018 The CloudEvents Authors
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package io.cloudevents.util;


import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.deser.std.StdDeserializer;
import com.fasterxml.jackson.databind.module.SimpleModule;
import com.fasterxml.jackson.datatype.jdk8.Jdk8Module;
import io.cloudevents.CloudEvent;
import io.cloudevents.impl.DefaultCloudEventImpl;

import java.io.IOException;
import java.io.InputStream;
import java.time.DateTimeException;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;
import java.util.logging.Logger;


public final class JacksonMapper {

    private static final ObjectMapper MAPPER = new ObjectMapper();
    private static final Logger LOGGER = Logger.getLogger(JacksonMapper.class.getName());

    static {
        MAPPER.registerModule(new Jdk8Module());

        SimpleModule module = new SimpleModule();
        // custom types
        module.addDeserializer(ZonedDateTime.class, new TestZonedDateTimeDeserializer());
        MAPPER.registerModule(module);
    }

    public static CloudEvent fromInputStream(final InputStream inputStream) {
        try {
            return MAPPER.readValue(inputStream, DefaultCloudEventImpl.class);
        } catch (IOException e) {
            LOGGER.severe(e.getMessage());
            throw new IllegalStateException("input was not parseable", e);
        }
    }

    private static class TestZonedDateTimeDeserializer extends StdDeserializer<ZonedDateTime> {

        public TestZonedDateTimeDeserializer() {
            this(null);
        }

        public TestZonedDateTimeDeserializer(Class<?> vc) {
            super(vc);
        }

        @Override
        public ZonedDateTime deserialize(JsonParser jsonparser, DeserializationContext ctxt) throws IOException {
            // not serializing timezone data yet
            try {
                return ZonedDateTime.parse(jsonparser.getText(), DateTimeFormatter.ISO_OFFSET_DATE_TIME);
            } catch (DateTimeException e) {
                throw new IllegalArgumentException("could not parse");
            }
        }
    }
}
