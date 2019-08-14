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
package io.cloudevents.json;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.module.SimpleModule;
import com.fasterxml.jackson.datatype.jdk8.Jdk8Module;
import io.cloudevents.CloudEvent;
import io.cloudevents.impl.DefaultCloudEventImpl;

import java.io.InputStream;
import java.time.ZonedDateTime;

public final class Json {

    public static final ObjectMapper MAPPER = new ObjectMapper();

    static {
        // add Jackson datatype for ZonedDateTime
        MAPPER.registerModule(new Jdk8Module());

        final SimpleModule module = new SimpleModule();
        module.addSerializer(ZonedDateTime.class, new ZonedDateTimeSerializer());
        module.addDeserializer(ZonedDateTime.class, new ZonedDateTimeDeserializer());
        MAPPER.registerModule(module);
    }

    /**
     * Encode a POJO to JSON using the underlying Jackson mapper.
     *
     * @param obj a POJO
     * @return a String containing the JSON representation of the given POJO.
     * @throws IllegalStateException if a property cannot be encoded.
     */
    public static String encode(final Object obj) throws IllegalStateException {
        try {
            return MAPPER.writeValueAsString(obj);
        } catch (Exception e) {
        	e.printStackTrace();
            throw new IllegalStateException("Failed to encode as JSON: " + e.getMessage());
        }
    }

    public static CloudEvent fromInputStream(final InputStream inputStream) {
        try {
            return MAPPER.readValue(inputStream, DefaultCloudEventImpl.class);
        } catch (Exception e) {
            throw new IllegalStateException("Failed to encode as JSON: " + e.getMessage());
        }
    }
    
    public static <T> T fromInputStream(final InputStream inputStream,
    		Class<T> clazz) {
    	try {
            return MAPPER.readValue(inputStream, clazz);
        } catch (Exception e) {
            throw new IllegalStateException("Failed to encode as JSON: " 
            			+ e.getMessage());
        }
    }
 
    /**
     * Decode a given JSON string to a CloudEvent .
     *
     * @param str the JSON string.
     * @return an instance of CloudEvent
     * @throws IllegalStateException when there is a parsing or invalid mapping.
     */
    public static DefaultCloudEventImpl decodeCloudEvent(final String str) throws IllegalStateException {
        return decodeValue(str, DefaultCloudEventImpl.class);
    }

    /**
     * Decode a given JSON string to a POJO of the given class type.
     *
     * @param str the JSON string.
     * @param clazz the class to map to.
     * @param <T> the generic type.
     * @return an instance of T
     * @throws IllegalStateException when there is a parsing or invalid mapping.
     */
    protected static <T> T decodeValue(final String str, final Class<T> clazz) throws IllegalStateException {
        try {
            return MAPPER.readValue(str, clazz);
        } catch (Exception e) {
            throw new IllegalStateException("Failed to decode: " + e.getMessage());
        }
    }

    /**
     * Decode a given JSON string to a POJO of the given type.
     *
     * @param str the JSON string.
     * @param type the type to map to.
     * @param <T> the generic type.
     * @return an instance of T
     * @throws IllegalStateException when there is a parsing or invalid mapping.
     */
    public static <T> T decodeValue(final String str, final TypeReference<T> type) throws IllegalStateException {
        try {
            return MAPPER.readValue(str, type);
        } catch (Exception e) {
            throw new IllegalStateException("Failed to decode: " + e.getMessage(), e);
        }
    }

    private Json() {
        // no-op
    }
}
