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

package io.cloudevents.avro;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;

import io.cloudevents.CloudEvent;
import io.cloudevents.CloudEventData;
import io.cloudevents.AvroCloudEvent;
import io.cloudevents.core.builder.CloudEventBuilder;
import io.cloudevents.core.format.EventDeserializationException;
import io.cloudevents.core.format.EventFormat;
import io.cloudevents.core.format.EventSerializationException;
import io.cloudevents.rw.CloudEventDataMapper;

public class AvroFormat implements EventFormat {

    public static final String AVRO_CONTENT_TYPE = "application/avro";

    @Override
    public byte[] serialize(CloudEvent event) throws EventSerializationException {
        AvroCloudEvent avroCloudEvent = AvroSerializer.toAvro(event);
        ByteArrayOutputStream output = new ByteArrayOutputStream();

        try {
            AvroCloudEvent.getEncoder().encode(avroCloudEvent, output);
        } catch (IOException e) {
            throw new EventSerializationException(e);
        }

        return output.toByteArray();
    }

    @Override
    public CloudEvent deserialize(byte[] bytes, CloudEventDataMapper<? extends CloudEventData> mapper)
        throws EventDeserializationException {
        ByteArrayInputStream input = new ByteArrayInputStream(bytes);

         try {
             AvroCloudEvent avroCloudEvent = AvroCloudEvent.getDecoder().decode(input);

             return new AvroDeserializer(avroCloudEvent).read(CloudEventBuilder::fromSpecVersion, mapper);
         } catch (IOException e) {
             throw new EventDeserializationException(e);
         }
    }

    @Override
    public String serializedContentType() {
        return AVRO_CONTENT_TYPE;
    }
}
