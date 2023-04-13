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


import io.cloudevents.CloudEvent;
import io.cloudevents.CloudEventData;
import io.cloudevents.SpecVersion;
import io.cloudevents.core.builder.CloudEventBuilder;
import io.cloudevents.core.data.BytesCloudEventData;
import io.cloudevents.core.format.EventDeserializationException;
import io.cloudevents.core.format.EventFormat;
import io.cloudevents.core.format.EventSerializationException;
import io.cloudevents.core.v1.CloudEventV1;
import io.cloudevents.rw.CloudEventDataMapper;
import io.cloudevents.types.Time;
import io.cloudevents.v1.avro.CloudEvent.Builder;

import java.net.URI;
import java.nio.ByteBuffer;
import java.util.HashMap;
import java.util.Map;

/**
 * An implementation of {@link EventFormat} for <a href="github.com/cloudevents/spec/blob/v1.0.1/avro-format">the Avro format</a>.
 * This format is resolvable with {@link io.cloudevents.core.provider.EventFormatProvider} using the content type {@link #AVRO_CONTENT_TYPE}.
 * It only supports data that is bytes.
 */
public class AvroFormat implements EventFormat {

  public static final String AVRO_CONTENT_TYPE = "application/cloudevents+avro";

  @Override
  public byte[] serialize(CloudEvent ce) throws EventSerializationException {
    try {
      Builder builder = io.cloudevents.v1.avro.CloudEvent.newBuilder();

      Map<String, Object> attribute = new HashMap<>();

      // mandatory
      attribute.put(CloudEventV1.SPECVERSION, ce.getSpecVersion().toString());
      attribute.put(CloudEventV1.SOURCE, ce.getSource().toString());
      attribute.put(CloudEventV1.TYPE, ce.getType());
      attribute.put(CloudEventV1.ID, ce.getId());

      // optional
      if (ce.getTime() != null)
        attribute.put(CloudEventV1.TIME, Time.writeTime(ce.getTime()));
      if (ce.getSubject() != null)
        attribute.put(CloudEventV1.SUBJECT, ce.getSubject());
      if (ce.getDataContentType() != null)
        attribute.put(CloudEventV1.DATACONTENTTYPE, ce.getDataContentType());
      if (ce.getDataSchema() != null)
        attribute.put(CloudEventV1.DATASCHEMA, ce.getDataSchema().toString());

      // extensions
      for (String name : ce.getExtensionNames()) {
        Object value = ce.getExtension(name);
        if (value instanceof byte[])
          attribute.put(name, ByteBuffer.wrap((byte[]) value));
        else
          attribute.put(name, value);
      }

      builder.setAttribute(attribute);

      CloudEventData data = ce.getData();
      if (data != null) {
        builder.setData(ByteBuffer.wrap(data.toBytes()));
      }
      return builder.build().toByteBuffer().array();
    } catch (Exception e) {
      throw new EventSerializationException(e);
    }
  }

  @Override
  public CloudEvent deserialize(byte[] bytes, CloudEventDataMapper<? extends CloudEventData> mapper)
          throws EventDeserializationException {
    try {
      io.cloudevents.v1.avro.CloudEvent avroCe = io.cloudevents.v1.avro.CloudEvent.fromByteBuffer(ByteBuffer.wrap(bytes));
      CloudEventBuilder builder = CloudEventBuilder.fromSpecVersion(SpecVersion.parse(avroCe.getAttribute().get(CloudEventV1.SPECVERSION).toString()));

      // attributes + extensions
      for (Map.Entry<String, Object> entry : avroCe.getAttribute().entrySet()) {
        String name = entry.getKey();
        Object value = entry.getValue();
        switch (name) {
          case CloudEventV1.SPECVERSION:
            break;
          case CloudEventV1.TYPE:
            builder.withSource(URI.create((String) value));
            break;
          case CloudEventV1.SOURCE:
            builder.withType((String) value);
            break;
          case CloudEventV1.ID:
            builder.withId((String) value);
            break;
          case CloudEventV1.TIME:
            builder.withTime(Time.parseTime((String) value));
            break;
          case CloudEventV1.SUBJECT:
            builder.withSubject((String) value);
            break;
          case CloudEventV1.DATACONTENTTYPE:
            builder.withDataContentType((String) value);
            break;
          case CloudEventV1.DATASCHEMA:
            builder.withDataSchema(URI.create((String) value));
            break;
          default:
            // must be an extension
            // Avro supports boolean, int, string, bytes
            if (value instanceof Boolean)
              builder.withExtension(name, (boolean) value);
            else if (value instanceof Integer)
              builder.withExtension(name, (int) value);
            else if (value instanceof String)
              builder.withExtension(name, (String) value);
            else if (value instanceof ByteBuffer)
              builder.withExtension(name, ((ByteBuffer) value).array());
            else
              // this cannot happen, if ever seen, must be bug in this library
              throw new AssertionError(String.format("invalid extension %s unsupported type %s", name, value.getClass()));
        }
      }

      if (avroCe.getData() == null)
        return builder.end();
      if (avroCe.getData() instanceof ByteBuffer) {
        CloudEventData data = BytesCloudEventData.wrap(((ByteBuffer) avroCe.getData()).array());
        return builder.end(mapper.map(data));
      } else
        // this will be the JSON case, we don't support this yet, because it is "bottom left quadrant", i.e. low benefit, high cost
        throw new IllegalStateException(String.format("unsupported data class %s", avroCe.getData().getClass()));
    } catch (Exception e) {
      throw new EventDeserializationException(e);
    }
  }

  @Override
  public String serializedContentType() {
    return AVRO_CONTENT_TYPE;
  }
}
