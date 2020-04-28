package io.cloudevents.mock;

import io.cloudevents.CloudEvent;
import io.cloudevents.SpecVersion;
import io.cloudevents.format.EventFormat;
import io.cloudevents.types.Time;
import io.cloudevents.v1.CloudEventBuilder;

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
            event.getAttributes().getSpecVersion().toString(),
            event.getAttributes().getId(),
            event.getAttributes().getType(),
            event.getAttributes().getSource().toString(),
            Objects.toString(event.getAttributes().getDataContentType()),
            Objects.toString(event.getAttributes().getDataSchema()),
            Objects.toString(event.getAttributes().getSubject()),
            event.getAttributes().getTime() != null
                ? Time.RFC3339_DATE_FORMAT.format(event.getAttributes().getTime())
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

        CloudEventBuilder builder = CloudEvent.buildV1()
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
                return builder.build().toV03();
            case V1:
                return builder.build().toV1();
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
