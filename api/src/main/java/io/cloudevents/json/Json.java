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
import com.fasterxml.jackson.databind.JavaType;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.module.SimpleModule;
import com.fasterxml.jackson.databind.type.TypeFactory;
import io.cloudevents.Attributes;
import io.cloudevents.CloudEvent;
import io.cloudevents.format.json.ZonedDateTimeDeserializer;
import io.cloudevents.format.json.ZonedDateTimeSerializer;
import io.cloudevents.fun.DataMarshaller;
import io.cloudevents.fun.DataUnmarshaller;

import java.io.InputStream;
import java.time.ZonedDateTime;
import java.util.Map;

public final class Json {

    public static final ObjectMapper MAPPER = new ObjectMapper();

    static {
        // add ZonedDateTime ser/de
        final SimpleModule module = new SimpleModule("Custom ZonedDateTime");
        module.addSerializer(ZonedDateTime.class, new ZonedDateTimeSerializer());
        module.addDeserializer(ZonedDateTime.class, new ZonedDateTimeDeserializer());
        MAPPER.registerModule(module);
    }

    public static String encode(final CloudEvent event) throws IllegalStateException {

    }

    public static byte[] encodeToBinary(final CloudEvent event) throws IllegalStateException {

    }

    public static CloudEvent decode(final byte[] binary) {

    }

    public static CloudEvent decode(final String string) {

    }

    // TODO remove all the stuff below

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
            throw new IllegalStateException("Failed to encode as JSON: " + e.getMessage());
        }
    }

    /**
     * Encode a POJO to JSON using the underlying Jackson mapper.
     *
     * @param obj a POJO
     * @return a byte array containing the JSON representation of the given POJO.
     * @throws IllegalStateException if a property cannot be encoded.
     */
    public static byte[] binaryEncode(final Object obj) throws IllegalStateException {
        try {
            return MAPPER.writeValueAsBytes(obj);
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

    public static <T> T fromInputStream(final InputStream inputStream,
    		final TypeReference<T> type) {
    	try {
            return MAPPER.readValue(inputStream, type);
        } catch (Exception e) {
            throw new IllegalStateException("Failed to encode as JSON: "
            			+ e.getMessage());
        }
    }

    /**
     * Decode a given JSON string to a POJO of the given class type.
     *
     * @param str the JSON string.
     * @param clazz the class to map to.
     * @param <T> the generic type.
     * @return an instance of T or {@code null} when {@code str} is an empty string or {@code null}
     * @throws IllegalStateException when there is a parsing or invalid mapping.
     */
    protected static <T> T decodeValue(final String str, final Class<T> clazz) throws IllegalStateException {

    	if(null!= str && !"".equals(str.trim())) {
	        try {
	            return MAPPER.readValue(str.trim(), clazz);
	        } catch (Exception e) {
	            throw new IllegalStateException("Failed to decode: " + e.getMessage());
	        }
    	}

    	return null;
    }

    protected static <T> T binaryDecodeValue(byte[] payload, final Class<T> clazz) {
    	if(null!= payload) {
    		try {
	            return MAPPER.readValue(payload, clazz);
	        } catch (Exception e) {
	            throw new IllegalStateException("Failed to decode: " + e.getMessage());
	        }
    	}
    	return null;
    }

    /**
     * Decode a given JSON string to a POJO of the given type.
     *
     * @param str the JSON string.
     * @param type the type to map to.
     * @param <T> the generic type.
     * @return an instance of T or {@code null} when {@code str} is an empty string or {@code null}
     * @throws IllegalStateException when there is a parsing or invalid mapping.
     */
    public static <T> T decodeValue(final String str, final TypeReference<T> type) throws IllegalStateException {
    	if(null!= str && !"".equals(str.trim())) {
	        try {
	            return MAPPER.readValue(str.trim(), type);
	        } catch (Exception e) {
	            throw new IllegalStateException("Failed to decode: " + e.getMessage(), e);
	        }
    	}
    	return null;
    }

    /**
     * Example of use:
     * <pre>
     * String someJson = "...";
     * Class<?> clazz = Much.class;
     *
     * Json.decodeValue(someJson, CloudEventImpl.class, clazz);
     * </pre>
     * @param str The JSON String to parse
     * @param parametrized Actual full type
     * @param parameterClasses Type parameters to apply
     * @param <T> the generic type.
     * @return An instance of T or {@code null} when {@code str} is an empty string or {@code null}
     * @see ObjectMapper#getTypeFactory
     * @see TypeFactory#constructParametricType(Class, Class...)
     */
    public static <T> T decodeValue(final String str, Class<?> parametrized,
    		Class<?>...parameterClasses) {
    	if(null!= str && !"".equals(str.trim())) {
    		 try {
    			 JavaType type =
	    			 MAPPER.getTypeFactory()
	    			 	.constructParametricType(parametrized,
	    			 			parameterClasses);

    			 return MAPPER.readValue(str.trim(), type);
 	        } catch (Exception e) {
 	            throw new IllegalStateException("Failed to decode: " + e.getMessage(), e);
 	        }
    	}
    	return null;
    }

    /**
     * Example of use:
     * <pre>
     * String someJson = "...";
     * Class<?> clazz = Much.class;
     *
     * Json.decodeValue(someJson, CloudEventImpl.class, clazz);
     * </pre>
     * @param json The JSON byte array to parse
     * @param parametrized Actual full type
     * @param parameterClasses Type parameters to apply
     * @param <T> the generic type.
     * @return An instance of T or {@code null} when {@code str} is an empty string or {@code null}
     * @see ObjectMapper#getTypeFactory
     * @see TypeFactory#constructParametricType(Class, Class...)
     */
    public static <T> T binaryDecodeValue(final byte[] json, Class<?> parametrized,
    		Class<?>...parameterClasses) {
    	if(null!= json) {
    		 try {
    			 JavaType type =
	    			 MAPPER.getTypeFactory()
	    			 	.constructParametricType(parametrized,
	    			 			parameterClasses);

    			 return MAPPER.readValue(json, type);
 	        } catch (Exception e) {
 	            throw new IllegalStateException("Failed to decode: " + e.getMessage(), e);
 	        }
    	}
    	return null;
    }

    /**
     * Creates a JSON Data Unmarshaller
     * @param <T> The 'data' type
     * @param <A> The attributes type
     * @param type The type of 'data'
     * @return A new instance of {@link DataUnmarshaller}
     */
    public static <T, A extends Attributes> DataUnmarshaller<String, T, A>
    umarshaller(Class<T> type) {
    	return (payload, attributes) -> Json.decodeValue(payload, type);
    }

    /**
     * Unmarshals a byte array into T type
     * @param <T> The 'data' type
     * @param <A> The attributes type
     * @return The data objects
     */
    public static <T, A extends Attributes> DataUnmarshaller<byte[], T, A>
    		binaryUmarshaller(Class<T> type) {
    	return (payload, attributes) -> Json.binaryDecodeValue(payload, type);
    }

    /**
     * Creates a JSON Data Marshaller that produces a {@link String}
     * @param <T> The 'data' type
     * @param <H> The type of headers value
     * @return A new instance of {@link DataMarshaller}
     */
    public static <T, H> DataMarshaller<String, T, H> marshaller() {
    	return (data, headers) -> Json.encode(data);
    }

    /**
     * Marshalls the 'data' value as JSON, producing a byte array
     * @param <T> The 'data' type
     * @param <H> The type of headers value
     * @param data The 'data' value
     * @param headers The headers
     * @return A byte array with 'data' value encoded JSON
     */
    public static <T, H> byte[] binaryMarshal(T data,
    			Map<String, H> headers) {
    	return Json.binaryEncode(data);
    }

    private Json() {
        // no-op
    }
}
