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
package io.cloudevents.avroturbo;


import io.cloudevents.CloudEvent;
import io.cloudevents.CloudEventData;
import io.cloudevents.core.builder.CloudEventBuilder;
import io.cloudevents.core.data.BytesCloudEventData;
import io.cloudevents.core.format.EventDeserializationException;
import io.cloudevents.core.format.EventFormat;
import io.cloudevents.core.format.EventSerializationException;
import io.cloudevents.rw.CloudEventDataMapper;
import io.cloudevents.v1.avro.CloudEvent.Builder;

import java.net.URI;
import java.nio.ByteBuffer;
import java.time.ZoneOffset;
import java.util.HashMap;
import java.util.Map;

/**
 * An implementation of {@link EventFormat} for the Avro Turbo format.
 * This format is resolvable with {@link io.cloudevents.core.provider.EventFormatProvider} using the content type {@link #AVRO_TURBO_CONTENT_TYPE}.
 */
public class AvroTurboFormat implements EventFormat {

    public static final String AVRO_TURBO_CONTENT_TYPE = "application/cloudevents+avroturbo";

    @Override
    public byte[] serialize(CloudEvent from) throws EventSerializationException {
        try {
            Builder to = io.cloudevents.v1.avro.CloudEvent.newBuilder();

            // extensions
            Map<String, Object> attribute = new HashMap<>();
            for (String name : from.getExtensionNames()) {
                Object value = from.getExtension(name);
                attribute.put(name, value instanceof byte[] ? ByteBuffer.wrap((byte[]) value) : value);
            }

            to.setSource(from.getSource().toString())
                    .setType(from.getType())
                    .setId(from.getId())
                    .setSubject(from.getSubject())
                    .setDatacontenttype(from.getDataContentType())
                    .setAttribute(attribute);

            if (from.getTime() != null)
                to.setTime(from.getTime().toInstant());
            if (from.getDataSchema() != null)
                to.setDataschema(from.getDataSchema().toString());

            CloudEventData data = from.getData();
            if (data != null)
                to.setData(ByteBuffer.wrap(data.toBytes()));
            return to.build().toByteBuffer().array();
        } catch (Exception e) {
            throw new EventSerializationException(e);
        }
    }

    @Override
    public CloudEvent deserialize(byte[] bytes, CloudEventDataMapper<? extends CloudEventData> mapper) throws EventDeserializationException {
        try {
            io.cloudevents.v1.avro.CloudEvent from = io.cloudevents.v1.avro.CloudEvent.fromByteBuffer(ByteBuffer.wrap(bytes));
            CloudEventBuilder to = CloudEventBuilder.v1()
                    .withSource(URI.create(from.getSource()))
                    .withType(from.getType())
                    .withId(from.getType())
                    .withSubject(from.getSubject())
                    .withDataContentType(from.getDatacontenttype());

            if (from.getTime() != null)
                to.withTime(from.getTime().atOffset(ZoneOffset.UTC));
            if (from.getDataschema() != null)
                to.withDataSchema(URI.create(from.getDataschema()));

            // extensions
            for (Map.Entry<String, Object> entry : from.getAttribute().entrySet()) {
                String name = entry.getKey();
                Object value = entry.getValue();
                // Avro supports boolean, int, string, bytes
                if (value instanceof Boolean)
                    to.withExtension(name, (boolean) value);
                else if (value instanceof Integer)
                    to.withExtension(name, (int) value);
                else if (value instanceof String)
                    to.withExtension(name, (String) value);
                else if (value instanceof ByteBuffer)
                    to.withExtension(name, ((ByteBuffer) value).array());
                else
                    // this cannot happen, if ever seen, must be bug in this library
                    throw new AssertionError(String.format("invalid extension %s unsupported type %s", name, value.getClass()));
            }

            if (from.getData() == null)
                return to.end();
            else {
                CloudEventData data = BytesCloudEventData.wrap(from.getData().array());
                return to.end(mapper.map(data));
            }
        } catch (Exception e) {
            throw new EventDeserializationException(e);
        }
    }

    @Override
    public String serializedContentType() {
        return AVRO_TURBO_CONTENT_TYPE;
    }
}
