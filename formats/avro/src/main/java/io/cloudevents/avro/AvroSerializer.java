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

import java.nio.ByteBuffer;
import java.util.Map;
import java.util.HashMap;

import io.cloudevents.CloudEvent;
import io.cloudevents.CloudEventData;
import io.cloudevents.core.v1.CloudEventV1;
import io.cloudevents.avro.AvroCloudEvent;

class AvroSerializer {

    static final AvroCloudEvent toAvro(CloudEvent e) {
        AvroCloudEvent avroCloudEvent = new AvroCloudEvent();

        Map<String, Object> attrs = new HashMap<>();

        attrs.put(CloudEventV1.SPECVERSION, e.getSpecVersion().toString());
        attrs.put(CloudEventV1.TYPE, e.getType());
        attrs.put(CloudEventV1.ID, e.getId());
        attrs.put(CloudEventV1.SOURCE, e.getSource());

        if (e.getTime() != null) {
            // convert to string
            attrs.put(CloudEventV1.TIME, e.getTime().toString());
        }

        if (e.getDataSchema() != null) {
            // convert
            attrs.put(CloudEventV1.DATASCHEMA, e.getDataSchema().toString());
        }

        attrs.put(CloudEventV1.SUBJECT, e.getSubject());
        attrs.put(CloudEventV1.DATACONTENTTYPE, e.getDataContentType());

        avroCloudEvent.setAttribute(attrs);

        // check datacontenttype
        CloudEventData cloudEventData = e.getData();
        if (cloudEventData != null) {
            avroCloudEvent.setData(ByteBuffer.wrap(cloudEventData.toBytes()));
        }

        return avroCloudEvent;
    }
}
