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

import java.util.Map;
import java.util.HashMap;

import io.cloudevents.CloudEvent;
import io.cloudevents.CloudEventData;
import io.cloudevents.AvroCloudEvent;
import io.cloudevents.AvroCloudEventData;

public class AvroSerializer {

    public static final AvroCloudEvent toAvro(CloudEvent e) {
        AvroCloudEvent avroCloudEvent = new AvroCloudEvent();

        Map<CharSequence, Object> attrs = new HashMap<>();

        attrs.put("type", e.getType());
        attrs.put("specversion", e.getSpecVersion().toString());
        attrs.put("id", e.getId());
        attrs.put("source", e.getSource());
        attrs.put("time", e.getTime());
        attrs.put("dataschema", e.getDataSchema());
        attrs.put("contenttype", AvroFormat.AVRO_CONTENT_TYPE);
        attrs.put("datacontenttype", e.getDataContentType());

        avroCloudEvent.setAttribute(attrs);

        // check datacontenttype
        CloudEventData cloudEventData = e.getData();
        if (cloudEventData != null) {
            avroCloudEvent.setData(cloudEventData.toBytes());
        }

        return avroCloudEvent;
    }
}
