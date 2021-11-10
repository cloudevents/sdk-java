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

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.Map;

import io.cloudevents.avro.AvroCloudEventData;
import io.cloudevents.CloudEventData;
import io.cloudevents.core.format.EventDeserializationException;

/**
 * Encode JSON style cloudevent data into Avro format.
 *
 */
public class AvroCloudEventDataWrapper implements CloudEventData {

    private final AvroCloudEventData avroCloudEventData;

    /**
     * Wraps a JSON object-like data structure.
     */
    public AvroCloudEventDataWrapper(Map<String, Object> data) {
        avroCloudEventData = new AvroCloudEventData();
        avroCloudEventData.setValue(data);
    }

    @Override
    public byte[] toBytes() {
        try (ByteArrayOutputStream bytes = new ByteArrayOutputStream()) {
            AvroCloudEventData.getEncoder().encode(this.avroCloudEventData, bytes);
            return bytes.toByteArray();
        } catch (IOException e) {
            throw new EventDeserializationException(e);
        }
    }
}
