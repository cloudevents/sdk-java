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

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.module.SimpleModule;
import io.cloudevents.CloudEvent;
import io.cloudevents.core.builder.CloudEventBuilder;
import io.cloudevents.core.format.EventDeserializationException;
import io.cloudevents.core.format.EventFormat;
import io.cloudevents.core.format.EventSerializationException;
import io.cloudevents.rw.CloudEventDataMapper;
import io.cloudevents.rw.CloudEventRWException;

import java.io.IOException;

/**
 * Implementation of {@link EventFormat} for <a href="https://github.com/cloudevents/spec/blob/v1.0/json-format.md">JSON event format</a>
 * using Jackson. This format is resolvable with {@link io.cloudevents.core.provider.EventFormatProvider} using the content type {@link #CONTENT_TYPE}.
 */
public final class JsonFormat implements EventFormat {

    public static final String CONTENT_TYPE = "application/cloudevents+json";

    private final ObjectMapper mapper;
    private final boolean forceDataBase64Serialization;
    private final boolean forceStringSerialization;

    public JsonFormat(boolean forceDataBase64Serialization, boolean forceStringSerialization) {
        this.mapper = new ObjectMapper();
        this.mapper.registerModule(getCloudEventJacksonModule(forceDataBase64Serialization, forceStringSerialization));
        this.forceDataBase64Serialization = forceDataBase64Serialization;
        this.forceStringSerialization = forceStringSerialization;
    }

    public JsonFormat() {
        this(false, false);
    }

    /**
     * @return a copy of this JsonFormat that serialize events with json data with Base64 encoding
     */
    public JsonFormat withForceJsonDataToBase64() {
        return new JsonFormat(true, this.forceStringSerialization);
    }

    /**
     * @return a copy of this JsonFormat that serialize events with non-json data as string
     */
    public JsonFormat withForceNonJsonDataToString() {
        return new JsonFormat(this.forceDataBase64Serialization, true);
    }

    @Override
    public byte[] serialize(CloudEvent event) throws EventSerializationException {
        try {
            return mapper.writeValueAsBytes(event);
        } catch (JsonProcessingException e) {
            throw new EventSerializationException(e);
        }
    }

    @Override
    public CloudEvent deserialize(byte[] bytes) throws EventDeserializationException {
        try {
            return mapper.readValue(bytes, CloudEvent.class);
        } catch (IOException e) {
            throw new EventDeserializationException(e);
        }
    }

    @Override
    public CloudEvent deserialize(byte[] bytes, CloudEventDataMapper mapper) throws EventDeserializationException {
        CloudEvent deserialized = this.deserialize(bytes);
        if (deserialized.getData() == null) {
            return deserialized;
        }
        try {
            return CloudEventBuilder.from(deserialized).withData(
                mapper.map(deserialized.getData())
            ).build();
        } catch (CloudEventRWException e) {
            throw new EventDeserializationException(e);
        }
    }

    @Override
    public String serializedContentType() {
        return CONTENT_TYPE;
    }

    /**
     * @return a JacksonModule with CloudEvent serializer/deserializer with default values
     */
    public static SimpleModule getCloudEventJacksonModule() {
        return getCloudEventJacksonModule(false, false);
    }

    /**
     * @return a JacksonModule with CloudEvent serializer/deserializer customizing the data serialization.
     * Look at {@link #withForceJsonDataToBase64()} and {@link #withForceNonJsonDataToString()} for more details.
     */
    public static SimpleModule getCloudEventJacksonModule(boolean forceDataBase64Serialization, boolean forceStringSerialization) {
        final SimpleModule ceModule = new SimpleModule("CloudEvent");
        ceModule.addSerializer(CloudEvent.class, new CloudEventSerializer(forceDataBase64Serialization, forceStringSerialization));
        ceModule.addDeserializer(CloudEvent.class, new CloudEventDeserializer());
        return ceModule;
    }

    protected static boolean dataIsJsonContentType(String contentType) {
        // If content type, spec states that we should assume is json
        return contentType == null || contentType.startsWith("application/json") || contentType.startsWith("text/json");
    }
}
