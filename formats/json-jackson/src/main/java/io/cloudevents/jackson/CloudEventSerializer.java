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
import io.cloudevents.SpecVersion;
import io.cloudevents.core.impl.CloudEventUtils;
import io.cloudevents.core.provider.EventDataCodecProvider;
import io.cloudevents.rw.CloudEventAttributesWriter;
import io.cloudevents.rw.CloudEventExtensionsWriter;
import io.cloudevents.rw.CloudEventRWException;
import io.cloudevents.rw.CloudEventReader;

import java.io.IOException;

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
        public void setAttribute(String name, String value) throws CloudEventRWException {
            try {
                gen.writeStringField(name, value);
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        }

        @Override
        public void setExtension(String name, String value) throws CloudEventRWException {
            try {
                gen.writeStringField(name, value);
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        }

        @Override
        public void setExtension(String name, Number value) throws CloudEventRWException {
            try {
                gen.writeFieldName(name);
                provider.findValueSerializer(value.getClass()).serialize(value, gen, provider);
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        }

        @Override
        public void setExtension(String name, Boolean value) throws CloudEventRWException {
            try {
                gen.writeBooleanField(name, value);
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        }
    }

    @Override
    public void serialize(CloudEvent value, JsonGenerator gen, SerializerProvider provider) throws IOException {
        SpecVersion specVersion = value.getSpecVersion();

        gen.writeStartObject();
        gen.writeStringField("specversion", specVersion.toString());

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
        Object data = value.getRawData();
        String contentType = value.getDataContentType();
        if (data != null) {
            if (data instanceof byte[]) {
                writeBase64Data(gen, specVersion, (byte[]) data);
            } else if (Json.dataIsJsonContentType(contentType) && !shouldSerializeBase64(contentType)) {
                // Let JsonGenerator serialize the pojo
                gen.writeObjectField("data", data);
            } else {
                byte[] bytes = EventDataCodecProvider.getInstance().serialize(contentType, data);
                if (shouldSerializeBase64(contentType)) {
                    writeBase64Data(gen, specVersion, bytes);
                } else {
                    gen.writeFieldName("data");
                    gen.writeUTF8String(bytes, 0, bytes.length);
                }
            }
        }
        gen.writeEndObject();
    }

    private boolean shouldSerializeBase64(String contentType) {
        if (Json.dataIsJsonContentType(contentType)) {
            return this.forceDataBase64Serialization;
        } else {
            return !this.forceStringSerialization;
        }
    }

    private void writeBase64Data(JsonGenerator gen, SpecVersion specVersion, byte[] data) throws IOException {
        switch (specVersion) {
            case V03:
                gen.writeStringField("datacontentencoding", "base64");
                gen.writeFieldName("data");
                gen.writeBinary(data);
                break;
            case V1:
                gen.writeFieldName("data_base64");
                gen.writeBinary(data);
                break;
        }
    }

}
