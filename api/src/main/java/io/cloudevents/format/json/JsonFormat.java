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
package io.cloudevents.format.json;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.module.SimpleModule;
import io.cloudevents.CloudEvent;
import io.cloudevents.format.EventFormat;

import java.time.ZonedDateTime;
import java.util.Collections;
import java.util.Set;

public final class JsonFormat implements EventFormat {

    public static final ObjectMapper MAPPER = new ObjectMapper();

    static {
        // add ZonedDateTime ser/de
        final SimpleModule module = new SimpleModule("Custom ZonedDateTime");
        module.addSerializer(ZonedDateTime.class, new ZonedDateTimeSerializer());
        module.addDeserializer(ZonedDateTime.class, new ZonedDateTimeDeserializer());
        MAPPER.registerModule(module);
    }


    private static class SingletonContainer {
        private final static JsonFormat INSTANCE = new JsonFormat();
    }

    public static JsonFormat getInstance() {
        return JsonFormat.SingletonContainer.INSTANCE;
    }

    private JsonFormat() {}

    @Override
    public byte[] serializeToBytes(CloudEvent event) {
        return new byte[0];
    }

    @Override
    public String serializeToString(CloudEvent event) {
        return null;
    }

    @Override
    public CloudEvent deserialize(byte[] event) {
        return null;
    }

    @Override
    public CloudEvent deserialize(String event) {
        return null;
    }

    @Override
    public Set<String> supportedContentTypes() {
        return Collections.singleton("application/cloudevents+json");
    }
}
