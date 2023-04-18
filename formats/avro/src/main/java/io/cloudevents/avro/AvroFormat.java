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

      // extensions
      Map<String, Object> attribute = new HashMap<>();
      for (String name : ce.getExtensionNames()) {
        Object value = ce.getExtension(name);
        attribute.put(name, value instanceof byte[] ? ByteBuffer.wrap((byte[]) value) : value);
      }

      builder.setSource(ce.getSource().toString())
              .setType(ce.getType())
              .setId(ce.getId())
              .setSubject(ce.getSubject())
              .setDatacontenttype(ce.getDataContentType())
              .setAttribute(attribute);

      if (ce.getTime() != null)
        builder.setTime(Time.writeTime(ce.getTime()));
      if (ce.getDataSchema() != null)
        builder.setDataschema(ce.getDataSchema().toString());

      CloudEventData data = ce.getData();
      if (data != null)
        builder.setData(ByteBuffer.wrap(data.toBytes()));
      return builder.build().toByteBuffer().array();
    } catch (Exception e) {
      throw new EventSerializationException(e);
    }
  }

  @Override
  public CloudEvent deserialize(byte[] bytes, CloudEventDataMapper<? extends CloudEventData> mapper) throws EventDeserializationException {
    try {
      io.cloudevents.v1.avro.CloudEvent avroCe = io.cloudevents.v1.avro.CloudEvent.fromByteBuffer(ByteBuffer.wrap(bytes));
      CloudEventBuilder builder = CloudEventBuilder.v1()
              .withSource(URI.create(avroCe.getSource()))
              .withType(avroCe.getType())
              .withId(avroCe.getType())
              .withSubject(avroCe.getSubject())
              .withDataContentType(avroCe.getDatacontenttype());

      if (avroCe.getTime() != null)
        builder.withTime(Time.parseTime(avroCe.getTime()));
      if (avroCe.getDataschema() != null)
        builder.withDataSchema(URI.create(avroCe.getDataschema()));

      // extensions
      for (Map.Entry<String, Object> entry : avroCe.getAttribute().entrySet()) {
        String name = entry.getKey();
        Object value = entry.getValue();
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
