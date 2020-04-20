package io.cloudevents.format.json;

import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.databind.SerializerProvider;
import com.fasterxml.jackson.databind.ser.std.StdSerializer;
import io.cloudevents.CloudEvent;
import io.cloudevents.impl.AttributesInternal;
import io.cloudevents.impl.CloudEventImpl;
import io.cloudevents.message.BinaryMessageAttributesVisitor;
import io.cloudevents.message.BinaryMessageExtensionsVisitor;
import io.cloudevents.message.MessageVisitException;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.Optional;

public class CloudEventSerializer extends StdSerializer<CloudEvent> {
    protected CloudEventSerializer() {
        super(CloudEvent.class);
    }

    private static class AttributesSerializer implements BinaryMessageAttributesVisitor {

        private JsonGenerator gen;

        public AttributesSerializer(JsonGenerator gen) {
            this.gen = gen;
        }

        @Override
        public void setAttribute(String name, String value) throws MessageVisitException {
            try {
                gen.writeStringField(name, value);
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        }
    }

    private static class ExtensionsSerializer implements BinaryMessageExtensionsVisitor {

        private JsonGenerator gen;
        private SerializerProvider provider;

        public ExtensionsSerializer(JsonGenerator gen, SerializerProvider provider) {
            this.gen = gen;
            this.provider = provider;
        }

        @Override
        public void setExtension(String name, String value) throws MessageVisitException {
            try {
                gen.writeStringField(name, value);
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        }

        @Override
        public void setExtension(String name, Number value) throws MessageVisitException {
            try {
                gen.writeFieldName(name);
                provider.findValueSerializer(value.getClass()).serialize(value, gen, provider);
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        }

        @Override
        public void setExtension(String name, Boolean value) throws MessageVisitException {
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
        Optional<byte[]> dataOptional = value.getData();
        String contentType = attributesInternal.getDataContentType().orElse(null);
        if (dataOptional.isPresent()) {
            if (JsonFormat.shouldSerializeBase64(contentType)) {
                switch (attributesInternal.getSpecVersion()) {
                    case V03:
                        gen.writeStringField("datacontentencoding", "base64");
                        gen.writeFieldName("data");
                        gen.writeBinary(dataOptional.get());
                        break;
                    case V1:
                        gen.writeFieldName("data_base64");
                        gen.writeBinary(dataOptional.get());
                        break;
                }
            } else if (JsonFormat.isJsonContentType(contentType)) {
                // TODO really bad, is there another solution out there?
                char[] data = new String(dataOptional.get(), StandardCharsets.UTF_8).toCharArray();
                gen.writeFieldName("data");
                gen.writeRawValue(data, 0, data.length);
            } else {
                byte[] data = dataOptional.get();
                gen.writeFieldName("data");
                gen.writeUTF8String(data, 0, data.length);
            }
        }
        gen.writeEndObject();
    }
}
