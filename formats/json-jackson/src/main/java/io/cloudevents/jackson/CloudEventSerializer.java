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

import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.databind.SerializerProvider;
import com.fasterxml.jackson.databind.ser.std.StdSerializer;
import io.cloudevents.CloudEvent;
import io.cloudevents.CloudEventData;
import io.cloudevents.core.impl.CloudEventUtils;
import io.cloudevents.rw.CloudEventAttributesWriter;
import io.cloudevents.rw.CloudEventExtensionsWriter;
import io.cloudevents.rw.CloudEventRWException;
import io.cloudevents.rw.CloudEventReader;

import java.io.IOException;
import java.nio.charset.StandardCharsets;

/**
 * Jackson {@link com.fasterxml.jackson.databind.JsonSerializer} for {@link CloudEvent}
 */
public class CloudEventSerializer extends StdSerializer<CloudEvent> {

    private final boolean forceDataBase64Serialization;
    private final boolean forceStringSerialization;

    protected CloudEventSerializer(boolean forceDataBase64Serialization, boolean forceStringSerialization) {
        super(CloudEvent.class);
        this.forceDataBase64Serialization = forceDataBase64Serialization;
        this.forceStringSerialization = forceStringSerialization;
    }

    private static class FieldsSerializer implements CloudEventAttributesWriter, CloudEventExtensionsWriter {

        private final JsonGenerator gen;
        private final SerializerProvider provider;

        public FieldsSerializer(JsonGenerator gen, SerializerProvider provider) {
            this.gen = gen;
            this.provider = provider;
        }

        @Override
        public FieldsSerializer withAttribute(String name, String value) throws CloudEventRWException {
            try {
                gen.writeStringField(name, value);
                return this;
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        }

        @Override
        public FieldsSerializer withExtension(String name, String value) throws CloudEventRWException {
            try {
                gen.writeStringField(name, value);
                return this;
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        }

        @Override
        public FieldsSerializer withExtension(String name, Number value) throws CloudEventRWException {
            try {
                gen.writeFieldName(name);
                provider.findValueSerializer(value.getClass()).serialize(value, gen, provider);
                return this;
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        }

        @Override
        public FieldsSerializer withExtension(String name, Boolean value) throws CloudEventRWException {
            try {
                gen.writeBooleanField(name, value);
                return this;
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        }
    }

    @Override
    public void serialize(CloudEvent value, JsonGenerator gen, SerializerProvider provider) throws IOException {
        gen.writeStartObject();
        gen.writeStringField("specversion", value.getSpecVersion().toString());

        // Serialize attributes
        try {
            CloudEventReader visitable = CloudEventUtils.toVisitable(value);
            FieldsSerializer serializer = new FieldsSerializer(gen, provider);
            visitable.readAttributes(serializer);
            visitable.readExtensions(serializer);
        } catch (RuntimeException e) {
            throw (IOException) e.getCause();
        }

        // Serialize data
        if (value.getData() != null) {
            CloudEventData data = value.getData();
            if (data instanceof JsonCloudEventData) {
                gen.writeObjectField("data", ((JsonCloudEventData) data).getNode());
            } else {
                byte[] dataBytes = data.toBytes();
                String contentType = value.getDataContentType();
                if (shouldSerializeBase64(contentType)) {
                    switch (value.getSpecVersion()) {
                        case V03:
                            gen.writeStringField("datacontentencoding", "base64");
                            gen.writeFieldName("data");
                            gen.writeBinary(dataBytes);
                            break;
                        case V1:
                            gen.writeFieldName("data_base64");
                            gen.writeBinary(dataBytes);
                            break;
                    }
                } else if (JsonFormat.dataIsJsonContentType(contentType)) {
                    // TODO really bad b/c it allocates stuff, is there another solution out there?
                    char[] dataAsString = new String(dataBytes, StandardCharsets.UTF_8).toCharArray();
                    gen.writeFieldName("data");
                    gen.writeRawValue(dataAsString, 0, dataAsString.length);
                } else {
                    gen.writeFieldName("data");
                    gen.writeUTF8String(dataBytes, 0, dataBytes.length);
                }
            }
        }
        gen.writeEndObject();
    }

    private boolean shouldSerializeBase64(String contentType) {
        if (JsonFormat.dataIsJsonContentType(contentType)) {
            return this.forceDataBase64Serialization;
        } else {
            return !this.forceStringSerialization;
        }
    }

}
