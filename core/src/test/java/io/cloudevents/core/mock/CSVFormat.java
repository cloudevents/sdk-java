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

package io.cloudevents.core.mock;

import io.cloudevents.CloudEvent;
import io.cloudevents.SpecVersion;
import io.cloudevents.core.builder.CloudEventBuilder;
import io.cloudevents.core.format.EventFormat;
import io.cloudevents.types.Time;

import java.net.URI;
import java.nio.charset.StandardCharsets;
import java.time.ZonedDateTime;
import java.util.Base64;
import java.util.Collections;
import java.util.Objects;
import java.util.Set;
import java.util.regex.Pattern;

public class CSVFormat implements EventFormat {

    public static final CSVFormat INSTANCE = new CSVFormat();

    @Override
    public byte[] serialize(CloudEvent event) {
        return String.join(
            ",",
            event.getSpecVersion().toString(),
            event.getId(),
            event.getType(),
            event.getSource().toString(),
            Objects.toString(event.getDataContentType()),
            Objects.toString(event.getDataSchema()),
            Objects.toString(event.getSubject()),
            event.getTime() != null
                ? Time.RFC3339_DATE_FORMAT.format(event.getTime())
                : "null",
            event.getData() != null
                ? new String(Base64.getEncoder().encode(event.getData()), StandardCharsets.UTF_8)
                : "null"
        ).getBytes();
    }

    @Override
    public CloudEvent deserialize(byte[] event) {
        String[] splitted = new String(event, StandardCharsets.UTF_8).split(Pattern.quote(","));
        SpecVersion sv = SpecVersion.parse(splitted[0]);

        String id = splitted[1];
        String type = splitted[2];
        URI source = URI.create(splitted[3]);
        String datacontenttype = splitted[4].equals("null") ? null : splitted[4];
        URI dataschema = splitted[5].equals("null") ? null : URI.create(splitted[5]);
        String subject = splitted[6].equals("null") ? null : splitted[6];
        ZonedDateTime time = splitted[7].equals("null") ? null : Time.parseTime(splitted[7]);
        byte[] data = splitted[8].equals("null") ? null : Base64.getDecoder().decode(splitted[8].getBytes());

        io.cloudevents.core.v1.CloudEventBuilder builder = CloudEventBuilder.v1()
            .withId(id)
            .withType(type)
            .withSource(source);

        if (datacontenttype != null) {
            builder.withDataContentType(datacontenttype);
        }
        if (dataschema != null) {
            builder.withDataSchema(dataschema);
        }
        if (subject != null) {
            builder.withSubject(subject);
        }
        if (time != null) {
            builder.withTime(time);
        }
        if (data != null) {
            builder.withData(data);
        }
        switch (sv) {
            case V03:
                return CloudEventBuilder.v03(builder.build()).build();
            case V1:
                return builder.build();
        }
        return null;
    }

    @Override
    public Set<String> deserializableContentTypes() {
        return Collections.singleton(serializedContentType());
    }

    @Override
    public String serializedContentType() {
        return "application/cloudevents+csv";
    }
}
