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

import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.databind.SerializerProvider;
import com.fasterxml.jackson.databind.ser.std.StdSerializer;
import io.cloudevents.CloudEvent;
import io.cloudevents.CloudEventAttributesVisitor;
import io.cloudevents.CloudEventExtensionsVisitor;
import io.cloudevents.CloudEventVisitException;
import io.cloudevents.impl.AttributesInternal;
import io.cloudevents.impl.CloudEventImpl;

import java.io.IOException;
import java.nio.charset.StandardCharsets;

public class CloudEventSerializer extends StdSerializer<CloudEvent> {

    private final boolean forceDataBase64Serialization;
    private final boolean forceStringSerialization;

    protected CloudEventSerializer(boolean forceDataBase64Serialization, boolean forceStringSerialization) {
        super(CloudEvent.class);
        this.forceDataBase64Serialization = forceDataBase64Serialization;
        this.forceStringSerialization = forceStringSerialization;
    }

    private static class AttributesSerializer implements CloudEventAttributesVisitor {

        private JsonGenerator gen;

        public AttributesSerializer(JsonGenerator gen) {
            this.gen = gen;
        }

        @Override
        public void setAttribute(String name, String value) throws CloudEventVisitException {
            try {
                gen.writeStringField(name, value);
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        }
    }

    private static class ExtensionsSerializer implements CloudEventExtensionsVisitor {

        private JsonGenerator gen;
        private SerializerProvider provider;

        public ExtensionsSerializer(JsonGenerator gen, SerializerProvider provider) {
            this.gen = gen;
            this.provider = provider;
        }

        @Override
        public void setExtension(String name, String value) throws CloudEventVisitException {
            try {
                gen.writeStringField(name, value);
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        }

        @Override
        public void setExtension(String name, Number value) throws CloudEventVisitException {
            try {
                gen.writeFieldName(name);
                provider.findValueSerializer(value.getClass()).serialize(value, gen, provider);
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        }

        @Override
        public void setExtension(String name, Boolean value) throws CloudEventVisitException {
            try {
                gen.writeBooleanField(name, value);
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        }
    }

    @Override
    public void serialize(CloudEvent value, JsonGenerator gen, SerializerProvider provider) throws IOException {
        gen.writeStartObject();
        gen.writeStringField("specversion", value.getAttributes().getSpecVersion().toString());

        // Serialize attributes
        AttributesInternal attributesInternal = (AttributesInternal) value.getAttributes();
        try {
            attributesInternal.visitAttributes(new AttributesSerializer(gen));
        } catch (RuntimeException e) {
            throw (IOException) e.getCause();
        }

        // Serialize extensions
        try {
            ((CloudEventImpl) value).visitExtensions(new ExtensionsSerializer(gen, provider));
        } catch (RuntimeException e) {
            throw (IOException) e.getCause();
        }

        // Serialize data
        byte[] data = value.getData();
        String contentType = attributesInternal.getDataContentType();
        if (data != null) {
            if (shouldSerializeBase64(contentType)) {
                switch (attributesInternal.getSpecVersion()) {
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
            } else if (JsonFormat.dataIsJsonContentType(contentType)) {
                // TODO really bad b/c it allocates stuff, is there another solution out there?
                char[] dataAsString = new String(data, StandardCharsets.UTF_8).toCharArray();
                gen.writeFieldName("data");
                gen.writeRawValue(dataAsString, 0, dataAsString.length);
            } else {
                gen.writeFieldName("data");
                gen.writeUTF8String(data, 0, data.length);
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
